package org.vaslim.batch_stt.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileService {

    //receive video/audio file as input, send it to whisper ai and return the resulting text file as OutputStream
    OutputStream processFile(InputStream inputStream) throws IOException;
}
