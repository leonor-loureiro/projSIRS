package filesystem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operations")
public class FileSystemController {

    @RequestMapping(value = "/createfile")
    public void newFile(){
        FileSystemInterface.newfile();
    }
    @RequestMapping(value = "/download")
    public void download(){
        FileSystemInterface.download();
    }

    @RequestMapping(value = "/upload")
    public void upload(){
        FileSystemInterface.upload();
    }

    @RequestMapping(value = "/share")
    public void share(){
        FileSystemInterface.share();
    }

}

