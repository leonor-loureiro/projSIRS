import filesystem.FileSystemInterface;
import filesystem.data.EncryptedFileWrapper;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class FileSystemInterfaceTests {

    static final String FILENAME = "fileName.test";
    static final String CREATOR = "testCreator";

    EncryptedFileWrapper encFile;
    Random random = new Random();
    byte[] array = new byte[20];


    @Before
    public void setUp(){

        FileSystemInterface.init();
        encFile = new EncryptedFileWrapper();
        encFile.setFileName(FILENAME);
        encFile.setFileCreator(CREATOR);

        random.nextBytes(array);
        encFile.setFile(array);
    }

    /**
     * Uploads a file successfully
     * @throws IOException
     */
    @Test
    public void successfulUpload() throws IOException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles, CREATOR);

        Path userFolder = Paths.get("./" + CREATOR);
        Assert.assertTrue(Files.exists(userFolder));

        Path userFile = Paths.get(CREATOR + "/" + FILENAME + ".file" );
        Assert.assertTrue(Files.exists(userFile));

    }

    /**
     * Uploads a file successfully twice, creating one backup
     * @throws IOException
     */
    @Test
    public void successfulFirstBackup() throws IOException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles, CREATOR);
        FileSystemInterface.upload(encFiles, CREATOR);


        Path userFile = Paths.get(CREATOR + "/" + FILENAME + 0 + "@oldv" + ".file" );
        Assert.assertTrue(Files.exists(userFile));
    }

    /**
     * Uploads a file successfully three times, creating two backups
     * @throws IOException
     */
    @Test
    public void successfulSecondBackup() throws IOException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles, CREATOR);
        FileSystemInterface.upload(encFiles, CREATOR);
        FileSystemInterface.upload(encFiles, CREATOR);


        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "/" + FILENAME + ".file" )));
        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "/" + FILENAME + 0 + "@oldv" + ".file" )));
        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "/" + FILENAME + 1 + "@oldv" + ".file" )));


    }


    /**
     * Uploads a file successfully four times, creating three backups
     * @throws IOException
     */
    @Test
    public void successfulThirdBackup() throws IOException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles, CREATOR);
        FileSystemInterface.upload(encFiles, CREATOR);
        FileSystemInterface.upload(encFiles, CREATOR);
        FileSystemInterface.upload(encFiles, CREATOR);


        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "/" + FILENAME + ".file" )));
        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "/" + FILENAME + 0 + "@oldv" + ".file" )));
        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "/" + FILENAME + 1 + "@oldv" + ".file" )));
        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "/" + FILENAME + 2 + "@oldv" + ".file" )));

    }

    /**
     * Uploads a file successfully and then downloads it
     * @throws IOException
     */
    @Test
    public void successfulDownload() throws IOException, ClassNotFoundException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles, CREATOR);
        FileSystemInterface.download("./" + CREATOR);

        //Check if the folder exists
        Assert.assertTrue(Files.exists(Paths.get("./" + CREATOR)));
        //Check if the file exists
        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "/" + FILENAME + ".file" )));
    }



    /**
     * Uploads a file successfully four times, creating three backups
     * @throws IOException
     */
   @Test
    public void successfulDownloadThenBackup() throws IOException, ClassNotFoundException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles, CREATOR);           // first version
        FileSystemInterface.download("./" + CREATOR);         // download the file
        FileSystemInterface.upload(encFiles, CREATOR);           // backup

        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "/" + FILENAME + ".file" )));
        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "/" + FILENAME + 0 + "@oldv" + ".file" )));


    }

    /**
     * Uploads files with creator as different user to the correct username
     * @throws IOException
     */
    @Test
    public void uploadFilesFromDifferentUser() throws IOException {
        EncryptedFileWrapper[] encFiles = {encFile};
        String diffCreator = "differentCreator";
        FileSystemInterface.upload(encFiles, diffCreator);

        //Check if the folder exists
        Assert.assertTrue(Files.exists(Paths.get("./" + diffCreator)));
        //Check if the file exists
        Assert.assertTrue(Files.exists(Paths.get(diffCreator + "/" + FILENAME + ".file" )));

        //Check if the folder exists
        Assert.assertFalse(Files.exists(Paths.get("./" + CREATOR)));
        //Check if the file exists
        Assert.assertFalse(Files.exists(Paths.get(CREATOR + "/" + FILENAME + ".file" )));

        //Clean up
        File index = new File("./" + diffCreator);
        if(index.exists())
            FileUtils.deleteDirectory(index);
    }

    /**
     * Retrive the previous version of the file
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Test
    public void successGetOldVersion() throws IOException, ClassNotFoundException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles, CREATOR);

        byte[] oldCopy = encFile.getFile().clone();

        //Change the file
        random.nextBytes(array);
        encFile.setFile(array);
        FileSystemInterface.upload(encFiles, CREATOR);

        EncryptedFileWrapper oldVersion = FileSystemInterface.getOldVersion(CREATOR, FILENAME);

        Assert.assertArrayEquals(oldVersion.getFile(), oldCopy);

        //Check if the file is replaced by the old version
        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "/" + FILENAME + ".file" )));
        Assert.assertFalse(Files.exists(Paths.get(CREATOR + "/" + FILENAME + 0 + "@oldv" + ".file" )));
    }

    /**
     * Try to get an old version, when there is no backup
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Test
    public void getOldVersionWithoutBackups() throws IOException, ClassNotFoundException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles, CREATOR);

        Assert.assertFalse(Files.exists(Paths.get(CREATOR + "/" + FILENAME + 0 + "@oldv" + ".file" )));
        Assert.assertTrue(FileSystemInterface.getOldVersion(CREATOR, FILENAME) == null);

        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "/" + FILENAME + ".file" )));
    }

    /**
     * Shares a file with a user that doesn't have any files yet
     * @throws IOException
     */
    @Test
    public void successfulShare() throws IOException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles, CREATOR);

        String user2 = "user2";
        FileSystemInterface.share(CREATOR, user2, encFile);

        //Check if the folder exists
        Assert.assertTrue(Files.exists(Paths.get("./" + user2)));
        //Check if the file exists
        Assert.assertTrue(Files.exists(Paths.get(user2 + "/" +  FILENAME + ".file" )));

        //Clean up
        File index = new File("./" + user2);
        if(index.exists())
            FileUtils.deleteDirectory(index);

    }

    /**
     * Shares a file with a user that already has files
     * @throws IOException
     */
    @Test
    public void successfulShare2() throws IOException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles, CREATOR);

        String user2 = "user2";
        encFile.setFileName("fileName2.txt");
        FileSystemInterface.upload(encFiles, user2);

        encFile.setFileName(FILENAME);
        FileSystemInterface.share(CREATOR, user2, encFile);

        //Check if the folder exists
        Assert.assertTrue(Files.exists(Paths.get("./" + user2)));
        //Check if the file exists
        Assert.assertTrue(Files.exists(Paths.get(user2 + "/" + FILENAME + ".file" )));

        //Clean up
        File index = new File("./" + user2);
        if(index.exists())
            FileUtils.deleteDirectory(index);

    }

    /**
     * Share a file with a user that already has a file with the same name
     * @throws IOException
     */
    @Test
    public void successfulShare3() throws IOException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles, CREATOR);
        String user2 = "user2";
        FileSystemInterface.upload(encFiles, user2);
        encFile.setFileName("from-" +CREATOR + "_" + FILENAME);
        FileSystemInterface.share(CREATOR, user2, encFile);

        //Check if the folder exists
        Assert.assertTrue(Files.exists(Paths.get("./" + user2)));
        //Check if the file exists
        Assert.assertTrue(Files.exists(Paths.get(user2 + "/" + FILENAME + ".file" )));

        //Check if the file exists
        Assert.assertTrue(Files.exists(Paths.get(user2 + "/" +FILENAME + ".file" )));

        //Clean up
        File index = new File("./" + user2);
        if(index.exists())
            FileUtils.deleteDirectory(index);

    }


    @After
    public void cleanUp() throws IOException {
       File index = new File("./" + CREATOR);
        if(index.exists())
            FileUtils.deleteDirectory(index);
    }
}
