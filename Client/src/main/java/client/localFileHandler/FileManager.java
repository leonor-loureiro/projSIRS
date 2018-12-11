package client.localFileHandler;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Interface FileManager
 * Handles all local file storage and loading functions
 */
public interface FileManager {
    String path ="./";

    /**
     * Stores locally the given file and it's encrypting key
     * @param file the fileWrapper to be saved
     * @throws IOException an IOException maybe thrown if file already exists with that name
     */
    static void saveFile(FileWrapper file) throws IOException {

        File dest = new File(path + file.getFileName());
        if(!dest.createNewFile()){
            dest.delete();
            dest.createNewFile();
        }

        System.out.println(Arrays.toString(file.getFile()));
        try (FileOutputStream stream = new FileOutputStream(dest)) {
            stream.write(file.getFile());
        }

        // Store the key
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
     * loads a local file with a given name (should include extension)
     * @param name name should include extension
     * @return FileWrapper with name found
     */
    static FileWrapper loadFile(String name, String username) throws IOException {

        FileWrapper temp = new FileWrapper();
        // Will be null if path is invalid

        File[] files = new File(path).listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().equals(name)) {
                    //temp.setFile(getFileC ontent(file));
                    temp.setFile(Files.readAllBytes(file.toPath()));
                    temp.setFileName(file.getName());
                    temp.setFileCreator(username);

                    //TODO: get FileKey if it's stored

                    return temp;
                }
            }
        }

        return null;
    }

    static byte[] getFileContent(File inputFile){
        FileInputStream inputStream = null;
        byte[] file = null;
        try {
            inputStream = new FileInputStream(inputFile);
            file = new byte[(int) inputFile.length()];
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return file;
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
                    temp.setFile(getFileContent(file));
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
