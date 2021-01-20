package com.svichkar;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InputOutputInterface {

    void writeFile(List<Result> resultList, String fileName);

    Optional<Map<Integer, File>> readFile(File inputFileName);

}

