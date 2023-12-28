package org.vaslim.batch_stt.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.vaslim.batch_stt.dto.InferenceInstanceDTO;
import org.vaslim.batch_stt.dto.StatisticsDTO;
import org.vaslim.batch_stt.model.AppUser;
import org.vaslim.batch_stt.model.InferenceInstance;
import org.vaslim.batch_stt.pool.ConnectionPool;
import org.vaslim.batch_stt.repository.AppUserRepository;
import org.vaslim.batch_stt.repository.InferenceInstanceRepository;
import org.vaslim.batch_stt.repository.ItemRepository;
import org.vaslim.batch_stt.service.StatisticsService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final InferenceInstanceRepository inferenceInstanceRepository;

    private final AppUserRepository appUserRepository;

    private final ItemRepository itemRepository;

    private final ModelMapper modelMapper;

    private final ConnectionPool connectionPool;

    public StatisticsServiceImpl(InferenceInstanceRepository inferenceInstanceRepository, AppUserRepository appUserRepository, ItemRepository itemRepository, ModelMapper modelMapper, ConnectionPool connectionPool) {
        this.inferenceInstanceRepository = inferenceInstanceRepository;
        this.appUserRepository = appUserRepository;
        this.itemRepository = itemRepository;
        this.modelMapper = modelMapper;
        this.connectionPool = connectionPool;
    }

    @Override
    public StatisticsDTO getUserStatistics(String username) {
        AppUser appUser = appUserRepository.findByUsername(username).orElse(null);
        Set<InferenceInstanceDTO> inferenceInstanceDTOSet = inferenceInstanceRepository.findAllByAppUser(appUser).stream().map(inferenceInstance ->
                modelMapper.map(inferenceInstance, InferenceInstanceDTO.class)).collect(Collectors.toSet());

        StatisticsDTO statisticsDTO = new StatisticsDTO();
        statisticsDTO.setInferenceInstances(inferenceInstanceDTOSet);
        statisticsDTO.setPendingItemsTotal(itemRepository.countItemByFilePathTextIsNull());
        statisticsDTO.setProcessedItemsTotal(itemRepository.countItemByFilePathTextIsNotNull());
        statisticsDTO.setCurrentlyOnlineWorkersCount(connectionPool.getOnlineConnectionsCount());
        statisticsDTO.setCurrentlyActiveWorkersCount(connectionPool.getCurrentlyProcessingCount());
        assert appUser != null;
        statisticsDTO.setItemsProcessed(appUser.getItemsProcessed());

        return statisticsDTO;
    }

    @Override
    public void incrementProcessedItemsPerInstance(String inferenceInstanceUrl) {
        InferenceInstance inferenceInstance = inferenceInstanceRepository.findByInstanceUrl(inferenceInstanceUrl).orElse(null);
        assert inferenceInstance != null;
        Integer itemsProcessed = inferenceInstance.getItemsProcessed();
        itemsProcessed++;
        inferenceInstance.setItemsProcessed(itemsProcessed);

        inferenceInstanceRepository.save(inferenceInstance);

        AppUser appUser = inferenceInstance.getAppUser();
        Set<InferenceInstance> inferenceInstances = inferenceInstanceRepository.findAllByAppUser(appUser);
        appUser.setItemsProcessed(inferenceInstances.stream().mapToInt(InferenceInstance::getItemsProcessed).sum());

        appUserRepository.save(appUser);
    }
}
