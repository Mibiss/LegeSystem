public class Pasient {
    public String navn;
    public String fnr;
    static int id = -1;
    public int pasientID;
    public int antallNarkotisk = 0;
    Lenkeliste<Resept> reseptListe = new Koe<>();

    public Pasient(String navn, String fnr) {
        this.navn = navn;
        this.fnr = fnr;
        id += 1;
        pasientID = id;
    }

    @Override
    public String toString() {
        return "Pasient{" +
                "navn='" + navn + '\'' +
                ", fnr='" + fnr + '\'' +
                ", pasientID=" + pasientID +
                ", antallNarkotisk=" + antallNarkotisk +
                ", reseptListe=" + reseptListe +
                '}';
    }

    public Lenkeliste<Resept> skrivUtListe() {
        return reseptListe;
    }

    public Lenkeliste<Resept> leggTilResept(Resept nyResept) {
        reseptListe.leggTil(nyResept);
        return reseptListe;
    }
}
