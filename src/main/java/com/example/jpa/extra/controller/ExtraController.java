package com.example.jpa.extra.controller;

import com.example.jpa.common.model.ResponseResult;
import com.example.jpa.extra.model.AirInput;
import com.example.jpa.extra.model.OpenApiResult;
import com.example.jpa.extra.model.PharmacySearch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;


@RestController
@RequiredArgsConstructor
public class ExtraController {

    @GetMapping("/api/extra/pharmacy")
    public ResponseEntity<?> pharmacy(@RequestBody PharmacySearch pharmacySearch){

        String apiKey = "puBjkko%2FXHLTNid5%2F8krjbE%2FFR9cgN2q2rLEiUDnP5IJAheW3QqxTeuEFZywSRFSOsm3KEB0Kp%2BwUleeXELcPg%3D%3D";
        String url = String.format("https://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyFullDown?serviceKey=%s&pageNo=1&numOfRows=10",apiKey);

        String apiResult = "";

        try{
            url += String.format("&Q0=%s&Q1=%s",
                    URLEncoder.encode(pharmacySearch.getSearchSido(), "UTF-8"),
                    URLEncoder.encode(pharmacySearch.getSearchGugun(), "UTF-8"));

            URI uri = new URI(url);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String result = restTemplate.getForObject(uri, String.class);

            apiResult = result;

        }catch (Exception e){
            e.printStackTrace();
        }

        OpenApiResult jsonResult = null;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.readValue(apiResult, OpenApiResult.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseResult.success(jsonResult);

    }


    @GetMapping("/api/extra/air")
    public String air(@RequestBody AirInput airInput){

        String apiKey = "puBjkko%2FXHLTNid5%2F8krjbE%2FFR9cgN2q2rLEiUDnP5IJAheW3QqxTeuEFZywSRFSOsm3KEB0Kp%2BwUleeXELcPg%3D%3D";
        String url = "https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?serviceKey=%s&returnType=xml&numOfRows=100&pageNo=1&sidoName=%s";

        String apiResult = "";

        try{
            URI uri = new URI(String.format(url, apiKey, URLEncoder.encode(airInput.getSearchSido(),"UTF-8")));

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String result = restTemplate.getForObject(uri, String.class);

            apiResult = result;

        }catch (Exception e){
            e.printStackTrace();
        }


        return apiResult;

    }
}
