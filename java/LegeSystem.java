import java.util.*;
import java.io.*;

public class LegeSystem {
    IndeksertListe<Legemiddel> legemidler;
    IndeksertListe<Resept> resepter;
    IndeksertListe<Lege> leger;
    IndeksertListe<Pasient> pasienter;
    private static LegeSystem legesystem;
    // Single shared scanner to avoid closing System.in prematurely
    private static final Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

    public LegeSystem() {
        legemidler = new IndeksertListe<>();
        resepter = new IndeksertListe<>();
        leger = new IndeksertListe<>();
        pasienter = new IndeksertListe<>();
    }

    public static void main(String[] args) throws FileNotFoundException, UlovligUtskrift {
        legesystem = new LegeSystem();
        legesystem.LesInnFil("legedata.txt");
        grenseSnitt();
    }

    public IndeksertListe<Legemiddel> hentLegemidler() {
        return legemidler;
    }

    public IndeksertListe<Lege> hentLeger() {
        return leger;
    }

    public IndeksertListe<Pasient> hentPasienter() {
        return pasienter;
    }

    public IndeksertListe<Resept> hentResepter() {
        return resepter;
    }

    // Main menu loop - no recursion, uses a while loop
    public static void grenseSnitt() {
        int input = 0;
        while (input != 5) {
            System.out.println("Please select an option:");
            System.out.println("1: Print a complete overview of patients, doctors, medicines and prescriptions.");
            System.out.println("2: Create and add new elements to the system.");
            System.out.println("3: Use a given prescription from the list for a patient.");
            System.out.println("4: Print various forms of statistics.");
            System.out.println("5: Exit");
            input = scanner.nextInt();
            if (input == 1) {
                printInfo();
            }
            if (input == 2) {
                System.out.println("Please select an option.");
                System.out.println("1. Add a doctor.");
                System.out.println("2. Add a medicine.");
                System.out.println("3. Add a patient.");
                System.out.println("4. Add a prescription.");
                System.out.println("5. Back to the menu.");
                int nyinput = scanner.nextInt();
                if (nyinput == 1) {
                    leggTilLege();
                }
                if (nyinput == 2) {
                    leggTilLegemiddel();
                }
                if (nyinput == 3) {
                    leggTilPasient();
                }
                if (nyinput == 4) {
                    leggTilResept();
                }
                // nyinput == 5: fall through, while loop re-displays main menu
            }
            if (input == 3) {
                brukResept();
            }
            if (input == 4) {
                skrivStatistikk();
            }
        }
    }

