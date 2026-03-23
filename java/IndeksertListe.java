class IndeksertListe<E> extends Lenkeliste<E> {

    public void leggTil(int pos, E x) {
        Node noden = new Node(x);
        Node peker = start;

        // sjekker om gyldig index
        if (pos < 0 || pos > stoerrelse()) {
            throw new UgyldigListeindeks(pos);
        }

        // sjekker om index er 0
        if (pos == 0) {
            noden.neste = peker;
            start = noden;
        }

        // ellers putter i index
        else {
            for (int i = 0; i < pos - 1; i++) {
                peker = peker.neste;
            }
            noden.neste = peker.neste;
            peker.neste = noden;
        }
    }

    public void sett(int pos, E x) {
        Node peker = start;

        // sjekker om gyldig index
        if (pos < 0 || pos >= stoerrelse()) {
            throw new UgyldigListeindeks(pos);
        }

        // looper helt til noden
        for (int i = 0; i < pos; i++) {
            peker = peker.neste;
        }

        peker.verdi = x;
    }

    public E hent(int pos) {
        Node peker = start;

        // sjekker om gyldig index
        if (pos >= stoerrelse() || pos < 0) {
            throw new UgyldigListeindeks(pos);
        }

        // looper helt til noden
        for (int i = 0; i < pos; i++) {
            peker = peker.neste;
        }
        return peker.verdi;
    }

    public E fjern(int pos) {
        Node peker = start;
        Node noden;

        // sjekker om gyldig index
        if (pos >= stoerrelse() || pos < 0) {
            throw new UgyldigListeindeks(pos);
        }

        // sjekker om index er 0
        if (pos == 0) {
            noden = start;
            start = start.neste;
        } else {

            // Looper til noden hvis pos ikke er 0.
            for (int i = 0; i < pos - 1; i++) {
                peker = peker.neste;
            }

            // Fjerner noden
            noden = peker.neste;
            peker.neste = noden.neste;
        }
        return noden.verdi;
    }
}
