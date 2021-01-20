package com.svichkar;

import java.io.File;
import java.io.IOException;

public class CreateFilePath {

    public File createFileObject(String fileName) {
        return new File(new File(fileName).getAbsolutePath().replace("\\", "\\\\"));
    }

    public boolean createFileFromFileName(File fileName) {
        try {
            if (fileName.createNewFile()) {
                System.out.println("File is created with name " + fileName.getName());
                return true;
            }
        } catch (IOException e) {
            System.out.println("Create file with name " + fileName + " with exception " + e.getLocalizedMessage());
        }
        return false;
    }
}
