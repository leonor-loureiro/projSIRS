package client.UI;

public interface CommandExecution {

    static void add(){
        System.out.println("Adding File ...");
    }

    static void pull(){
        System.out.println("Pulling files from remote Files ...");
    }

    static void push(){
        System.out.println("Pushing Files to remote ...");
    }

    static void share(){
        System.out.println("Sharing file with user ...");
    }

    static void list(){
        System.out.println("Listing Files...");
    }

    static void exit(){
        System.out.println("Shutting down...");
        System.exit(0);

    }

}
