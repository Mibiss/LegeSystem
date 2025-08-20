class BlaaResept extends Resept {

    public BlaaResept(Legemiddel legemiddel, Lege utskrivendeLege, Pasient pasient, int reit) {
        super(legemiddel, utskrivendeLege, pasient, reit);
    }

    public String farge() {
        return "Blaa";
    }

    public int prisAaBetale() {
        int x = legemiddel.hentPris();
        return (int) Math.round(x * 0.25);
    }

}