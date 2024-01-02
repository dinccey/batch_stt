package org.vaslim.batch_stt.service.impl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.vaslim.batch_stt.dto.InferenceInstanceDTO;
import org.vaslim.batch_stt.exception.BatchSttException;
import org.vaslim.batch_stt.model.AppUser;
import org.vaslim.batch_stt.model.InferenceInstance;
import org.vaslim.batch_stt.repository.AppUserRepository;
import org.vaslim.batch_stt.repository.InferenceInstanceRepository;
import org.vaslim.batch_stt.service.InferenceInstanceService;

import java.io.IOException;
import java.net.*;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InferenceInstanceServiceImpl implements InferenceInstanceService {

    private static final Logger logger = LoggerFactory.getLogger(InferenceInstanceServiceImpl.class);
    private final AppUserRepository appUserRepository;

    private final InferenceInstanceRepository inferenceInstanceRepository;

    private final ModelMapper modelMapper;
    public InferenceInstanceServiceImpl(AppUserRepository appUserRepository, InferenceInstanceRepository inferenceInstanceRepository, ModelMapper modelMapper) {
        this.appUserRepository = appUserRepository;
        this.inferenceInstanceRepository = inferenceInstanceRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public InferenceInstanceDTO addInferenceInstance(InferenceInstanceDTO inferenceInstanceDTO, String username) {
        InferenceInstance inferenceInstance = modelMapper.map(inferenceInstanceDTO, InferenceInstance.class);
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(()-> new BatchSttException("User not found"));
        inferenceInstance.setAppUser(appUser);
        inferenceInstance.setItemsProcessed(0);
        inferenceInstance.setFailedRunsCount(0);
        return modelMapper.map(inferenceInstanceRepository.save(inferenceInstance), InferenceInstanceDTO.class);
    }

    @Override
    public InferenceInstanceDTO removeInferenceInstance(InferenceInstanceDTO inferenceInstanceDTO, String username) {
        InferenceInstance inferenceInstance = inferenceInstanceRepository.findByInstanceUrl(inferenceInstanceDTO.getInstanceUrl())
                .orElseThrow(() -> new BatchSttException("Inference instance not found."));

        inferenceInstanceRepository.delete(inferenceInstance);
        return inferenceInstanceDTO;
    }

    @Override
    public InferenceInstanceDTO disableInferenceInstance(InferenceInstanceDTO inferenceInstanceDTO, String username) {
        InferenceInstance inferenceInstance = inferenceInstanceRepository.findByInstanceUrl(inferenceInstanceDTO.getInstanceUrl())
                .orElseThrow(() -> new BatchSttException("Inference instance not found."));
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(()-> new BatchSttException("User not found"));
        inferenceInstance.setAppUser(appUser);
        inferenceInstance.setAvailable(false);

        return modelMapper.map(inferenceInstanceRepository.save(inferenceInstance), InferenceInstanceDTO.class);
    }

    @Override
    public InferenceInstanceDTO enableInferenceInstance(InferenceInstanceDTO inferenceInstanceDTO, String username) {
        InferenceInstance inferenceInstance = inferenceInstanceRepository.findByInstanceUrl(inferenceInstanceDTO.getInstanceUrl())
                .orElseThrow(() -> new BatchSttException("Inference instance not found."));
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(()-> new BatchSttException("User not found"));
        inferenceInstance.setAppUser(appUser);
        inferenceInstance.setAvailable(true);

        return modelMapper.map(inferenceInstanceRepository.save(inferenceInstance), InferenceInstanceDTO.class);
    }

    @Override
    public Set<InferenceInstanceDTO> getAll(String username) {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(()-> new BatchSttException("User not found"));
        Set<InferenceInstance> inferenceInstances = inferenceInstanceRepository.findAllByAppUser(appUser);

        return inferenceInstances.stream().map(inferenceInstance -> modelMapper.map(inferenceInstance, InferenceInstanceDTO.class)).collect(Collectors.toSet());
    }

    @Override
    public Boolean checkIsReachable(String basePath) {
        final int timeout = 2000;
        try {
//            boolean reachable = InetAddress.getByAddress(getBytesHostFromUrl(basePath)).isReachable(timeout);
//            if(!reachable) throw new IOException("Address not reachable.");
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(getHostFromUrl(basePath), getPortFromUrl(basePath)), timeout);
            URL url = new URL(basePath+"/docs");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(timeout);
            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            //logger.info("Instance " + basePath + " failed online check with exception: " + e.getMessage());
            return false;
        }
    }

    private int getPortFromUrl(String basePath) {
        basePath = stripPrefix(basePath);

        return Integer.parseInt(basePath.split(":")[1]);
    }


    private String getHostFromUrl(String basePath) {
        basePath = stripPrefix(basePath);

        return basePath.split(":")[0];
    }

    private byte[] getBytesHostFromUrl(String basePath) {
        basePath = stripPrefix(basePath);
        String addr = basePath.split(":")[0];

        String[] ipStr = addr.split("\\.");
        byte[] bytes = new byte[ipStr.length];
        for (int i = 0; i < ipStr.length; i++) {
            bytes[i] = Integer.valueOf(ipStr[i], 10).byteValue();
        }
        return bytes;
    }

    private String stripPrefix(String basePath) {
        final String HTTP = "http://";
        final String HTTPS = "https://";

        if(basePath.startsWith(HTTP)){
            basePath = basePath.replace(HTTP, "");
        }
        else if(basePath.startsWith(HTTPS)){
            basePath = basePath.replace(HTTPS, "");
        }
        return basePath;
    }
}
