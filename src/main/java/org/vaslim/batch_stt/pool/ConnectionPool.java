package org.vaslim.batch_stt.pool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.vaslim.batch_stt.model.InferenceInstance;
import org.vaslim.batch_stt.repository.InferenceInstanceRepository;
import org.vaslim.whisper_asr.client.api.EndpointsApi;
import org.vaslim.whisper_asr.invoker.ApiClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConnectionPool {
    private final Map<String, EndpointsApi> connections = new HashMap<>();
    private final Map<String, EndpointsApi> connectionsActive = new HashMap<>();

    private final InferenceInstanceRepository inferenceInstanceRepository;

    private int availableConnectionsCount;

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
        List<InferenceInstance> inferenceInstanceSet = inferenceInstanceRepository.findAll();
        connections.clear();
        inferenceInstanceSet.forEach(inferenceInstance -> {
            if (inferenceInstance.getAvailable()){
                ApiClient apiClient = new ApiClient();
                apiClient.setBasePath(inferenceInstance.getInstanceUrl());
                if(connectionsActive.containsKey(inferenceInstance.getInstanceUrl()) && !inferenceInstance.getAvailable()){
                    connectionsActive.remove(inferenceInstance.getInstanceUrl());
                }
                if(!connectionsActive.containsKey(inferenceInstance.getInstanceUrl())){
                    connections.put(inferenceInstance.getInstanceUrl(), new EndpointsApi(apiClient));
                }
            }
        });
        availableConnectionsCount = connections.size();
    }

    public synchronized EndpointsApi getConnection() {
        this.refreshUrlsFromDatabase();
        EndpointsApi endpointsApi = connections.values().stream().findFirst().orElse(null);
        if(endpointsApi != null){
            connections.remove(endpointsApi.getApiClient().getBasePath());
            connectionsActive.put(endpointsApi.getApiClient().getBasePath(), endpointsApi);
        }
        return endpointsApi;
    }

    public synchronized void addConnection(String url) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(url);
        connections.put(url, new EndpointsApi(apiClient));
        connectionsActive.remove(url);
    }

    public synchronized void addConnection(EndpointsApi endpointsApi) {
        connections.put(endpointsApi.getApiClient().getBasePath(), endpointsApi);
    }

    public synchronized void removeConnection(String url) {
        connections.remove(url);
    }

    public int getOnlineConnectionsCount() {
        return availableConnectionsCount;
    }

    public int getCurrentlyProcessingCount(){
        return connectionsActive.size();
    }
}