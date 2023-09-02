package org.vaslim.batch_stt.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

@Service
public interface FileService {

    //receive video/audio file as input, send it to whisper ai and return the resulting text file as OutputStream
    File processFile(File file) throws IOException;

    void findUnprocessedFiles(Path path);

    File extractAudio(File videoFile) throws IOException;
}
