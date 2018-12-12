import client.localFileHandler.FileManager;
import client.localFileHandler.FileWrapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import static javafx.scene.input.KeyCode.F;

public class FileManagerTests {

    FileWrapper file;
    @Before
    public void setUp(){
        file = new FileWrapper();
        file.setFileName("filename.test");
        file.setFileCreator("testCreator");
        byte[] b = new byte[50];
        new Random().nextBytes(b);
        file.setFile(b);
    }

    @Test
    public void successfulSave() throws IOException {
        FileManager.saveFile(file);
        Path filePath = Paths.get(file.getFileName());

        Assert.assertTrue(Files.exists(filePath));
    }

    @Test
    public void successfulLoad() throws IOException {
        FileManager.saveFile(file);
        FileWrapper loadedFile =FileManager.loadFile(file.getFileName(), file.getFileCreator());

        Assert.assertEquals(file.getFileCreator(), loadedFile.getFileCreator());
        Assert.assertEquals(file.getFileName(), loadedFile.getFileName());
        Assert.assertArrayEquals(file.getFile(), loadedFile.getFile());
    }

    @After
    public void cleanUp() throws IOException {
        Path fileToDeletePath = Paths.get(file.getFileName());
        Files.deleteIfExists(fileToDeletePath);
    }
}
