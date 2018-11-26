package client.security;

import java.security.Key;
import java.util.List;

public interface SecurityHandler {

    static List<Key> generateKeyPair(){
        throw new UnsupportedOperationException();
    }

    static Key generateSyncKey(){
        throw new UnsupportedOperationException();
    }

    static Object cipherObject (Object object, Key key){
        throw new UnsupportedOperationException();
    }
    static Object decipherObject (Object objet, Key key){
        throw new UnsupportedOperationException();
    }

    static Key loadPrivateKey(){
        throw new UnsupportedOperationException();
    }

    static void storePrivateKey(Key key){
        throw new UnsupportedOperationException();
    }


    static  byte[] generateMAC( byte[] content, Key key){

        throw new UnsupportedOperationException();
    }
}
