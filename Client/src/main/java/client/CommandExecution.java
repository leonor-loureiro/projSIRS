package client;

import client.localFileHandler.FileManager;
import client.localFileHandler.FileWrapper;
import client.security.Login;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class CommandExecution {

    /**
     * User under which the commands will be executed
     */
    private User user;


    /**
     * Communicates with the server
     */
    private Communication communication = new Communication();

    private void setUser(User user) {this.user = user; }

    public void login(Login login){
        setUser(new User(login.getUsername(), login.getPassword()));
        communication.login(user);
    }


    /**
     * adds a file to staging file Lists, allowing it to be pushed
     * @param filename name of the file to be staged
     */
    public void add(String filename){

        if(user == null){
            System.out.println("User must be logged for this operation!!");
            return;
        }

        System.out.println("Adding File ..." + filename);

        FileWrapper file = FileManager.loadFile(filename);

        user.addFileToStaged(file);

    }

    /**
     * get all remote files and adds them to staging
     */
    public void pull(){
        System.out.println("Pulling files from remote Files ...");
        if(user == null){
            System.out.println("User must be logged for this operation!!");
            return;
        }

        List<FileWrapper> files = communication.getFiles(user);

        for(FileWrapper file: files){

            file.setFile(new File(file.getFileName()));

            try ( FileOutputStream outputStream = new FileOutputStream(file.getFile())) {
                outputStream.write(file.getFileContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        user.addFilesToStaged(files);

    }

    /**
     * sends all staged file to remote
     */
    public void push(){
        System.out.println("Pushing Files to remote ...");
        if(user == null){
            System.out.println("User must be logged for this operation!!");
            return;
        }

        communication.putFiles(user, user.getstagedFiles());
    }

    /**
     * Shares a user's file with a given user
     */
    public void share(String dest, String fileName){
        System.out.println("Sharing file with user ...");
        System.out.println("It doesn't work yet! :P");
        if(user == null){
            System.out.println("User must be logged for this operation!!");
            return;
        }
    }

    /**
     * lists all local and staging files
     */
    public void list(){

        if(user == null){
            System.out.println("User must be logged for this operation!!");
            return;
        }

        System.out.println("Listing Files...");
        System.out.println("Local files:");
        for(String name : FileManager.listFileNames()){
            System.out.println(name);
        }
        System.out.println();

        System.out.println("Staged files:");
        for(String name : user.getStagedFilesNames()){
            System.out.println(name);
        }
    }

    /**
     * Exits program
     */
    public void exit(){
        System.out.println("Shutting down...");
        System.exit(0);

    }


}
