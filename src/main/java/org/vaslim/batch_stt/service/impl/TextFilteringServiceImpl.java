package org.vaslim.batch_stt.service.impl;

import org.springframework.stereotype.Service;
import org.vaslim.batch_stt.model.Item;
import org.vaslim.batch_stt.repository.ItemRepository;
import org.vaslim.batch_stt.service.TextFilteringService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TextFilteringServiceImpl implements TextFilteringService {

    private final ItemRepository itemRepository;

    public TextFilteringServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void processTextFiles(Map<String, String> filterMap) {
        String hash = generateFilterMapHash(filterMap);
        List<Item> itemsToProcess = itemRepository.findByTextFilterHashNotLikeOrTextFilterHashIsNull(hash);

        itemsToProcess.forEach(item-> {
            try {
                String content = Files.readString(Paths.get(item.getFilePathText()));
                Files.writeString(Paths.get(item.getFilePathText()+"+" + hash), content); //save original file as backup, under the name of the hash that will process it
                String filteredContent = replaceOccurrences(content, filterMap);
                Files.writeString(Paths.get(item.getFilePathText()), filteredContent);
                item.setTextFilterHash(hash);
                itemRepository.save(item);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String replaceOccurrences(String content, Map<String,String> filterMap) {
        for (Map.Entry<String, String> entry : filterMap.entrySet()) {
            content = content.replaceAll("(?i)" + entry.getKey(), entry.getValue());
        }
        return content;
    }

    @Override
    public Map<String, String> loadFilterMap(String path) {
        Map<String, String> map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    map.put(key, value);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    @Override
    public String generateFilterMapHash(Map<String, String> filterMap) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sb = new StringBuilder();
        for (String value : filterMap.values()) {
            sb.append(value);
        }
        byte[] hashBytes = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
        StringBuilder hashSb = new StringBuilder();
        for (byte b : hashBytes) {
            hashSb.append(String.format("%02x", b));
        }
        return hashSb.toString();
    }

}
