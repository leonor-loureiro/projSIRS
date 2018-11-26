package filesystem;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileSystemInterface {


    // f* this not necessary

 /*   public static void newfile(FileWrapper file){
        System.out.println("Creating new file");

        String filename = file.getFileName();

        String fileCreator = file.getFileCreator();

        String fileMAC = file.getFileMAC();

        System.out.println(filename + " " + fileCreator + " " + fileMAC);

    } */

    public static List<EncryptedFileWrapper> download(String name) throws IOException, ClassNotFoundException {
        System.out.println("Downloading file");

        // check sessiontoken

        // check folder of user

        Path path = Paths.get("./" + name);

        if(!Files.exists(path)){
            System.out.println(name + " " + "not registered");

            //Send exception
        }

        // download all files? or file by file?

        File folder = new File(path.toString());

        File[] listoffiles = folder.listFiles();

        ArrayList<EncryptedFileWrapper> files = new ArrayList<EncryptedFileWrapper>();

        for(int i = 0; i<listoffiles.length; i++){
            FileInputStream f = new FileInputStream(listoffiles[i]);
            ObjectInputStream o = new ObjectInputStream(f);
            EncryptedFileWrapper file = (EncryptedFileWrapper)o.readObject();
            files.add(file);
        }


        return files;
        // get list of files add mac of file to the list

        // add {Kcs}pubuser of each file to the list

        // return the list
    }

    public static void upload(List<EncryptedFileWrapper> files) throws IOException {

        System.out.println("uploading");


        String fileCreator = files.get(0).getFileCreator();

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

        for(int i = 0 ; i < files.size() ;i++){

            System.out.println(files.get(i).getFileName());
            FileOutputStream writer = new FileOutputStream(fileCreator + "\\" + files.get(i).getFileName() + ".file");
            ObjectOutputStream outwriter = new ObjectOutputStream(writer );
            outwriter.writeObject(files.get(i));
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
