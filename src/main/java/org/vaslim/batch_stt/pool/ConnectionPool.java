package org.vaslim.batch_stt.pool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.vaslim.batch_stt.model.InferenceInstance;
import org.vaslim.batch_stt.repository.InferenceInstanceRepository;
import org.vaslim.whisper_asr.client.api.EndpointsApi;
import org.vaslim.whisper_asr.invoker.ApiClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ConnectionPool {
    private final Map<String, EndpointsApi> connections = new HashMap<>();

    private final InferenceInstanceRepository inferenceInstanceRepository;

    @Autowired
    public ConnectionPool(@Value("${whisper.api.urls}") String[] apiUrls, InferenceInstanceRepository inferenceInstanceRepository) {
        this.inferenceInstanceRepository = inferenceInstanceRepository;
        for (String url : apiUrls) {
            ApiClient apiClient = new ApiClient();
            apiClient.setBasePath(url);
            connections.put(url, new EndpointsApi(apiClient));
        }
        refreshUrlsFromDatabase();
    }

    public synchronized void refreshUrlsFromDatabase() {
        Set<InferenceInstance> inferenceInstanceSet = inferenceInstanceRepository.findAllByAvailableIsTrue();
        inferenceInstanceSet.forEach(inferenceInstance -> {
            ApiClient apiClient = new ApiClient();
            apiClient.setBasePath(inferenceInstance.getInstanceUrl());
            connections.put(inferenceInstance.getInstanceUrl(), new EndpointsApi(apiClient));
        });
    }

    public synchronized EndpointsApi getConnection() {
        EndpointsApi endpointsApi = connections.values().stream().findFirst().orElse(null);
        if(endpointsApi != null){
            connections.remove(endpointsApi.getApiClient().getBasePath());
        }
        return endpointsApi;
    }

    public synchronized void addConnection(String url) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(url);
        connections.put(url, new EndpointsApi(apiClient));
    }

    public synchronized void addConnection(EndpointsApi endpointsApi) {
        connections.put(endpointsApi.getApiClient().getBasePath(), endpointsApi);
    }

    public synchronized void removeConnection(String url) {
        connections.remove(url);
    }
}