package client.localFileHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface FileManager
 * Handles all local file storage and loading functions
 */
public interface FileManager {
    String path ="./";

    /**
     * Stores locally the given file
     * @param file the fileWrapper to be saved
     * @throws IOException an IOException maybe thrown if file already exists with that name
     */
    static void saveFile(FileWrapper file) throws IOException {

        File dest = new File(path + file.getFileName());

        // Try-with-resource in java is a try statement that closes the resources at the end, it works
        // like try catch finally were you close resources on finally statement
        try (InputStream is = new FileInputStream(file.getFile()); OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            // writes file buffer into destinationFile buffer
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    /**
     * Stores locally all the files given in the list
     * @param files list of files to be saved
     * @throws IOException if it was impossible to write any of the files
     */
    static void saveFiles(List<FileWrapper> files) throws IOException {
        for(FileWrapper file : files){
            saveFile(file);
        }
    }

    /**
     * loads a file with a given name (should include extension
     * @param name name should include extension
     * @return FileWrapper with name found
     */
    static FileWrapper loadFile(String name){

        FileWrapper temp = new FileWrapper();
        // Will be null if path is invalid

        File[] files = new File(path).listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().equals(name)) {
                    temp.setFile(file);
                    temp.setFileName(file.getName());
                    return temp;
                }
            }
        }

        return null;
    }

    /**
     * Loads all files found into a list of file wrappers
     * @return all files as FileWrappers
     */
    static List<FileWrapper> loadFiles(){

        List<FileWrapper> results = new ArrayList<>();
        // Will be null if path is invalid
        File[] files = new File(path).listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    FileWrapper temp = new FileWrapper();
                    temp.setFile(file);
                    temp.setFileName(file.getName());
                    results.add(temp);
                }
            }
        }
        return results;
    }

    /**
     * Finds the name (with extensions included) of the files in the folder
     * @return name of all files in folder
     */
    static List<String> listFileNames(){

        List<String> results = new ArrayList<>();

        // Will be null if path is invalid
        File[] files = new File("./").listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    results.add(file.getName());
                }
            }
        }

        return results;
    }


}
