package org.vaslim.batch_stt.pool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.vaslim.whisper_asr.client.api.EndpointsApi;
import org.vaslim.whisper_asr.invoker.ApiClient;

import java.util.HashMap;
import java.util.Map;

@Component
public class ConnectionPool {
    private final Map<String, EndpointsApi> connections = new HashMap<>();

    @Autowired
    public ConnectionPool(@Value("${whisper.api.urls}") String[] apiUrls) {
        for (String url : apiUrls) {
            ApiClient apiClient = new ApiClient();
            apiClient.setBasePath(url);
            connections.put(url, new EndpointsApi(apiClient));
        }
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