package org.vaslim.batch_stt.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vaslim.batch_stt.service.FileScanService;

import java.io.File;
import java.util.*;

@Service
public class FileScanServiceImpl implements FileScanService {

    private static final Logger logger = LoggerFactory.getLogger(FileScanServiceImpl.class);

    @Value("${files.iteration.size}")
    private Integer size; // number of items to return

    Iterator<File> firstIterator;
    private Set<Iterator<File>> iterators = new HashSet<>();

    @Value("${filesystem.path}")
    private String path;

    @Value("${excluded.paths}")
    private String[] excludedPaths;

    public FileScanServiceImpl(@Value("${filesystem.path}") String path) {
        // list of files in the path
        File rootDirectory = new File(path);
        addIterators(iterators, rootDirectory);
    }

    public List<File> getNext() {
        List<File> result = new ArrayList<>(); // create a result list
        iterators.forEach(iterator -> {
            addFiles(iterator, result, size);
        });
        //logger.info("Get next result size: " + result.size() + " Iterators count: " + iterators.size());
        return result; // return the result
    }

    private void addIterators(Set<Iterator<File>> iterators, File directory) {
        File[] filesInDirectory = directory.listFiles(); // get all files in the directory
        assert filesInDirectory != null;
        if(excludedPaths == null || Arrays.stream(excludedPaths).noneMatch(directory.getAbsolutePath()::startsWith)){
            iterators.add(Arrays.asList(filesInDirectory).iterator());
        }
        for (File file : filesInDirectory) { // for each file in the directory
            if (file.isDirectory()) { // if the file is a directory
                addIterators(iterators, file);
            }
        }
    }



    private void addFiles(Iterator<File> iterator, List<File> result, int size) {
        while (iterator.hasNext() && result.size() < size) { // loop until iterator is exhausted or size is reached
            File nextFile = iterator.next(); // get the next file
            if (result.size() < size) { // if the file is not a directory and size is not reached
                result.add(nextFile); // add the next file to the result
            }
        }
    }


    @Override
    public void reset() {
        File rootDirectory = new File(path);
        this.iterators.clear();
        addIterators(iterators, rootDirectory);
        logger.info("Reset iterators.");
    }
}