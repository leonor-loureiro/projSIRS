package authserver;

import authserver.exception.InvalidUserException;
import authserver.exception.UserAlreadyExistsException;
import crypto.exception.CryptoException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService = AuthService.getInstance();

    @RequestMapping(value = "/test")
    public String test(){
        return "works";
    }

    @RequestMapping(value = "/login")
    public String login(@RequestBody Map<String, String> credentials) throws ServletException {

        //Extract params
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Validate params
        if(username == null || username.isEmpty())
            throw new ServletException("Invalid username");

        if(password == null || password.isEmpty())
            throw new ServletException("Invalid password");

        String jwtToken = null;

        try {
            jwtToken = authService.login(username, password);
        } catch (InvalidUserException iue) {
            throw new ServletException(iue.getMessage());
        }

        return jwtToken;
    }

    @RequestMapping(value = "/register")
    public String register(@RequestBody Map<String, String> credentials) throws ServletException {

        //Extract params
        String username = credentials.get("username");
        String password = credentials.get("password");
        String Kpub = credentials.get("Kpub");

        //Validate params
        if(username == null || username.isEmpty())
            throw new ServletException("Invalid username");

        if(password == null || password.isEmpty())
            throw new ServletException("Invalid password");

        if(Kpub == null || Kpub.isEmpty())
            throw new ServletException("Invalid public key");

        String jwtToken = null;

        try {
            jwtToken = authService.register(username, password, Kpub);
        } catch (UserAlreadyExistsException uaee) {
            throw new ServletException(uaee.getMessage());
        }

        return jwtToken;
    }

    @RequestMapping(value = "/getPublicKey")
    public String getPublicKey(@RequestBody Map<String, String> params) throws ServletException {
        String username1 = params.get("username1");
        String username2 = params.get("username2");
        String token = params.get("token");

        if(username1 == null || username1.isEmpty())
            throw new ServletException("Username1 is invalid");


        if(username2 == null || username2.isEmpty())
            throw new ServletException("Username 2 is invalid");


        if(token == null || token.isEmpty())
            throw new ServletException("Token is invalid");


        if(authService.authenticate(username1, token)) {
            try {
                return authService.getPublicKey(username2);
            } catch (InvalidUserException e) {
                throw  new ServletException("Username " + username2 +" does not exist");
            }
        }

        throw new ServletException("Token expired");

    }
}
