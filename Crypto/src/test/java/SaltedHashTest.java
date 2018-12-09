import crypto.Crypto;
import crypto.exception.CryptoException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class SaltedHashTest {

    String password = "password";

    @Before
    public void setUp(){
    }

    @Test
    public void success() throws CryptoException {
        String hash = Crypto.hash(password);
        Assert.assertTrue(Crypto.validateHash(password, hash));
    }

    @Test
    public void failure() throws CryptoException {
        String hash = Crypto.hash(password);
        String diffPassword = "differentPassword";
        Assert.assertFalse(Crypto.validateHash(diffPassword, hash));
    }

    @Test(expected = CryptoException.class)
    public void emptyPassword() throws CryptoException {
        Crypto.hash(null);
    }

    @Test(expected = CryptoException.class)
    public void emptyPasswordValidation() throws CryptoException {
        String hash = Crypto.hash(password);
        Crypto.validateHash(null, hash);
    }

    @Test(expected = CryptoException.class)
    public void emptyHashValidation() throws CryptoException {
        Crypto.hash(password);
        Crypto.validateHash(password, null);
    }
}
