class Stabel<E> extends Lenkeliste<E> {

    public void leggTil(E x) {
        Node noden = new Node(x);
        noden.neste = start;
        start = noden;
    }
}
