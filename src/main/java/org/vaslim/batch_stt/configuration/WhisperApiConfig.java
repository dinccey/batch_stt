package org.vaslim.batch_stt.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaslim.whisper_asr.client.api.EndpointsApi;
import org.vaslim.whisper_asr.invoker.ApiClient;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class WhisperApiConfig {

    //Put into an environment variable
    @Value("${WHISPER_ASR_URL}")
    private String whisperUrl;

    @Bean
    public EndpointsApi endpointsApi(){
        return new EndpointsApi(apiClient());
    }

    @Bean
    public ApiClient apiClient(){
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(whisperUrl);
        return apiClient;
    }
}
