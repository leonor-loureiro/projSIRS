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
    @Before
    public void setUp(){
        byte[] array = new byte[20];
        Random random = new Random();

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
        FileSystemInterface.upload(encFiles);

        Path userFolder = Paths.get("./" + CREATOR);
        Assert.assertTrue(Files.exists(userFolder));

        Path userFile = Paths.get(CREATOR + "\\" + FILENAME + ".file" );
        Assert.assertTrue(Files.exists(userFile));

    }

    /**
     * Uploads a file successfully twice, creating one backup
     * @throws IOException
     */
    @Test
    public void successfulFirstBackup() throws IOException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles);
        FileSystemInterface.upload(encFiles);

        Path userFile = Paths.get(CREATOR + "\\" + FILENAME + 0 + "oldv" + ".file" );
        Assert.assertTrue(Files.exists(userFile));
    }

    /**
     * Uploads a file successfully three times, creating two backups
     * @throws IOException
     */
    @Test
    public void successfulSecondBackup() throws IOException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles);
        FileSystemInterface.upload(encFiles);
        FileSystemInterface.upload(encFiles);

        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "\\" + FILENAME + ".file" )));
        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "\\" + FILENAME + 0 + "oldv" + ".file" )));
        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "\\" + FILENAME + 1 + "oldv" + ".file" )));


    }


    /**
     * Uploads a file successfully four times, creating three backups
     * @throws IOException
     */
    @Test
    public void successfulThirdBackup() throws IOException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles);
        FileSystemInterface.upload(encFiles);
        FileSystemInterface.upload(encFiles);
        FileSystemInterface.upload(encFiles);

        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "\\" + FILENAME + ".file" )));
        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "\\" + FILENAME + 0 + "oldv" + ".file" )));
        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "\\" + FILENAME + 1 + "oldv" + ".file" )));
        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "\\" + FILENAME + 2 + "oldv" + ".file" )));

    }

    /**
     * Uploads a file successfully and then downloads it
     * @throws IOException
     */
    @Test
    public void successfulDownload() throws IOException, ClassNotFoundException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles);
        FileSystemInterface.download("./" + CREATOR);

        //Check if the folder exists
        Assert.assertTrue(Files.exists(Paths.get("./" + CREATOR)));
        //Check if the file exists
        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "\\" + FILENAME + ".file" )));
    }



    /**
     * Uploads a file successfully four times, creating three backups
     * @throws IOException
     */
  /*  @Test
    public void successfulDownloadThenBackup() throws IOException, ClassNotFoundException {
        EncryptedFileWrapper[] encFiles = {encFile};
        FileSystemInterface.upload(encFiles);                       // first version
        //FileSystemInterface.download("./" + CREATOR);         // download the file
        FileSystemInterface.upload(encFiles);                       // backup

        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "\\" + FILENAME + ".file" )));
        Assert.assertTrue(Files.exists(Paths.get(CREATOR + "\\" + FILENAME + 0 + "oldv" + ".file" )));


    }*/


    @After
    public void cleanUp() throws IOException {
        File index = new File("./" + CREATOR);
        if(index.exists())
            FileUtils.deleteDirectory(index);
    }
}
