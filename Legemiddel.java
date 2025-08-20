abstract class Legemiddel {
    final public String navn;
    private int pris;
    final public double virkestoff;
    static int id = -1;
    final public int legemiddelID;

    public Legemiddel(String navn, int pris, double virkestoff) {
        this.navn = navn;
        this.pris = pris;
        this.virkestoff = virkestoff;
        id += 1;
        legemiddelID = id;
    }

    public int hentPris() {
        return pris;
    }

    public void settNyPris(int nypris) {
        pris = nypris;
    }

    public String toString() {
        return navn + " med pris: " + pris + "kr og har " + virkestoff + " virkestoff";
    }

    public String hentNavn() {
        return navn;
    }
}
