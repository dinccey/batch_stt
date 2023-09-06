package org.vaslim.batch_stt.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface TextFilteringService {
    void processTextFiles(Map<String, String> filterMap);

    Map<String, String> loadFilterMap(String path);

    String generateFilterMapHash(Map<String, String> filterMap);

}
