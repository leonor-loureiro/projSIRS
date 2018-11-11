package client.security;

import java.security.Key;
import java.util.List;

public class SecurityHandler {

    public List<Key> generateKeyPair(){
        throw new UnsupportedOperationException();
    }

    public Key generateSyncKey(){
        throw new UnsupportedOperationException();
    }

    public Object cipherObject (Object object, Key key){
        throw new UnsupportedOperationException();
    }
    public Object decipherObject (Object objet, Key key){
        throw new UnsupportedOperationException();
    }

    public Key loadPrivateKey(){
        throw new UnsupportedOperationException();
    }

    public void storePrivateKey(Key key){
        throw new UnsupportedOperationException();
    }


}
