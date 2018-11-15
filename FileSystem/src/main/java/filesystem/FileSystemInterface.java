package filesystem;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileSystemInterface {


    // f* this not necessary

 /*   public static void newfile(FileWrapper file){
        System.out.println("Creating new file");

        String filename = file.getFileName();

        String fileCreator = file.getFileCreator();

        String fileMAC = file.getFileMAC();

        System.out.println(filename + " " + fileCreator + " " + fileMAC);

    } */

    public static void download(){
        System.out.println("Downloading file");

        // check sessiontoken

        // check folder of user

        // get list of files add mac of file to the list

        // add {Kcs}pubuser of each file to the list

        // return the list
    }
    public static void upload(FileWrapper[] files) throws IOException {


        String fileCreator = files[0].getFileCreator();

        // check sessiontoken

        // check if user has a folder if not create it

        Path path = Paths.get("./" + fileCreator);



        if (!Files.exists(path)) {

            System.out.println(fileCreator + " " + "doesnt exist ");

            System.out.println("Creating" + " " +  fileCreator);

            File folder = new File("./" + fileCreator);

            folder.mkdirs();

        }

        System.out.println("Uploading files");

        for(int i = 0 ;i < Array.getLength(files);i++){

            System.out.println(files[0].getFileName());
            FileOutputStream writer = new FileOutputStream(fileCreator + "\\" + files[i].getFileName() + ".file");
            ObjectOutputStream outwriter = new ObjectOutputStream(writer );
            outwriter.writeObject(files[i]);
            outwriter.close();

        }







        // for each file check mac and compare to the mac(file)

        // add files to the folder if exists

        // return OK




    }
    public static void share(){

        System.out.println("Share file");

        // check sessiontoken

        // check if user exists and user exists

        // check mac and compare to the mac(file)

        // add file to user2 folder

        // associate file with username2 by saving the {Kcs}pub2

        // return ok


    }

}
