import java.util.Iterator;

public abstract class Lenkeliste<E> implements Liste<E> {
    public Node start;

    class Node {
        Node neste;
        E verdi;

        public Node(E verdi) {
            this.verdi = verdi;
        }
    }

    public int stoerrelse() {
        Node noden = start;
        int teller = 0;
        while (noden != null) {
            teller++;
            noden = noden.neste;
        }
        return teller;
    }

    public void leggTil(E x) {
        Node noden = new Node(x);
        Node peker = start;

        // Setter noden pa starten om listen er tom
        if (stoerrelse() == 0) {
            start = noden;
        }
        // gar gjennom listen til slutten ogsa putter noden
        else {
            while (peker.neste != null) {
                peker = peker.neste;
            }
            peker.neste = noden;
        }
    }

    public E hent() {
        // sjekker om listen er tom
        if (start == null) {
            throw new UgyldigListeindeks(-1);
        }
        return start.verdi;
    }

    public E hent(int pos) {
        Node peker = start;
        if (pos >= stoerrelse() || pos < 0) {
            throw new UgyldigListeindeks(pos);
        }
        // Looper til jeg kommer til noden
        for (int i = 0; i < pos; i++) {
            peker = peker.neste;
        }
        return peker.verdi;
    }

    public E fjern() {
        // sjekker om listen er tom
        if (start == null) {
            throw new UgyldigListeindeks(-1);
        }
        Node peker = start;
        // setter start som andre element
        start = start.neste;
        return peker.verdi;
    }

    public void skrivUt() {
        Node noden = start;
        while (noden != null) {
            System.out.println(noden.verdi);
            noden = noden.neste;
        }
    }

    public Iterator<E> iterator() {
        return new LenkelisteIterator(this);
    }

    class LenkelisteIterator implements Iterator<E> {
        private Lenkeliste<E> iterator;
        private int gjeldendeIndeks = 0;
        private Node noden;

        public LenkelisteIterator(Lenkeliste<E> liste) {
            iterator = liste;
            noden = start;
        }

        @Override
        public boolean hasNext() {
            return gjeldendeIndeks < stoerrelse();
        }

        @Override
        public E next() {
            return (hent(gjeldendeIndeks++));
        }
    }
}
