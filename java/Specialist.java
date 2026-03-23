public class Specialist extends Lege implements Godkjenningsfritak{
    final public String kontrollID;

    public Specialist(String navn, String kontrollID) {
        super(navn);
        this.kontrollID = kontrollID;
    }

    public String hentKontrollID(){
        return kontrollID;
    }

    public String toString() {
        return "Lege: " + navn;
    }
}
