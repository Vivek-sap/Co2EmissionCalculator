package com.sap.emission.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sap.emission.Exceptions.HttpServerErrorExceptions;
import com.sap.emission.Exceptions.ResourceNotFoundException;
import com.sap.emission.data.EmissionRequest;
import com.sap.emission.data.EmissionResult;

@RunWith(SpringRunner.class)
public class EmissionServiceTest {

    private static final String NOT_NULL = "I am so full of content!";


    @TestConfiguration
    static class AdapterserviceTestContextConfiguration {

        @MockBean
        private static RestTemplate restTemplate;

        @MockBean
        private static TransportationService transportationService;

        @Bean
        public EmissionService customerService() {
            return new EmissionService(NOT_NULL, transportationService);
        }
    }

    @Autowired
    private EmissionService emissionService;

    private static final String start = "munich";
    private static final String end = "stuttgart";
    private static final String method = "small-car";

    private static final String prefix = "Your trip caused ";
    private static final String postfix = "kg of CO2-equivalent.";

    @Before
    public void init() {
        emissionService.setRestTemplate(AdapterserviceTestContextConfiguration.restTemplate);
        emissionService.setTransportationService(AdapterserviceTestContextConfiguration.transportationService);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCalculateDistanceWithSuccess() {

        double total = 30.0;
        Mockito.when(AdapterserviceTestContextConfiguration.transportationService.isValidTransportMethod(any(String.class))).thenReturn(true);
        Mockito.when(AdapterserviceTestContextConfiguration.transportationService.parseCoordinateData(any(String.class))).thenReturn(new ArrayList<Double>());
        Mockito.when(AdapterserviceTestContextConfiguration.transportationService.parseDistanceData(any(String.class))).thenReturn(200000.2);
        Mockito.when(AdapterserviceTestContextConfiguration.transportationService.getCo2EmissionValue(any(String.class))).thenReturn("150g");
        Mockito.when(AdapterserviceTestContextConfiguration.restTemplate.getForObject(any(String.class), any(Class.class), any(String.class), any(String.class),
            any(String.class))).thenReturn("xxxx");
        Mockito.when(AdapterserviceTestContextConfiguration.restTemplate.postForObject(any(String.class), any(Object.class), any(Class.class)))
            .thenReturn("xxxx");


        EmissionResult output = emissionService.calculateDistance(new EmissionRequest(start, end, method));
        assertThat(output.getResult()).isEqualTo(prefix + total + postfix);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCalculateDistanceWithHttpServerExceptionByPost() throws JsonProcessingException {
        Mockito.when(AdapterserviceTestContextConfiguration.transportationService.isValidTransportMethod(any(String.class))).thenReturn(true);
        Mockito.when(AdapterserviceTestContextConfiguration.transportationService.parseCoordinateData(any(String.class))).thenReturn(new ArrayList<Double>());
        Mockito.when(AdapterserviceTestContextConfiguration.transportationService.parseDistanceData(any(String.class))).thenReturn(200000.2);
        Mockito.when(AdapterserviceTestContextConfiguration.transportationService.getCo2EmissionValue(any(String.class))).thenReturn("150g");
        Mockito.when(AdapterserviceTestContextConfiguration.restTemplate.getForObject(any(String.class), any(Class.class), any(String.class), any(String.class),
            any(String.class))).thenReturn("xxxx");

        assertThrows(HttpServerErrorExceptions.class, () -> emissionService.calculateDistance(new EmissionRequest(start, end, method)));

    }

    @Test
    public void testCalculateDistanceWithResourceNotFound() throws JsonProcessingException {
        Mockito.when(AdapterserviceTestContextConfiguration.transportationService.isValidTransportMethod(any(String.class))).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> emissionService.calculateDistance(new EmissionRequest(start, end, method)));

    }


}
