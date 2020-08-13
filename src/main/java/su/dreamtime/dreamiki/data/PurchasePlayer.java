package su.dreamtime.dreamiki.data;

public class PurchasePlayer {
    public String username;
    public String command;
    public boolean given;
    public boolean item;

    public PurchasePlayer(String username, String command, boolean given, boolean item) {
        this.username = username;
        this.command = command;
        this.given = given;
        this.item = item;
    }
}
