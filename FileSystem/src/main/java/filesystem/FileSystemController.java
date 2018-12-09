package filesystem;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public FileSystemMessage download(@Valid @RequestBody FileSystemMessage fMsg) throws IOException, ClassNotFoundException {
        FileSystemMessage m = new FileSystemMessage();
        m.setFiles(FileSystemInterface.download(fMsg.getName()));
        return m;
        //return null;
    }

    @PostMapping(value = "/upload")
    public void upload(@Valid @RequestBody FileSystemMessage fMsg) throws IOException {

//        System.out.println(file.get(0).getFileName());
//        System.out.println(Arrays.toString(file.get(0).getFile()));
        FileSystemInterface.upload(fMsg.getFiles());
    }

    @RequestMapping(value = "/share")
    public void share(@RequestBody Map<String,String> information, EncryptedFileWrapper file) throws IOException, ClassNotFoundException {
        FileSystemInterface.share(information,file);
    }

    @RequestMapping(value = "/test")
    public String test(){
        return "works";
    }
}

