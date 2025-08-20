public class PResept extends HvitResept {
    static final int UNGRABATT = 108;

    public PResept(Legemiddel legemiddel, Lege utskrivendeLege, Pasient pasient, int reit) {
        super(legemiddel, utskrivendeLege, pasient, reit);
    }

    public int prisAaBetale() {
        int x = legemiddel.hentPris() - UNGRABATT;

        if (x < 0) {
            return 0;
        } else {
            return x;
        }
    }
}
