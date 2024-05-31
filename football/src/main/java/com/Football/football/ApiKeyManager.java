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
    @Value("${api.key8}")
    private String apiKey8;
    @Value("${api.ke9}")
    private String apiKey9;
    @Value("${api.key10}")
    private String apiKey10;
    @Value("${api.key11}")
    private String apiKey11;
    @Value("${api.key12}")
    private String apiKey12;
    @Value("${api.key13}")
    private String apiKey13;
    @Value("${api.key14}")
    private String apiKey14;
    @Value("${api.key15}")
    private String apiKey15;
    @Value("${api.key16}")
    private String apiKey16;
    @Value("${api.key17}")
    private String apiKey17;
    @Value("${api.key18}")
    private String apiKey18;
    @Value("${api.key19}")
    private String apiKey19;
    @Value("${api.key20}")
    private String apiKey20;
    private int requestCounter = 0;
    private static final int REQUEST_LIMIT = 95;

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
        } else if ((requestCounter >= (REQUEST_LIMIT * 6)) && ((REQUEST_LIMIT * 7) > requestCounter)){
            return apiKey7;
        } else if ((requestCounter >= (REQUEST_LIMIT * 7)) && ((REQUEST_LIMIT * 8) > requestCounter)){
            return apiKey8;
        } else if ((requestCounter >= (REQUEST_LIMIT * 8)) && ((REQUEST_LIMIT * 9) > requestCounter)){
            return apiKey9;
        } else if ((requestCounter >= (REQUEST_LIMIT * 9)) && ((REQUEST_LIMIT * 10) > requestCounter)){
            return apiKey10;
        } else if ((requestCounter >= (REQUEST_LIMIT * 10)) && ((REQUEST_LIMIT * 11) > requestCounter)){
            return apiKey11;
        } else if ((requestCounter >= (REQUEST_LIMIT * 11)) && ((REQUEST_LIMIT * 12) > requestCounter)){
            return apiKey12;
        } else if ((requestCounter >= (REQUEST_LIMIT * 12)) && ((REQUEST_LIMIT * 13) > requestCounter)){
            return apiKey13;
        } else if ((requestCounter >= (REQUEST_LIMIT * 13)) && ((REQUEST_LIMIT * 14) > requestCounter)){
            return apiKey14;
        } else if ((requestCounter >= (REQUEST_LIMIT * 14)) && ((REQUEST_LIMIT * 15) > requestCounter)){
            return apiKey15;
        } else if ((requestCounter >= (REQUEST_LIMIT * 15)) && ((REQUEST_LIMIT * 16) > requestCounter)){
            return apiKey16;
        } else if ((requestCounter >= (REQUEST_LIMIT * 16)) && ((REQUEST_LIMIT * 17) > requestCounter)){
            return apiKey17;
        } else if ((requestCounter >= (REQUEST_LIMIT * 17)) && ((REQUEST_LIMIT * 18) > requestCounter)){
            return apiKey18;
        } else if ((requestCounter >= (REQUEST_LIMIT * 18)) && ((REQUEST_LIMIT * 19) > requestCounter)){
            return apiKey19;
        } else {
            return apiKey20;
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
