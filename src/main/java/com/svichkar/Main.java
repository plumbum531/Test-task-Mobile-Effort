package com.svichkar;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class Main {
    public static void main(String[] args) {
        if (args.length == 2) {
            new Main().application(args);
        } else {
            System.out.println("Input arguments not found.");
        }
    }

    private void application(String[] args) {
        CreateFilePath createFilePath = new CreateFilePath();
        File inputFileObject = createFilePath.createFileObject(args[0]);
        if (inputFileObject.exists()) {
            FileReaderWriter fileReaderWriter = new FileReaderWriter(new CreateFilePath());
            Optional<Map<Integer, File>> folderCollection = fileReaderWriter.readFile(inputFileObject);
            if (folderCollection.isPresent()) {
                Optional<List<Result>> result = new ThreadWorker().createThreads(folderCollection.get());
                if (result.isPresent()) {
                    printToScreen(result.get());
                    writeToFile(result.get(), fileReaderWriter, args[1]);
                }
            } else {
                System.out.println("Direction set not found.");
            }
        } else {
            System.out.println("Input file not found.");
        }
    }

    private void printToScreen(List<Result> resultList) {
        System.out.println("Result:");
        System.out.println("-----------------------------------------");
        System.out.printf("%6s\t %8s\t %s\n", " O/n", "Quantity", "Path");
        resultList.forEach(p -> System.out.printf("%6d\t %8d\t %s\n", p.getSerialNumber(), p.getCount().get(), p.getPath()));
        System.out.println("-----------------------------------------");
    }

    private void writeToFile(List<Result> resultList, FileReaderWriter fileReaderWriter, String fileName) {
        CreateFilePath createFilePath = new CreateFilePath();
        File fileObject = createFilePath.createFileObject(fileName);
        if (fileObject.exists()) {
            fileReaderWriter.writeFile(resultList, fileName);
        } else {
            if (createFilePath.createFileFromFileName(fileObject)) {
                fileReaderWriter.writeFile(resultList, fileName);
            }
        }
    }
}


