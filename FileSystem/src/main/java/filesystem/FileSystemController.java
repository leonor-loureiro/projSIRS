package filesystem;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/operations")
public class FileSystemController {

 /*   @PostMapping(path = "/newfile")
    public void newFile(@Valid @RequestBody FileWrapper file){
        System.out.println("Got to restcontroller");
        FileSystemInterface.newfile(file);
    } */
    @RequestMapping(value = "/download")
    public void download(){
        FileSystemInterface.download();
    }

    @PostMapping(value = "/upload")
    public void upload(@Valid @RequestBody FileWrapper[] files) throws IOException {

        FileSystemInterface.upload(files);
    }

    @RequestMapping(value = "/share")
    public void share(){
        FileSystemInterface.share();
    }

}

