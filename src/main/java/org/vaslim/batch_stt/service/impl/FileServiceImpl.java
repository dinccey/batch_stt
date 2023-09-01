package org.vaslim.batch_stt.service.impl;

import org.vaslim.batch_stt.service.FileService;
import org.vaslim.whisper_asr.client.api.EndpointsApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileServiceImpl implements FileService {

    private final EndpointsApi endpointsApi;

    public FileServiceImpl(EndpointsApi endpointsApi) {
        this.endpointsApi = endpointsApi;
    }

    @Override
    public OutputStream processFile(InputStream inputStream) throws IOException {
        //TODO use ASR api
        //endpointsApi.asrAsrPost(inputStream,)

        return null;
    }
}
