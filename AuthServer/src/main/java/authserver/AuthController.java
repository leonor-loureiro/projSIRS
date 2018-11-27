package authserver;

import authserver.exception.InvalidUserException;
import authserver.exception.UserAlreadyExistsException;
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

        //Validate params
        if(username == null || username.isEmpty())
            throw new ServletException("Invalid username");

        if(password == null || password.isEmpty())
            throw new ServletException("Invalid password");

        String jwtToken = null;

        try {
            jwtToken = authService.register(username, password);
        } catch (UserAlreadyExistsException uaee) {
            throw new ServletException(uaee.getMessage());
        }

        return jwtToken;
    }
}
