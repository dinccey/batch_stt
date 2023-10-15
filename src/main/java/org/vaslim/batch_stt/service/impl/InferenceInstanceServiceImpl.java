package org.vaslim.batch_stt.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.vaslim.batch_stt.dto.InferenceInstanceDTO;
import org.vaslim.batch_stt.exception.BatchSttException;
import org.vaslim.batch_stt.model.AppUser;
import org.vaslim.batch_stt.model.InferenceInstance;
import org.vaslim.batch_stt.repository.AppUserRepository;
import org.vaslim.batch_stt.repository.InferenceInstanceRepository;
import org.vaslim.batch_stt.service.InferenceInstanceService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InferenceInstanceServiceImpl implements InferenceInstanceService {

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
        try {
            URL url = new URL(basePath+"/docs");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            return false;
        }
    }
}
