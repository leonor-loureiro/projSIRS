package filesystem;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public List<EncryptedFileWrapper> download(@Valid @RequestBody String name) throws IOException, ClassNotFoundException {
        return FileSystemInterface.download(name);
    }

    @PostMapping(value = "/upload")
    public void upload(@Valid @RequestBody List<EncryptedFileWrapper> files) throws IOException {

//        System.out.println(file.get(0).getFileName());
//        System.out.println(Arrays.toString(file.get(0).getFile()));
        FileSystemInterface.upload(files);
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

