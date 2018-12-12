package filesystem;

import filesystem.data.EncryptedFileWrapper;
import filesystem.data.FileSystemMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Map;

@RestController
@RequestMapping("/operations")
public class FileSystemController {

    @RequestMapping(value = "/download")
    public ResponseEntity<FileSystemMessage> download(@Valid @RequestBody FileSystemMessage fMsg) throws IOException, ClassNotFoundException {

        if(!checkInput(fMsg))
            new ResponseEntity(HttpStatus.BAD_REQUEST);

        FileSystemMessage message = new FileSystemMessage();

        //Check if the token is valid

        if(fMsg.getUserName() == null || fMsg.getUserName().isEmpty())
            return new ResponseEntity<FileSystemMessage>(HttpStatus.BAD_REQUEST);
        if(fMsg.getToken() == null || fMsg.getToken().isEmpty())
            return new ResponseEntity<FileSystemMessage>(HttpStatus.BAD_REQUEST);
        if(!FileSystemInterface.validateToken(fMsg.getUserName(), fMsg.getToken()))
            return new ResponseEntity<FileSystemMessage>(HttpStatus.PRECONDITION_FAILED);

        message.setFiles(FileSystemInterface.download(fMsg.getUserName()));
        return new ResponseEntity<FileSystemMessage>(message, HttpStatus.OK);
    }

    @PostMapping(value = "/upload")
    public ResponseEntity upload(@Valid @RequestBody FileSystemMessage fMsg) throws IOException {

        if(fMsg.getUserName() == null || fMsg.getUserName().isEmpty())
            return new ResponseEntity<FileSystemMessage>(HttpStatus.BAD_REQUEST);
        if(fMsg.getToken() == null || fMsg.getToken().isEmpty())
            return new ResponseEntity<FileSystemMessage>(HttpStatus.BAD_REQUEST);

        if(!checkInput(fMsg))
            new ResponseEntity(HttpStatus.BAD_REQUEST);

        if(!FileSystemInterface.validateToken(fMsg.getUserName(), fMsg.getToken()))
            return new ResponseEntity(HttpStatus.PRECONDITION_FAILED);

        FileSystemInterface.upload(fMsg.getFiles(),fMsg.getUserName());

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/share")
    public ResponseEntity share(@Valid@RequestBody FileSystemMessage fMsg) throws IOException, ClassNotFoundException {


        if(fMsg.getUserName() == null || fMsg.getUserName().isEmpty())
            return new ResponseEntity<FileSystemMessage>(HttpStatus.BAD_REQUEST);
        if(fMsg.getToken() == null || fMsg.getToken().isEmpty())
            return new ResponseEntity<FileSystemMessage>(HttpStatus.BAD_REQUEST);

        if(!checkInput(fMsg))
            new ResponseEntity(HttpStatus.BAD_REQUEST);

        if(!FileSystemInterface.validateToken(fMsg.getUserName(), fMsg.getToken()))
            return new ResponseEntity(HttpStatus.PRECONDITION_FAILED);

        if(!FileSystemInterface.share(fMsg.getUserName(),fMsg.getUserToShareWith(),fMsg.getFiles()[0]))
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/getoldversion")
    public ResponseEntity getoldversion(@Valid@RequestBody FileSystemMessage fMsg) throws IOException, ClassNotFoundException {

        if(fMsg.getUserName() == null || fMsg.getUserName().isEmpty())
            return new ResponseEntity<FileSystemMessage>(HttpStatus.BAD_REQUEST);
        if(fMsg.getToken() == null || fMsg.getToken().isEmpty())
            return new ResponseEntity<FileSystemMessage>(HttpStatus.BAD_REQUEST);

        if(!checkInput(fMsg))
            new ResponseEntity(HttpStatus.BAD_REQUEST);

        if(!FileSystemInterface.validateToken(fMsg.getUserName(), fMsg.getToken()))
            return new ResponseEntity(HttpStatus.PRECONDITION_FAILED);

        FileSystemMessage message = new FileSystemMessage();


        message.setFiles(new EncryptedFileWrapper[]{ FileSystemInterface.getOldVersion(fMsg.getUserName(),fMsg.getBackUpFileName(),fMsg.getCorrupted())});
        if(message.getFiles()[0] == null)
            return new ResponseEntity(HttpStatus.CONFLICT);
        message.setUserName(fMsg.getUserName());
        return new ResponseEntity<FileSystemMessage>(message , HttpStatus.OK);
    }


    public Boolean checkInput(FileSystemMessage fMsg){
        if (fMsg.getUserName()!= null ) {
            if(!fMsg.getUserName().matches("[a-zA-Z0-9]*")) {
                System.out.println("Bad username");
                return false;
            }
        }
        if (fMsg.getUserToShareWith()!= null){
            if(!fMsg.getUserToShareWith().matches("[a-zA-Z0-9]*")) {
                System.out.println("Bad username to share with");
                return false;
            }
        }
        if(fMsg.getBackUpFileName()!=null){
            if(!fMsg.getBackUpFileName().matches("[a-zA-Z0-9._-]*"))
                System.out.println("Bad filename for backup");
                return false;
        }
        EncryptedFileWrapper[] files = fMsg.getFiles();

        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                if (!files[i].getFileCreator().matches("[a-zA-Z0-9]*")) {
                    System.out.println("Bad FileCreatorName");
                    return false;
                }
                if (!files[i].getFileName().matches("[a-zA-Z0-9 . _-]*")) {
                    System.out.println("Bad FileName");
                    return false;
                }
            }
        }
        System.out.println("Passed validation");
        return true;
    }
}

