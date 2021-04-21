package com.sap.emission.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sap.emission.data.EmissionResult;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmissionService {

	private @Setter RestTemplate restTemplate;

	private TransportationService transportationService;

	@Value("${ORS_TOKEN}")
	private String api_key;

	public EmissionService(@Value("${openrouteservice.url}") String openRouteUrl,
			TransportationService transportationService) {
		log.info("openRouteUrl={}", openRouteUrl);

		RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
				.requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
				.rootUri(openRouteUrl);

		this.restTemplate = restTemplateBuilder.build();
		this.transportationService = transportationService;
	}

	public EmissionResult calculateDistance(String start, String end, String transportMethod) throws Exception {
		if (transportationService.isValidTransportMethod(transportMethod)) {
			double[] source = transportationService.parseCoordinateData(fetchCoordinateByCity(start));
			double[] destination = transportationService.parseCoordinateData(fetchCoordinateByCity(start));
		}

		return new EmissionResult("OK");

	}
	

	private String fetchCoordinateByCity(String text) {
		String res = null;
		String layers = "locality";
		log.info("Getting Coordinate for city {}", text);

		try {
			res = restTemplate.getForObject("/geocode/search?api_key={api_key}&text={text}&layers={layers}",
					String.class, api_key, text, layers);
			validateResult(res);
			System.out.println(res);
			log.info("result::::: {}", res);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("{}", e.getMessage(), e);
		}

		return res;

	}

	private void validateResult(String result) {
		if (result == null || result.equals("")) {
			log.error("Errror::::: {}", result);
			// throw new BadRequestException(ErrorSource.SERVICE, ErrorCode.INVALID_DATA,
			// "Some information related to pod is invalid");
		}
	}

}
