public class Lege {
    IndeksertListe<Resept> utskrevneResepter = new IndeksertListe<>();
    public int antallNarkotisk = 0;
    String navn;

    public Lege(String navn) {
        this.navn = navn;
    }

    public String hentNavn() {
        return navn;
    }

    public int compareTo(Lege lege) {
        return navn.compareTo(lege.navn);
    }

    public Resept skrivResept(Legemiddel legemiddel, Pasient pasient, int reit) throws UlovligUtskrift {
        if (legemiddel instanceof Narkotisk && !(this instanceof Specialist)) {
            throw new UlovligUtskrift(this, legemiddel);
        }
        HvitResept hvitResept = new HvitResept(legemiddel, this, pasient, reit);
        utskrevneResepter.leggTil(hvitResept);
        pasient.leggTilResept(hvitResept);
        return hvitResept;
    }

    public HvitResept skrivHvitResept(Legemiddel legemiddel, Pasient pasient, int reit) throws UlovligUtskrift {
        if (legemiddel instanceof Narkotisk) {
            throw new UlovligUtskrift(this, legemiddel);
        }
        HvitResept hvitResept = new HvitResept(legemiddel, this, pasient, reit);
        pasient.leggTilResept(hvitResept);
        utskrevneResepter.leggTil(hvitResept);
        return hvitResept;
    }

    public MilResept skrivMilResept(Legemiddel legemiddel, Pasient pasient) throws UlovligUtskrift {
        if (legemiddel instanceof Narkotisk) {
            throw new UlovligUtskrift(this, legemiddel);
        }
        MilResept milResept = new MilResept(legemiddel, this, pasient);
        pasient.leggTilResept(milResept);
        utskrevneResepter.leggTil(milResept);
        return milResept;
    }

    public PResept skrivPResept(Legemiddel legemiddel, Pasient pasient, int reit) throws UlovligUtskrift {
        if (legemiddel instanceof Narkotisk) {
            throw new UlovligUtskrift(this, legemiddel);
        }
        PResept pResept = new PResept(legemiddel, this, pasient, reit);
        pasient.leggTilResept(pResept);
        utskrevneResepter.leggTil(pResept);
        return pResept;
    }

    public BlaaResept skrivBlaaResept(Legemiddel legemiddel, Pasient pasient, int reit) throws UlovligUtskrift {
        if (legemiddel instanceof Narkotisk && !(this instanceof Specialist)) {
            throw new UlovligUtskrift(this, legemiddel);
        }
        BlaaResept blaaResept = new BlaaResept(legemiddel, this, pasient, reit);
        pasient.leggTilResept(blaaResept);
        utskrevneResepter.leggTil(blaaResept);
        return blaaResept;
    }

    @Override
    public String toString() {
        return "Lege{" +
                ", navn='" + navn + '\'' +
                '}';
    }
}
