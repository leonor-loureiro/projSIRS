package authserver;

import authserver.exception.InvalidUserException;
import authserver.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService = AuthService.getInstance();

    /**
     * Maps a login request from an user
     * @param credentials username and password of the user
     * @return authentication token
     */
    @RequestMapping(value = "/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {

        //Extract params
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Validate params
        if(username == null || username.isEmpty())
            return new ResponseEntity<String>("Invalid username", HttpStatus.BAD_REQUEST);

        if(password == null || password.isEmpty())
            return new ResponseEntity<String>("Invalid password", HttpStatus.BAD_REQUEST);

        String jwtToken = null;

        try {
            jwtToken = authService.login(username, password);
        } catch (InvalidUserException iue) {
            return new ResponseEntity<String>(iue.getMessage(), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<String>(jwtToken, HttpStatus.OK);
    }

    /**
     * Maps a register request from an user
     * @param credentials username, password and public key of the user
     * @return authentication token
     */
    @RequestMapping(value = "/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> credentials) {

        //Extract params
        String username = credentials.get("username");
        String password = credentials.get("password");
        String Kpub = credentials.get("Kpub");

        //Validate params
        if(username == null || username.isEmpty())
            return new ResponseEntity<String>("Invalid username", HttpStatus.BAD_REQUEST);

        if(password == null || password.isEmpty())
            return new ResponseEntity<String>("Invalid password", HttpStatus.BAD_REQUEST);

        if(Kpub == null || Kpub.isEmpty())
            return new ResponseEntity<String>("Invalid public key", HttpStatus.BAD_REQUEST);

        String jwtToken = null;

        try {
            jwtToken = authService.register(username, password, Kpub);
        } catch (UserAlreadyExistsException uaee) {
            return new ResponseEntity<String>(uaee.getMessage(), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<String>(jwtToken, HttpStatus.OK);
    }

    /**
     * Maps a public key request from an user
     * @param params username1 (sharer of the file), username2 (destination of the file), token
     * @return public key of username2
     */
    @RequestMapping(value = "/getPublicKey")
    public ResponseEntity<String> getPublicKey(@RequestBody Map<String, String> params) {
        String username1 = params.get("username1");
        String username2 = params.get("username2");
        String token = params.get("token");

        if(username1 == null || username1.isEmpty())
            return new ResponseEntity<String>("Username1 is invalid", HttpStatus.BAD_REQUEST);


        if(username2 == null || username2.isEmpty())
            return new ResponseEntity<String>("Username2 is invalid", HttpStatus.BAD_REQUEST);


        if(token == null || token.isEmpty())
            return new ResponseEntity<String>("Token is invalid", HttpStatus.PRECONDITION_FAILED);


        if(authService.authenticate(username1, token)) {
            try {
                String publicKey = authService.getPublicKey(username2);
                //System.out.println(publicKey);
                return new ResponseEntity<String>( publicKey, HttpStatus.OK);

            } catch (InvalidUserException e) {
                return new ResponseEntity<String>("Username " + username2 +" does not exist", HttpStatus.CONFLICT);
            }
        }

        return new ResponseEntity<String>("Token expired", HttpStatus.PRECONDITION_FAILED);
    }
}
