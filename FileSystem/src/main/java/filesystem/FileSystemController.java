package filesystem;

import filesystem.data.EncryptedFileWrapper;
import filesystem.data.FileSystemMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/operations")
public class FileSystemController {

 /*   @PostMapping(path = "/newfile")
    public void newFile(@Valid @RequestBody FileWrapper file){
        System.out.println("Got to restcontroller");
        FileSystemInterface.newfile(file);
    } */
    @RequestMapping(value = "/download")
    public ResponseEntity<FileSystemMessage> download(@Valid @RequestBody FileSystemMessage fMsg) throws IOException, ClassNotFoundException {
        FileSystemMessage m = new FileSystemMessage();

        //Check if the token is valid
        if(FileSystemInterface.validateToken(m.getName(), m.getToken()))
            return new ResponseEntity<FileSystemMessage>(HttpStatus.PRECONDITION_FAILED);

        m.setFiles(FileSystemInterface.download(fMsg.getName()));
        return new ResponseEntity<FileSystemMessage>(m, HttpStatus.OK);
    }

    @PostMapping(value = "/upload")
    public ResponseEntity upload(@Valid @RequestBody FileSystemMessage fMsg) throws IOException {

//        System.out.println(file.get(0).getFileName());
//        System.out.println(Arrays.toString(file.get(0).getFile()));
        //Check if the token is valid
        FileSystemMessage m = new FileSystemMessage();

        if(FileSystemInterface.validateToken(m.getName(), m.getToken()))
            return new ResponseEntity(HttpStatus.PRECONDITION_FAILED);

        FileSystemInterface.upload(fMsg.getFiles());
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/share")
    public ResponseEntity share(@RequestBody Map<String,String> information, EncryptedFileWrapper file) throws IOException, ClassNotFoundException {
        FileSystemMessage m = new FileSystemMessage();

        if(FileSystemInterface.validateToken(m.getName(), m.getToken()))
            return new ResponseEntity(HttpStatus.PRECONDITION_FAILED);

        FileSystemInterface.share(information,file);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/test")
    public String test(){
        return "works";
    }
}

