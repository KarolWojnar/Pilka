package com.Football.football;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyManager {

    @Value("${api.key}")
    private String apiKey;
    @Value("${api.key2}")
    private String apiKey2;
    @Value("${api.key3}")
    private String apiKey3;
    @Value("${api.key4}")
    private String apiKey4;
    @Value("${api.key5}")
    private String apiKey5;
    @Value("${api.key6}")
    private String apiKey6;
    @Value("${api.key7}")
    private String apiKey7;
    private int requestCounter = 0;
    private static final int REQUEST_LIMIT = 90;

    public synchronized String getApiKey() {
        if (requestCounter < REQUEST_LIMIT) {
            return apiKey;
        } else if ((requestCounter >= REQUEST_LIMIT) && ((REQUEST_LIMIT * 2) > requestCounter)){
            return apiKey2;
        } else if ((requestCounter >= (REQUEST_LIMIT * 2)) && ((REQUEST_LIMIT * 3) > requestCounter)){
            return apiKey3;
        } else if ((requestCounter >= (REQUEST_LIMIT * 3)) && ((REQUEST_LIMIT * 4) > requestCounter)){
            return apiKey4;
        } else if ((requestCounter >= (REQUEST_LIMIT * 4)) && ((REQUEST_LIMIT * 5) > requestCounter)){
            return apiKey5;
        } else if ((requestCounter >= (REQUEST_LIMIT * 5)) && ((REQUEST_LIMIT * 6) > requestCounter)){
            return apiKey6;
        } else {
            return apiKey7;
        }
    }

    public synchronized void incrementRequestCounter() {
        requestCounter++;
        System.out.println(requestCounter);
        if (requestCounter >= 7 * REQUEST_LIMIT) {
            requestCounter = 0;
        }
    }
}
