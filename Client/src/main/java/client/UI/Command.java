package client.UI;

public enum Command {
    pull("pull"),
    push("push"),
    add("add"),
    share("share"),
    list("list"),
    help("help"),
    exit("exit");

    private final String text;

    /**
     * @param text command name
     */
    Command(final String text) {
        this.text = text;
    }



    @Override
    public String toString() {
        return text;
    }
}
