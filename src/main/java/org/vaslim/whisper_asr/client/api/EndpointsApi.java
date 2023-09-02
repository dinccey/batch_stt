package org.vaslim.whisper_asr.client.api;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.vaslim.whisper_asr.invoker.ApiClient;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class EndpointsApi {
    private ApiClient apiClient;

    public EndpointsApi() {
        this(new ApiClient());
    }

    public EndpointsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Asr
     * 
     * <p><b>200</b> - Successful Response
     * <p><b>422</b> - Validation Error
     * @param audioFile  (required)
     * @param task  (optional, default to transcribe)
     * @param language  (optional)
     * @param initialPrompt  (optional)
     * @param encode Encode audio first through ffmpeg (optional, default to true)
     * @param output  (optional, default to txt)
     * @return Object
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public byte[] asrAsrPost(File audioFile, String task, String language, String initialPrompt, Boolean encode, String output) throws RestClientException {
        return asrAsrPostWithHttpInfo(audioFile, task, language, initialPrompt, encode, output).getBody();
    }

    /**
     * Asr
     * 
     * <p><b>200</b> - Successful Response
     * <p><b>422</b> - Validation Error
     * @param audioFile  (required)
     * @param task  (optional, default to transcribe)
     * @param language  (optional)
     * @param initialPrompt  (optional)
     * @param encode Encode audio first through ffmpeg (optional, default to true)
     * @param output  (optional, default to txt)
     * @return ResponseEntity&lt;Object&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<byte[]> asrAsrPostWithHttpInfo(File audioFile, String task, String language, String initialPrompt, Boolean encode, String output) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'audioFile' is set
        if (audioFile == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'audioFile' when calling asrAsrPost");
        }
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "task", task));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "language", language));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "initial_prompt", initialPrompt));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "encode", encode));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "output", output));


        if (audioFile != null)
            localVarFormParams.add("audio_file", new FileSystemResource(audioFile));

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "multipart/form-data"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<byte[]> localReturnType = new ParameterizedTypeReference<byte[]>() {};
        return apiClient.invokeAPI("/asr", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Detect Language
     * 
     * <p><b>200</b> - Successful Response
     * <p><b>422</b> - Validation Error
     * @param audioFile  (required)
     * @param encode Encode audio first through ffmpeg (optional, default to true)
     * @return Object
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Object detectLanguageDetectLanguagePost(File audioFile, Boolean encode) throws RestClientException {
        return detectLanguageDetectLanguagePostWithHttpInfo(audioFile, encode).getBody();
    }

    /**
     * Detect Language
     * 
     * <p><b>200</b> - Successful Response
     * <p><b>422</b> - Validation Error
     * @param audioFile  (required)
     * @param encode Encode audio first through ffmpeg (optional, default to true)
     * @return ResponseEntity&lt;Object&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Object> detectLanguageDetectLanguagePostWithHttpInfo(File audioFile, Boolean encode) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'audioFile' is set
        if (audioFile == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'audioFile' when calling detectLanguageDetectLanguagePost");
        }
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "encode", encode));


        if (audioFile != null)
            localVarFormParams.add("audio_file", new FileSystemResource(audioFile));

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "multipart/form-data"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Object> localReturnType = new ParameterizedTypeReference<Object>() {};
        return apiClient.invokeAPI("/detect-language", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
}
