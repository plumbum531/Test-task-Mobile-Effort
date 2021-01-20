package com.svichkar;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FileReaderWriter implements InputOutputInterface {
    private final CreateFilePath createFilePath;

    public FileReaderWriter(CreateFilePath createFilePath) {
        this.createFilePath = createFilePath;
    }

    public void writeFile(List<Result> resultList, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Result result : resultList) {
                writer.write(result.getPath() + ";" + result.getCount() + ";");
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Exception of write file " + e.getLocalizedMessage());
        }
    }

    public Optional<Map<Integer, File>> readFile(File inputFileName) {
        Map<Integer, File> folderNameMap = new LinkedHashMap<>();
        int serialNumber = 0;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFileName))) {
            while (bufferedReader.ready()) {
                File fileName = createFilePath.createFileObject(bufferedReader.readLine().trim());
                if (fileName.exists() & !folderNameMap.containsValue(fileName)) {
                    folderNameMap.put(serialNumber++, fileName);
                } else {
                    System.out.println("Duplicate file way or file not found: " + fileName.toString());
                }
            }
            if (folderNameMap.size() != 0) {
                return Optional.of(folderNameMap);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File " + inputFileName.toString() + " not found: " + e.getLocalizedMessage());
        } catch (IOException e) {
            System.out.println("File " + inputFileName.toString() + " read with error: " + e.getLocalizedMessage());
        }
        return Optional.empty();
    }
}
