class Prioritetskoe<E extends Comparable<E>> extends Lenkeliste<E> {
    public void leggTil(E x){
        Node noden = new Node(x);
        Node peker = start;
        if (stoerrelse() == 0){
            start = noden;
        }
        //hvis noden er mindre enn foerste node sa tar den plassen
        else if(x.compareTo(start.verdi) < 0 ){
            noden.neste = start;
            start = noden;
        }
        //looper gjennom listen og sammenligner nodene til vi finner riktig plass
        else{
            while(peker.neste != null){
                //hvis noden er storre enn noden pekern holder saa fortsatt vi aa loope
                if (x.compareTo(peker.neste.verdi) > 0){
                    peker = peker.neste;
                }
                //ellers er den storre eller lik sa vi putter noden her
                else {
                    noden.neste = peker.neste;
                    peker.neste = noden;
                    return;
                }
            }
            peker.neste = noden;
        }
    }
}
