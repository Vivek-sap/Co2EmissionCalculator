package com.sap.emission.services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sap.emission.Exceptions.HttpServerErrorExceptions;
import com.sap.emission.Exceptions.ResourceNotFoundException;
import com.sap.emission.data.EmissionRequest;
import com.sap.emission.data.EmissionResult;
import com.sap.emission.data.MatrixData;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmissionService {
	private @Setter RestTemplate restTemplate;

	private static final String prefix = "Your trip caused ";
	private static final String postfix = "kg of CO2-equivalent.";

    private @Setter TransportationService transportationService;

	@Value("${ORS_TOKEN}")
	private String api_key;

	private static final String metrics = "distance";

	public EmissionService(@Value("${openrouteservice.url}") String openRouteUrl,
			TransportationService transportationService) {
		log.info("openRouteUrl={}", openRouteUrl);

		RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
				.requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
				.rootUri(openRouteUrl);

		this.restTemplate = restTemplateBuilder.build();
		this.transportationService = transportationService;
	}

	public EmissionResult calculateDistance(EmissionRequest request) {
		List<Double> source = null;
		List<Double> destination = null;
		if (transportationService.isValidTransportMethod(request.getMethod())) {
			source = transportationService.parseCoordinateData(fetchCoordinateByCity(request.getStart()));
			destination = transportationService.parseCoordinateData(fetchCoordinateByCity(request.getEnd()));
		} else {
			throw new ResourceNotFoundException("Unable to find transport method "+ request.getMethod());
		}

		MatrixData postRequest = preparePostBody(source, destination);
		String postResponse = calculateDistance(postRequest);
		double distance = transportationService.parseDistanceData(postResponse);
		log.info("distance::::{}", distance);
		double total = totalEmission(distance, request.getMethod());
		
		return new EmissionResult(prefix+total+postfix);

	}

	private String calculateDistance(MatrixData request) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", api_key);
		headers.setContentType(MediaType.APPLICATION_JSON);
		String response = null;
		HttpEntity<MatrixData> postBody = new HttpEntity<>(request, headers);

		try {
			response = restTemplate.postForObject("/v2/matrix/driving-car", postBody, String.class);
			log.info("response::::{}", response);
			validateResult(response, "");
		} catch (Exception e) {
			log.error("{}", e.getMessage(), e);
            throw new HttpServerErrorExceptions("Unable to get distance for the given coordinates, post request failed ");
		}

		return response;
	}

	private String fetchCoordinateByCity(String text) {
		String res = null;
		String layers = "locality";
		log.info("Getting Coordinate for city {}", text);

		try {
			res = restTemplate.getForObject("/geocode/search?api_key={api_key}&text={text}&layers={layers}",
					String.class, api_key, text, layers);
			log.info("result::::: {}", res);
			validateResult(res, text);			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("{}", e.getMessage(), e);
			throw new HttpServerErrorExceptions("Unable to get Coordinates for the given location, get request failed "+ text);
		}

		return res;

	}

	private void validateResult(String result, String text) {
		if (result == null || result.equals("")) {
			throw new ResourceNotFoundException("Unable to find coordinate data for the given location "+ text);
		}
	}

	private MatrixData preparePostBody(List<Double> source, List<Double> destination) {
		MatrixData data = new MatrixData();
		List<List<Double>> location = new ArrayList<List<Double>>();
		location.add(source);
		location.add(destination);

		List<String> metric = new ArrayList<String>();
		metric.add(metrics);

		data.setLocations(location);
		data.setMetrics(metric);

		return data;

	}

	private double totalEmission(double distance, String method) {
		String emissionValue = transportationService.getCo2EmissionValue(method);
		emissionValue = emissionValue.substring(0, emissionValue.length() - 1);
		double value = Double.valueOf(emissionValue).doubleValue();
		return Double.parseDouble(new DecimalFormat("##.##").format(((distance / 1000) * value) / 1000));

	}

}
