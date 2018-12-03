import client.localFileHandler.FileWrapper;
import client.security.EncryptedFileWrapper;
import client.security.SecurityHandler;
import client.security.exception.FileCorrupted;
import crypto.Crypto;
import crypto.exception.CryptoException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.security.KeyPair;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SecurityHandlerTest {

    private Random random = new Random();
    String fileName = "fileName.txt";
    String creator = "user1";
    byte[] fileData = new byte[200];
    byte[] key;
    FileWrapper file;

    KeyPair keyPair;
    @Before
    public void setUp() {
        Crypto.init();
        try {
            keyPair = Crypto.generateRSAKeys();
        } catch (CryptoException e) {
            e.printStackTrace();
        }

        //Generate the file
        file = new FileWrapper();
        file.setFileName(fileName);
        file.setFileCreator(creator);
        random.nextBytes(fileData);
        file.setFile(fileData);
        try {
            key = Crypto.generateSecretKey().getEncoded();
            file.setFileKey(key);
        } catch (CryptoException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void success(){
        // Encrypt the fail
        EncryptedFileWrapper enc = SecurityHandler.encryptFileWrapper(file, keyPair.getPublic());

        // Decrypt the file
        try {
            FileWrapper dec = SecurityHandler.decryptFileWrapper(enc, keyPair.getPrivate());

            //Check if decryption is correct
            assertEquals(dec.getFileName(), file.getFileName());
            assertEquals(dec.getFileCreator(), file.getFileCreator());
            assertArrayEquals(dec.getFile(), file.getFile());
            assertArrayEquals(dec.getFileKey(), file.getFileKey());

        } catch (FileCorrupted fileCorrupted) {
            Assert.fail();
        }
    }

    @Test (expected = FileCorrupted.class)
    public void corruptedFileName() throws FileCorrupted {

        // Encrypt file
        EncryptedFileWrapper enc = SecurityHandler.encryptFileWrapper(file, keyPair.getPublic());

        //Corrupt file name
        enc.setFileName("corruptedFileName");

        // Decrypt file
        FileWrapper dec = SecurityHandler.decryptFileWrapper(enc, keyPair.getPrivate());
    }


    @Test (expected = FileCorrupted.class)
    public void corruptedCreator() throws FileCorrupted {

        // Encrypt file
        EncryptedFileWrapper enc = SecurityHandler.encryptFileWrapper(file, keyPair.getPublic());

        //Corrupt file name
        enc.setFileCreator("corruptedCreator");

        // Decrypt file
        FileWrapper dec = SecurityHandler.decryptFileWrapper(enc, keyPair.getPrivate());
    }

    @Test (expected = FileCorrupted.class)
    public void corruptedFileData() throws FileCorrupted {

        // Encrypt file
        EncryptedFileWrapper enc = SecurityHandler.encryptFileWrapper(file, keyPair.getPublic());

        // Corrupt file data
        byte[] corruptedData = new byte[200];
        random.nextBytes(corruptedData);
        enc.setFile(corruptedData);

        // Decrypt file
        FileWrapper dec = SecurityHandler.decryptFileWrapper(enc, keyPair.getPrivate());
    }

    @Test (expected = FileCorrupted.class)
    public void corruptedFileKey() throws FileCorrupted {

        // Encrypt file
        EncryptedFileWrapper enc = SecurityHandler.encryptFileWrapper(file, keyPair.getPublic());

        // Corrupt file data
        byte[] corruptedKey = new byte[200];
        random.nextBytes(corruptedKey);
        enc.setFileKey(corruptedKey);

        // Decrypt file
        FileWrapper dec = SecurityHandler.decryptFileWrapper(enc, keyPair.getPrivate());
    }

    @Test (expected = FileCorrupted.class)
    public void corruptedMac() throws FileCorrupted {

        // Encrypt file
        EncryptedFileWrapper enc = SecurityHandler.encryptFileWrapper(file, keyPair.getPublic());

        // Corrupt file data
        byte[] array = new byte[enc.getFileMAC().length()]; // length is bounded by 7
        new Random().nextBytes(array);
        String corruptedMac = new String(array, Charset.forName("UTF-8"));
        enc.setFileMAC(corruptedMac);


        // Decrypt file
        FileWrapper dec = SecurityHandler.decryptFileWrapper(enc, keyPair.getPrivate());
    }

    @After
    public void tearDown() {
        keyPair = null;
        file = null;
    }
}
