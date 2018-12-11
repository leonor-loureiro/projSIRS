package filesystem;

import crypto.Crypto;
import crypto.TokenManager;
import crypto.exception.CryptoException;
import filesystem.data.EncryptedFileWrapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Map;

public class FileSystemInterface {


    private static final String keystoreFile = "./" + "\\src\\main\\resources\\serverkeystore.jks";
    private static final String keystorePwd = "password";
    private static final String authServerAlias = "auth-public-key";

    // f* this not necessary

 /*   public static void newfile(FileWrapper file){
        System.out.println("Creating new file");

        String filename = file.getFileName();

        String fileCreator = file.getFileCreator();

        String fileMAC = file.getFileMAC();

        System.out.println(filename + " " + fileCreator + " " + fileMAC);

    } */

   public static boolean validateToken(String username, String token){

        PublicKey key = null;
        try {
            key = Crypto.getPublicKey(keystoreFile, keystorePwd, authServerAlias);
        } catch (CryptoException e) {
            return false;
        }
        return TokenManager.validateJTW(token, "authServer", username, key);
    }

    public static EncryptedFileWrapper[] download(String name) throws IOException, ClassNotFoundException {
        System.out.println("Downloading files");

        // check sessiontoken

        // check folder of user

        System.out.println(name);
        Path path = Paths.get("./" + name);

        if(!Files.exists(path)){
            System.out.println(name + " " + "not registered");

            //Send exception
        }

        // download all files? or file by file?

        File folder = new File(path.toString());

        File[] listOfFiles = folder.listFiles();

        ArrayList<EncryptedFileWrapper> files = new ArrayList<EncryptedFileWrapper>();

        for(int i = 0; i<listOfFiles.length; i++){
            if(listOfFiles[i].getName().endsWith("oldv.file"));
            System.out.println("Adding file " + " " + listOfFiles[i].getName() + " " + "to be downloaded");
            FileInputStream f = new FileInputStream(listOfFiles[i]);
            ObjectInputStream o = new ObjectInputStream(f);
            EncryptedFileWrapper file = (EncryptedFileWrapper)o.readObject();
            files.add(file);
        }

        EncryptedFileWrapper[] vectorEnc;

        vectorEnc = new EncryptedFileWrapper[files.size()];


        for(int i =0;i<files.size();i++){
            vectorEnc[i] = files.get(i);
        }
        return vectorEnc;


    }

    public static void upload(EncryptedFileWrapper[] files) throws IOException {

        System.out.println("uploading");


        String fileCreator = files[0].getFileCreator();


        // check if user has a folder if not create it

        Path path = Paths.get("./" + fileCreator);



        if (!Files.exists(path)) {

            System.out.println(fileCreator + " " + "doesnt exist ");

            System.out.println("Creating" + " " +  fileCreator);

            File folder = new File("./" + fileCreator);

            folder.mkdirs();

        }

        System.out.println("Uploading files");

        for(int i = 0 ; i < files.length ;i++){

            System.out.println("Uploading file:" + " " + files[i].getFileName());
            String fileName  = files[i].getFileName();
            System.out.println("Was there need to backup the file:" + " " + checkForBackup(fileCreator + "\\" + fileName));
            FileOutputStream writer = new FileOutputStream(fileCreator + "\\" + fileName + ".file");
            ObjectOutputStream outwriter = new ObjectOutputStream(writer );
            outwriter.writeObject(files[i]);
            outwriter.close();

        }

        System.out.println("Finished uploading files");



        // for each file check mac and compare to the mac(file)

        // add files to the folder if exists

        // return OK


    }

    private static Boolean checkForBackup(String file) {
        File f = new File(file + ".file");
        int versionumber = 0;
        loop:
        if(f.exists() && !f.isDirectory()) {
            for(int i = 0;true;i++){
                File oldbackup = new File(file + i + "oldv" + ".file");
                System.out.println(oldbackup.getAbsolutePath());
                if(oldbackup.exists()) {
                    System.out.println("Version" + " " + versionumber + " " + "alreadyexists" + "of file " + " " + file);
                    versionumber++;
                    continue;

                }
                else {
                    break loop;
                }
            }
        }
        else
            return false;
        File newfile  = new File(file + (versionumber++) + "oldv"  + ".file");
        f.renameTo(newfile);
        return true;
    }

    public EncryptedFileWrapper getOldVersion(String fileCreator,String fileName,Boolean corrupted) throws IOException, ClassNotFoundException {
        File file = new File(fileCreator + "\\" + fileName + ".file");
        if (corrupted){
            int versionumber = 0;
            file.delete();
            loop:
            for(int i = 0;true;i++){
                File oldbackup = new File(fileCreator + "\\" + fileName +  i + "oldv" + ".file");
                versionumber++;
                if(oldbackup.exists()) {
                    continue;

                }
                else {
                    break loop;
                }
            }

            File newMainFile = new File(fileCreator + "\\" + fileName + ".file");
            File higherVersionFile = new File(fileCreator + "\\" + fileName +  versionumber +"oldv"  + ".file");
            higherVersionFile.renameTo(newMainFile);

            FileInputStream f = new FileInputStream(newMainFile);
            ObjectInputStream o = new ObjectInputStream(f);
            EncryptedFileWrapper filetobereturned = (EncryptedFileWrapper)o.readObject();
            return filetobereturned;
        }
        else{
            int versionumber = 0;
            loop:
            for(int i = 0;true;i++){
                File oldbackup = new File(fileCreator + "\\" + fileName + i + "oldv"  + ".file");
                versionumber++;
                if(oldbackup.exists()) {
                    continue;

                }
                else {
                    break loop;
                }
            }

            File higherVersionFile = new File(fileCreator + "\\" + fileName + versionumber + "oldv"  + ".file");

            FileInputStream f = new FileInputStream(higherVersionFile);
            ObjectInputStream o = new ObjectInputStream(f);
            EncryptedFileWrapper filetobereturned = (EncryptedFileWrapper)o.readObject();
            return filetobereturned;
        }


    }

    public static void share(String user1,String user2,EncryptedFileWrapper file) throws IOException, ClassNotFoundException {

        System.out.println("Share file");


        // check if user exists and user exists


        // add file to user2 folder

        String filename = file.getFileName();

        Path pathuser2 = Paths.get("./" + user2);


        if (!Files.exists(pathuser2)){
            System.out.println("User" + " " + pathuser2 + " " + "doesnt exist");
        }

        FileOutputStream writer = new FileOutputStream(user2 + "\\" + filename + ".file");
        ObjectOutputStream outwriter = new ObjectOutputStream(writer );
        outwriter.writeObject(file);
        outwriter.close();



        // return ok


    }

}