    public void LesInnFil(String filnavn) throws FileNotFoundException, UlovligUtskrift {
        File fil = new File(filnavn);
        // try-with-resources ensures fscan is always closed
        try (Scanner fscan = new Scanner(fil)) {
            // Leser inn pasienter
            if (fscan.hasNextLine() && fscan.nextLine().equals("# Pasienter (navn, fnr)")) {
                while (fscan.hasNextLine()) {
                    try {
                        String[] linje = fscan.nextLine().split(",");
                        if (linje[0].equals("# Legemidler (navn")) {
                            break;
                        }
                        Pasient pasient = new Pasient(linje[0], linje[1]);
                        pasienter.leggTil(pasient);
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        System.out.println("Pasient: " + e);
                    }
                }
            } else {
                System.out.println("Advarsel: Ugyldig filformat - mangler pasient-seksjon.");
            }
            // Leser inn legemidler
            while (fscan.hasNextLine()) {
                try {
                    String[] linje = fscan.nextLine().split(",");
                    if (linje[0].equals("# Leger (navn")) {
                        break;
                    }
                    if (linje[1].equals("narkotisk")) {
                        int nummer = Integer.parseInt(linje[2]);
                        double nummer1 = Double.parseDouble(linje[3]);
                        int nummer2 = Integer.parseInt(linje[4]);
                        Narkotisk narkotisk = new Narkotisk(linje[0], nummer, nummer1, nummer2);
                        legemidler.leggTil(narkotisk);
                    }
                    if (linje[1].equals("vanedannende")) {
                        int nummer = Integer.parseInt(linje[2]);
                        double nummer1 = Double.parseDouble(linje[3]);
                        int nummer2 = Integer.parseInt(linje[4]);
                        Vanedannende vanedannende = new Vanedannende(linje[0], nummer, nummer1, nummer2);
                        legemidler.leggTil(vanedannende);
                    }
                    if (linje[1].equals("vanlig")) {
                        int nummer = Integer.parseInt(linje[2]);
                        double nummer1 = Double.parseDouble(linje[3]);
                        Vanlig vanligLegemiddel = new Vanlig(linje[0], nummer, nummer1);
                        legemidler.leggTil(vanligLegemiddel);
                    }
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    System.out.println("Legemidler: " + e);
                }
            }
            // Leser inn leger
            while (fscan.hasNextLine()) {
                try {
                    String[] linje = fscan.nextLine().split(",");
                    if (linje[0].equals("# Resepter (legemiddelNummer")) {
                        break;
                    }
                    int kontrollId = Integer.parseInt(linje[1]);
                    if (kontrollId == 0) {
                        Lege lege = new Lege(linje[0]);
                        leger.leggTil(lege);
                    } else if (kontrollId > 0) {
                        Specialist spesialist = new Specialist(linje[0], Integer.toString(kontrollId));
                        leger.leggTil(spesialist);
                    }
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    System.out.println("Leger: " + e);
                }
            }
            // Leser inn resepter
            while (fscan.hasNextLine()) {
                try {
                    String[] linje = fscan.nextLine().split(",");
                    int legemiddelId = Integer.parseInt(linje[0]);
                    String legeNavn = linje[1];
                    String reseptType = linje[3];
                    int pasientId = Integer.parseInt(linje[2]);
                    Legemiddel legemiddel = legemidler.hent(legemiddelId);
                    Lege legen = null;
                    for (Lege lege : leger) {
                        if (lege.hentNavn().equals(legeNavn)) {
                            legen = lege;
                        }
                    }
                    // Skip prescription if doctor not found instead of crashing with NPE
                    if (legen == null) {
                        System.out.println("Resept: Fant ikke lege med navn '" + legeNavn + "', hopper over resept.");
                        continue;
                    }
                    Pasient pasient = pasienter.hent(pasientId);
                    if (reseptType.equals("hvit")) {
                        int reit = Integer.parseInt(linje[4]);
                        HvitResept resept = legen.skrivHvitResept(legemiddel, pasient, reit);
                        resepter.leggTil(resept);
                    }
                    if (reseptType.equals("blaa")) {
                        int reit = Integer.parseInt(linje[4]);
                        BlaaResept resept = legen.skrivBlaaResept(legemiddel, pasient, reit);
                        resepter.leggTil(resept);
                    }
                    if (reseptType.equals("militaer")) {
                        MilResept resept = legen.skrivMilResept(legemiddel, pasient);
                        resepter.leggTil(resept);
                    }
                    if (reseptType.equals("p")) {
                        int reit = Integer.parseInt(linje[4]);
                        PResept resept = legen.skrivPResept(legemiddel, pasient, reit);
                        resepter.leggTil(resept);
                    }
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException | UgyldigListeindeks e) {
                    System.out.println("Resept: " + e);
                }
            }
        }
    }

