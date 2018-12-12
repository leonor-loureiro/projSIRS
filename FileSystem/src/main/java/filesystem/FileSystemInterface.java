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



    public static boolean validateToken(String username, String token){

        PublicKey key = null;
        try {
            key = Crypto.getPublicKey(keystoreFile, keystorePwd, authServerAlias);
        } catch (CryptoException e) {
            return false;
        }

        return TokenManager.validateJTW(token, "authServer", username, key);
    }

    /**
     * Gets all files from the user
     * @param name Name of the user
     * @return a list of encrypted files
     * @throws IOException
     * @throws ClassNotFoundException
     */

    public static EncryptedFileWrapper[] download(String name) throws IOException, ClassNotFoundException {
        System.out.println("Downloading files");


        System.out.println(name);
        Path path = Paths.get("./" + name);

        File folder = new File(path.toString());

        //get all the files in the folder
        File[] listOfFiles = folder.listFiles();

        ArrayList<EncryptedFileWrapper> files = new ArrayList<EncryptedFileWrapper>();

        for(int i = 0; i<listOfFiles.length; i++){
            if(listOfFiles[i].getName().endsWith("oldv.file")){
                continue;
            }
            System.out.println("Adding file " + " " + listOfFiles[i].getName() + " " + "to be downloaded");
            FileInputStream f = new FileInputStream(listOfFiles[i]);
            ObjectInputStream o = new ObjectInputStream(f);
            EncryptedFileWrapper file = (EncryptedFileWrapper)o.readObject();
            files.add(file);
            o.close();
        }

        EncryptedFileWrapper[] vectorEnc;

        vectorEnc = new EncryptedFileWrapper[files.size()];


        for(int i =0;i<files.size();i++){
            vectorEnc[i] = files.get(i);
        }


        return vectorEnc;


    }

    public static void upload(EncryptedFileWrapper[] files,String username) throws IOException {

        System.out.println("uploading");


        String fileCreator = username;



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
        System.out.println(newfile.getAbsolutePath());
        f.renameTo(newfile);
        return true;
    }

    public static EncryptedFileWrapper getOldVersion(String fileCreator,String fileName,Boolean corrupted) throws IOException, ClassNotFoundException {
        File file = new File(fileCreator + "\\" + fileName + ".file");
        int versionumber = 0;
            file.delete();
            loop:
            for (int i = 0; true; i++) {
                File oldbackup = new File(fileCreator + "\\" + fileName + i + "oldv" + ".file");
                if (oldbackup.exists()) {
                    versionumber++;
                    continue;

                } else {
                    if(i==0)
                        return null;
                    versionumber--;
                    break loop;
                }
            }
            File newMainFile = new File(fileCreator + "\\" + fileName + ".file");
            File higherVersionFile = new File(fileCreator + "\\" + fileName +  versionumber +"oldv"  + ".file");
            higherVersionFile.renameTo(newMainFile);

            FileInputStream f = new FileInputStream(newMainFile);
            ObjectInputStream o = new ObjectInputStream(f);
            EncryptedFileWrapper filetobereturned = (EncryptedFileWrapper)o.readObject();
            o.close();
            return filetobereturned;


    }

    public static Boolean share(String user1,String user2,EncryptedFileWrapper file) throws IOException {
        System.out.println("Share file");


        String filename = file.getFileName();

        Path pathuser1 = Paths.get("./" + user1);

        Path pathuser2 = Paths.get("./" + user2);

        Path fileName = Paths.get("./" + user2 + "/" + filename + ".file");


        if (!Files.exists(pathuser1)) {
            System.out.println("User" + " " + pathuser1 + " " + "doesnt exist");
            return false;
        }
        if (!Files.exists(pathuser2)) {
            System.out.println("User" + " " + pathuser2 + " " + "doesnt exist");
            return false;
        }
        if (!Files.exists(fileName)) {
            System.out.println("User" + " " + filename + " " + "doesnt exist");
            return false;
        }

        FileOutputStream writer = new FileOutputStream(user2 + "\\" + filename + ".file");
        ObjectOutputStream outwriter = new ObjectOutputStream(writer);
        outwriter.writeObject(file);
        outwriter.close();
        return true;


    }

}
