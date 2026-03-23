public abstract class Resept {
    static int id = -1;
    final public int reseptID;
    public int reit;
    public Legemiddel legemiddel;
    public Lege utskrivendeLege;
    public Pasient pasient;

    public Resept(Legemiddel legemiddel, Lege utskrivendeLege, Pasient pasient, int reit) {
        this.legemiddel = legemiddel;
        this.utskrivendeLege = utskrivendeLege;
        this.pasient = pasient;
        this.reit = reit;
        id += 1;
        reseptID = id;
    }

    public int hentID() {
        return reseptID;
    }

    public Legemiddel hentLegemiddel() {
        return legemiddel;
    }

    public Lege hentLege() {
        return utskrivendeLege;
    }

    public int hentPasientID() {
        return pasient.pasientID;
    }

    public int hentReit() {
        return reit;
    }

    public boolean bruk() {
        if (reit > 0) {
            reit -= 1;
            return true;
        } else {
            return false;
        }
    }

    abstract public String farge();

    abstract public int prisAaBetale();

    public String toString() {
        return "Resept: " + reseptID + "; Legemiddel: " + legemiddel + "; til pasient: " + pasient.pasientID;
    }
}