    public static void printInfo() {
        System.out.println("Legemidler i listen: ");
        Liste<Legemiddel> legemidler = legesystem.hentLegemidler();
        for (Legemiddel legemiddel : legemidler) {
            System.out.println(legemiddel);
        }
        System.out.println("Leger i listen, sortert: ");
        Liste<Lege> leger = legesystem.hentLeger();
        ArrayList<Lege> sorterteLeger = new ArrayList<>();
        for (Lege lege : leger) {
            boolean sattInn = false;
            int teller = 0;
            while (teller < sorterteLeger.size() && !sattInn) {
                if (lege.compareTo(sorterteLeger.get(teller)) < 0) {
                    sorterteLeger.add(teller, lege);
                    sattInn = true;
                }
                teller++;
            }
            if (!sattInn) {
                sorterteLeger.add(lege);
            }
        }
        for (Lege lega : sorterteLeger) {
            System.out.println(lega);
        }
        System.out.println("Pasienter i listen:");
        Liste<Pasient> pasienter = legesystem.hentPasienter();
        for (Pasient pasient : pasienter) {
            System.out.println(pasient);
        }
        System.out.println("Resepter i listen:");
        Liste<Resept> resepter = legesystem.hentResepter();
        for (Resept resept : resepter) {
            System.out.println(resept);
        }
    }

