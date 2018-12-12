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
import java.util.Properties;

/**
 * This class implements the functions of the file system service
 *
 * <li>Upload files</li>
 * <li>Download files</li>
 * <li>Share files</li>
 * <li>Retrieve backup</li>
 */
public class FileSystemInterface {


    private static String keystoreFile;
    private static String keystorePwd;
    private static String authServerAlias;


    /**
     * Initializes the file system interface
     */
    public static void init(){
        if(Application.properties == null){
            try {
                Application.properties = new Properties();
                InputStream input = new FileInputStream("./" + "\\src\\main\\resources\\config.properties");
                Application.properties.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        keystoreFile = "./" + "\\src\\main\\resources\\serverkeystore.jks";
        keystorePwd = Application.properties.getProperty("keystorepwd");
        authServerAlias = Application.properties.getProperty("myalias");
    }


    /**
     * Checks if a token is valid
     * @param username username of the user the token was for
     * @param token authentication token
     * @return true if is valid; false otherwise
     */
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

        //check if folder exists
        if(!folder.exists())
            return null;

        //get all the files in the folder
        File[] listOfFiles = folder.listFiles();

        ArrayList<EncryptedFileWrapper> files = new ArrayList<EncryptedFileWrapper>();

        for(int i = 0; i<listOfFiles.length; i++){
            //check if the file we want to download is an old version
            if(listOfFiles[i].getName().endsWith("@oldv.file")){
                continue;
            }
            System.out.println("Adding file " + " " + listOfFiles[i].getName() + " " + "to be downloaded");

            //get the file from the system
            FileInputStream f = new FileInputStream(listOfFiles[i]);
            ObjectInputStream o = new ObjectInputStream(f);
            EncryptedFileWrapper file = (EncryptedFileWrapper)o.readObject();
            files.add(file);
            f.close();
            o.close();
        }

        EncryptedFileWrapper[] vectorEnc;

        vectorEnc = new EncryptedFileWrapper[files.size()];


        //build a vector of encryptedfilewrappers to return
        for(int i =0;i<files.size();i++){
            vectorEnc[i] = files.get(i);
        }


        return vectorEnc;


    }

    /**
     * Receives files from clients and stores them in the filesystem
     * @param files list of encryptedfilewrappers to store
     * @param username name of the user
     * @throws IOException
     */
    public static void upload(EncryptedFileWrapper[] files,String username) throws IOException {

        System.out.println("uploading");


        //get the name of the user
        String fileCreator = username;



        // get the path to his folder
        Path path = Paths.get("./" + fileCreator);



        // if the folder doesnt exist create it (first upload)
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

            //check if there is a need to create a backup
            System.out.println("Was there need to backup the file:" + " " + checkForBackup(fileCreator + "\\" + fileName));

            //build the file in the system
            FileOutputStream writer = new FileOutputStream(fileCreator + "\\" + fileName + ".file");
            ObjectOutputStream outwriter = new ObjectOutputStream(writer );
            outwriter.writeObject(files[i]);
            writer.close();
            outwriter.close();

        }

        System.out.println("Finished uploading files");



    }

    /**
     * Checks if a file needs to be backed up
     * @param file to be checked
     * @return
     */
    private static Boolean checkForBackup(String file) {
        File f = new File(file + ".file");
        int versionumber = 0;
        loop:
        //if the file already exists and isnt a directory
        if(f.exists() && !f.isDirectory()) {
            for(int i = 0;true;i++){
                File oldbackup = new File(file + i + "@oldv" + ".file");
                System.out.println(oldbackup.getAbsolutePath());
                //If a backup already exists we increment the number of the version
                if(oldbackup.exists()) {
                    System.out.println("Version" + " " + versionumber + " " + "alreadyexists" + "of file " + " " + file);
                    versionumber++;
                    continue;

                }
                else {
                    //when we already now the number of the version stop the cycle
                    break loop;
                }
            }
        }
        else
            return false;

        //rename the old file, to the newest version
        File newfile  = new File(file + (versionumber++) + "@oldv"  + ".file");
        System.out.println(newfile.getAbsolutePath());
        f.renameTo(newfile);
        return true;
    }

    /**
     * Gets the most recent backed up version
     * @param fileCreator name of the user
     * @param fileName file requested
     * @return the EncryptedFileWrapper containing the version
     * @throws IOException
     * @throws ClassNotFoundException
     */

    public static EncryptedFileWrapper getOldVersion(String fileCreator, String fileName) throws IOException, ClassNotFoundException {
        File file = new File(fileCreator + "\\" + fileName + ".file");
        int versionNr = 0;
            loop:
            for (int i = 0; true; i++) {
                File oldbackup = new File(fileCreator + "\\" + fileName + i + "@oldv" + ".file");
                if (oldbackup.exists()) {
                    versionNr++;
                    continue;

                } else {
                    if(i==0)
                        return null;
                    versionNr--;
                    break loop;
                }
            }
            //delete the file
            file.delete();

            //replace the current version with the previous one
            File newMainFile = new File(fileCreator + "\\" + fileName + ".file");
            File higherVersionFile = new File(fileCreator + "\\" + fileName + versionNr +"@oldv"  + ".file");
            higherVersionFile.renameTo(newMainFile);

            //get the file from the filesystem to return
            FileInputStream f = new FileInputStream(newMainFile);
            ObjectInputStream o = new ObjectInputStream(f);
            EncryptedFileWrapper filetobereturned = (EncryptedFileWrapper)o.readObject();
            f.close();
            o.close();
            return filetobereturned;


    }


    /**
     * Share the file with user2 from user1.
     * @param user1 origin user
     * @param user2 destination user
     * @param file
     * @return a boolean returning if the operation was sucessful
     * @throws IOException
     */
    public static Boolean share(String user1,String user2,EncryptedFileWrapper file) throws IOException {
        System.out.println("Share file");


        String filename = file.getFileName();

        Path pathuser1 = Paths.get("./" + user1);

        Path pathuser2 = Paths.get("./" + user2);



        //check if user1 folder exists
        if (!Files.exists(pathuser1)) {
            System.out.println("User" + " " + pathuser1 + " " + "doesnt exist");
            return false;
        }


        //check if user2 folders exists if not create it
        if (!Files.exists(pathuser2)) {
            File folder = new File("./" + user2);
            folder.mkdirs();
        }

        //put the file in user2 folder
        FileOutputStream writer = new FileOutputStream(user2 + "\\" + filename + ".file");
        ObjectOutputStream outwriter = new ObjectOutputStream(writer);
        outwriter.writeObject(file);
        writer.close();
        outwriter.close();
        return true;


    }

}
