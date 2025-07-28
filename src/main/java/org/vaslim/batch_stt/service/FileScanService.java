package org.vaslim.batch_stt.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public interface FileScanService {
    List<File> getNext();

    void reset();
}