    // Sub-methods no longer call grenseSnitt() recursively - they just return
    public static void leggTilLege() {
        try {
            scanner.nextLine(); // consume leftover newline from previous nextInt
            System.out.println("Tast inn navn: ");
            String navn = scanner.nextLine();
            System.out.println("Tast inn kontrollId: ");
            String id = scanner.nextLine();
            int kontrollId = Integer.parseInt(id);
            if (kontrollId > 0) {
                Specialist specialist = new Specialist(navn, id);
                legesystem.leger.leggTil(specialist);
                System.out.println("Legespesialist lagt inn i systemet.");
            } else if (kontrollId < 0) {
                System.out.println("KontrollId må være positiv eller 0.");
            } else {
                Lege lege = new Lege(navn);
                legesystem.leger.leggTil(lege);
                System.out.println("Lege lagt inn i systemet.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ikke gyldig input.");
        }
    }

    public static void leggTilLegemiddel() {
        try {
            scanner.nextLine(); // consume leftover newline from previous nextInt
            System.out.println("Skriv inn legemiddel type: vanedannende, narkotisk, vanlig: ");
            String type = scanner.nextLine();
            if (type.equals("vanedannende")) {
                System.out.println("Tast inn navn pa legemiddelet: ");
                String navn = scanner.nextLine();
                System.out.println("Tast inn pris pa legemiddelet: ");
                int pris = scanner.nextInt();
                System.out.println("Tast inn virkestoff pa legemiddelet: ");
                double virkestoff = scanner.nextDouble();
                System.out.println("Tast inn styrke pa legemiddelet: ");
                int styrke = scanner.nextInt();
                Vanedannende vanedannende = new Vanedannende(navn, pris, virkestoff, styrke);
                legesystem.legemidler.leggTil(vanedannende);
                System.out.println("Vanedannende legemiddel lagt inn.");
            }
            if (type.equals("narkotisk")) {
                System.out.println("Tast inn navn pa legemiddelet: ");
                String navn = scanner.nextLine();
                System.out.println("Tast inn pris pa legemiddelet: ");
                int pris = scanner.nextInt();
                System.out.println("Tast inn virkestoff pa legemiddelet: ");
                double virkestoff = scanner.nextDouble();
                System.out.println("Tast inn styrke pa legemiddelet: ");
                int styrke = scanner.nextInt();
                Narkotisk narkotisk = new Narkotisk(navn, pris, virkestoff, styrke);
                legesystem.legemidler.leggTil(narkotisk);
                System.out.println("Narkotisk legemiddel lagt inn.");
            }
            if (type.equals("vanlig")) {
                System.out.println("Skriv navn på legemiddelet: ");
                String navn = scanner.nextLine();
                System.out.println("Skriv pris på legemiddelet: ");
                int pris = scanner.nextInt();
                System.out.println("Skriv virkestoff på legemiddelet: ");
                double virkestoff = scanner.nextDouble();
                Vanlig vanligLegemiddel = new Vanlig(navn, pris, virkestoff);
                legesystem.legemidler.leggTil(vanligLegemiddel);
                System.out.println("Vanlig legemiddel lagt inn.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Ugyldig input.");
        }
    }

    public static void leggTilPasient() {
        try {
            scanner.nextLine(); // consume leftover newline from previous nextInt
            System.out.println("Skriv navn til pasienten: ");
            String navn = scanner.nextLine();
            System.out.println("Skriv inn fodselsnummer til pasienten: ");
            String nr = scanner.nextLine();
            Pasient pasient = new Pasient(navn, nr);
            legesystem.pasienter.leggTil(pasient);
            System.out.println("Pasient lagt inn i systemet.");
        } catch (InputMismatchException e) {
            System.out.println("Ugyldig input");
        }
    }

    public static void leggTilResept() {
        try {
            scanner.nextLine(); // consume leftover newline from previous nextInt
            System.out.println("Tast inn hvilken type resept du vil lage(hvit, blaa, militaer, p): ");
            String type = scanner.nextLine();
            if (type.equals("hvit")) {
                for (Legemiddel legemiddel : legesystem.legemidler) {
                    System.out.println(legemiddel);
                }
                System.out.println("Tast inn Id til legemiddelet du vil ha resept for: ");
                int id = scanner.nextInt();
                Legemiddel legemiddel = legesystem.legemidler.hent(id);
                for (Pasient pasienter : legesystem.pasienter) {
                    System.out.println(pasienter);
                }
                System.out.println("Tast inn Id til pasienten du vil opprette resept til: ");
                int pasientId = scanner.nextInt();
                scanner.nextLine();
                Pasient pasient = legesystem.pasienter.hent(pasientId);
                for (Lege leger : legesystem.leger) {
                    System.out.println(leger);
                }
                System.out.println("Tast inn navn pa legen som oppretter reseptet: ");
                Lege legen = null;
                String navn = scanner.nextLine();
                for (Lege lege : legesystem.leger) {
                    if (lege.hentNavn().equals(navn)) {
                        legen = lege;
                    }
                }
                if (legen == null) {
                    System.out.println("Fant ikke lege med navn: " + navn);
                    return;
                }
                System.out.println("Tast inn antall reit pa reseptet: ");
                int reit = scanner.nextInt();
                Resept resept = legen.skrivHvitResept(legemiddel, pasient, reit);
                legesystem.resepter.leggTil(resept);
                System.out.println("Resept lagt til i systemet.");
            }
            if (type.equals("blaa")) {
                for (Legemiddel legemiddel : legesystem.legemidler) {
                    System.out.println(legemiddel);
                }
                System.out.println("Tast inn Id til legemiddelet du vil ha resept for: ");
                int Id = scanner.nextInt();
                Legemiddel legemiddel = legesystem.legemidler.hent(Id);
                for (Pasient pasienter : legesystem.pasienter) {
                    System.out.println(pasienter);
                }
                System.out.println("Tast inn Id til pasienten du vil opprette resept til: ");
                int pasientId = scanner.nextInt();
                scanner.nextLine();
                Pasient pasient = legesystem.pasienter.hent(pasientId);
                for (Lege leger : legesystem.leger) {
                    System.out.println(leger);
                }
                System.out.println("Tast inn navn pa legen som oppretter reseptet: ");
                Lege legen = null;
                String navn = scanner.nextLine();
                for (Lege lege : legesystem.leger) {
                    if (lege.hentNavn().equals(navn)) {
                        legen = lege;
                    }
                }
                if (legen == null) {
                    System.out.println("Fant ikke lege med navn: " + navn);
                    return;
                }
                System.out.println("Tast inn antall reit pa reseptet: ");
                int reit = scanner.nextInt();
                Resept resept = legen.skrivBlaaResept(legemiddel, pasient, reit);
                legesystem.resepter.leggTil(resept);
                System.out.println("Resept lagt til i systemet.");
            }
            if (type.equals("militaer")) {
                for (Legemiddel legemiddel : legesystem.legemidler) {
                    System.out.println(legemiddel);
                }
                System.out.println("Tast inn Id til legemiddelet du vil ha resept for: ");
                int Id = scanner.nextInt();
                Legemiddel legemiddel = legesystem.legemidler.hent(Id);
                for (Pasient pasienter : legesystem.pasienter) {
                    System.out.println(pasienter);
                }
                System.out.println("Tast inn Id'en til pasienten du vil opprette resept til: ");
                int pasientId = scanner.nextInt();
                scanner.nextLine();
                Pasient pasient = legesystem.pasienter.hent(pasientId);
                for (Lege leger : legesystem.leger) {
                    System.out.println(leger);
                }
                System.out.println("Tast inn navn pa legen som oppretter reseptet: ");
                Lege legen = null;
                String navn = scanner.nextLine();
                for (Lege lege : legesystem.leger) {
                    if (lege.hentNavn().equals(navn)) {
                        legen = lege;
                    }
                }
                if (legen == null) {
                    System.out.println("Fant ikke lege med navn: " + navn);
                    return;
                }
                Resept resept = legen.skrivMilResept(legemiddel, pasient);
                legesystem.resepter.leggTil(resept);
                System.out.println("Resept lagt til i systemet.");
            }
            if (type.equals("p")) {
                for (Legemiddel legemiddel : legesystem.legemidler) {
                    System.out.println(legemiddel);
                }
                System.out.println("Tast inn Id til legemiddelet du vil ha resept for: ");
                int Id = scanner.nextInt();
                Legemiddel legemiddel = legesystem.legemidler.hent(Id);
                for (Pasient pasienter : legesystem.pasienter) {
                    System.out.println(pasienter);
                }
                System.out.println("Tast inn Id'en til pasienten du vil opprette resept til: ");
                int pasientId = scanner.nextInt();
                scanner.nextLine();
                Pasient pasient = legesystem.pasienter.hent(pasientId);
                for (Lege leger : legesystem.leger) {
                    System.out.println(leger);
                }
                System.out.println("Tast inn navn pa legen som oppretter reseptet: ");
                Lege legen = null;
                String navn = scanner.nextLine();
                for (Lege lege : legesystem.leger) {
                    if (lege.hentNavn().equals(navn)) {
                        legen = lege;
                    }
                }
                if (legen == null) {
                    System.out.println("Fant ikke lege med navn: " + navn);
                    return;
                }
                System.out.println("Tast inn antall reit pa reseptet: ");
                int reit = scanner.nextInt();
                Resept resept = legen.skrivPResept(legemiddel, pasient, reit);
                legesystem.resepter.leggTil(resept);
                System.out.println("Resept lagt til i systemet.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Ugyldig input.");
        } catch (UlovligUtskrift e) {
            System.out.println("Legen kan ikke gi ut narkotisk legemiddel.");
        } catch (UgyldigListeindeks e) {
            System.out.println("Ugyldig indeks: " + e.getMessage());
        }
    }

    public static void brukResept() {
        try {
            for (Pasient pasienter : legesystem.pasienter) {
                System.out.println(pasienter);
            }
            System.out.println("Hvilken pasient vil du se resepter for? (id): ");
            int pasientID = scanner.nextInt();
            Pasient pasient = legesystem.pasienter.hent(pasientID);
            System.out.println("Valgt " + pasient);
            for (Resept resepter : pasient.reseptListe) {
                System.out.println(resepter);
            }
            System.out.println("Hvilken resept vil du bruke? (id): ");
            int reseptID = scanner.nextInt();
            Resept resept = legesystem.resepter.hent(reseptID);
            boolean brukt = resept.bruk();
            if (brukt) {
                System.out.println("Brukte resept." + " Antall gjenvaerende reit: " + resept.hentReit());
            } else {
                System.out.println("Kunne ikke bruke valgt resept. Ikke mer reit.");
            }
        } catch (UgyldigListeindeks e) {
            System.out.println("Ugyldig id: " + e.getMessage());
        }
    }

    // Statistics menu uses a while loop instead of recursion
    public static void skrivStatistikk() {
        int input = 0;
        while (input != 4) {
            System.out.println("Tast inn tallet ved siden av kommandoen for aa kjoere.");
            System.out.println("1. Totalt antall utskrevne resepter pa vanedannende legemidler. ");
            System.out.println("2. Totalt antall utskrevne resepter pa narkotiske legemidler. ");
            System.out.println("3. Statistikk om mulig misbruk av narkotika.");
            System.out.println("4. Tilbake til hovedmeny.");
            input = scanner.nextInt();
            if (input == 1) {
                int teller = 0;
                for (Resept resept : legesystem.resepter) {
                    Legemiddel legemiddel = resept.hentLegemiddel();
                    if (legemiddel instanceof Vanedannende) {
                        teller++;
                    }
                }
                System.out.println("Antall utskrevne resepter pa vanedannende legemidler er: " + teller);
            }
            if (input == 2) {
                int teller = 0;
                for (Resept resept : legesystem.resepter) {
                    Legemiddel legemiddel = resept.hentLegemiddel();
                    if (legemiddel instanceof Narkotisk) {
                        teller++;
                    }
                }
                System.out.println("Antall utskrevne resepter pa narkotiske legemidler er: " + teller);
            }
            if (input == 3) {
                Lenkeliste<Lege> leger = new Koe<>();
                for (Lege lege : legesystem.leger) {
                    boolean sattInn = false;
                    lege.antallNarkotisk = 0;
                    for (Resept resept : lege.utskrevneResepter) {
                        Legemiddel legemiddel = resept.hentLegemiddel();
                        if (legemiddel instanceof Narkotisk && !sattInn) {
                            leger.leggTil(lege);
                            lege.antallNarkotisk++;
                            sattInn = true;
                        } else if (legemiddel instanceof Narkotisk) {
                            lege.antallNarkotisk++;
                        }
                    }
                }
                IndeksertListe<Lege> sorterteLeger = new IndeksertListe<>();
                for (Lege lege : leger) {
                    boolean sattInn = false;
                    int teller = 0;
                    while (teller < sorterteLeger.stoerrelse() && !sattInn) {
                        if (lege.compareTo(sorterteLeger.hent(teller)) < 0) {
                            sorterteLeger.leggTil(teller, lege);
                            sattInn = true;
                        }
                        teller++;
                    }
                    if (!sattInn) {
                        sorterteLeger.leggTil(lege);
                    }
                }
                System.out.println("Leger som har skrevet ut resepter pa narkotiske legemidler: ");
                for (Lege lege : sorterteLeger) {
                    System.out.println(
                            lege + ", Antall resepter pa narkotiske legemidler skrevet ut: " + lege.antallNarkotisk);
                }

                Lenkeliste<Pasient> pasienter = new Koe<>();
                for (Pasient pasient : legesystem.pasienter) {
                    boolean sattInn = false;
                    pasient.antallNarkotisk = 0;
                    for (Resept resept : pasient.reseptListe) {
                        Legemiddel legemiddel = resept.hentLegemiddel();
                        if (legemiddel instanceof Narkotisk && !sattInn) {
                            pasienter.leggTil(pasient);
                            pasient.antallNarkotisk++;
                            sattInn = true;
                        } else if (legemiddel instanceof Narkotisk) {
                            pasient.antallNarkotisk++;
                        }
                    }
                }
                System.out.println("Pasienter som har en gyldig resept pa narkotiske legemidler: ");
                for (Pasient pasient : pasienter) {
                    System.out.println(
                            pasient + ", Antall resepter pa narkotiske legemidler: " + pasient.antallNarkotisk);
                }
            }
        }
    }
}
