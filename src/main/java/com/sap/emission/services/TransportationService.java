package com.sap.emission.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.emission.Exceptions.ParseException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransportationService {

	@Value("classpath:data/co2.json")
	private Resource resourceFile;

	private static Map<?, ?> transport = new HashMap<String, String>();

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public String getCo2EmissionValue(String method) {
		if (transport.size() == 0) {
			populateData();
		}
		return (String) transport.get(method);
	}

	public boolean isValidTransportMethod(String method) {
		if (transport.size() == 0) {
			populateData();
		}
		return transport.containsKey(method);

	}

	private void populateData() {
		try {
			transport = objectMapper.readValue(resourceFile.getInputStream(), Map.class);
		} catch (IOException e) {
			log.error("{}", "unable to parse json file", e);
			throw new ParseException("Unable to parse data from the co2 json file. Please check if the file exist");
		}
	}

	public List<Double> parseCoordinateData(String data) {
		List<Double> result = new ArrayList<Double>();

		JsonNode node = null;
		try {
			node = objectMapper.readTree(data);
			List<JsonNode> l1 = node.findValues("features");
			if (l1.size() > 0) {
				List<JsonNode> n = l1.get(0).findValue("geometry").findValues("coordinates");
				if (n.size() > 0) {
					JsonNode nn = n.get(0);
					IntStream.range(0, nn.size()).forEach(idx -> result.add(nn.get(idx).doubleValue()));
				}
			}
		} catch (IOException e) {
			log.error("{}", "unable to parse coordinate response", e);
			throw new ParseException("Unable to parse coordinate response.");
		}

		return result;
	}

	public double parseDistanceData(String data) {
		List<Double> result = new ArrayList<Double>();

		JsonNode node = null;
		try {
			node = objectMapper.readTree(data);
			List<JsonNode> l1 = node.findValues("distances");
			if (l1.size() > 0) {
				JsonNode nn = l1.get(0).get(0);
				IntStream.range(0, nn.size()).forEach(idx -> result.add(nn.get(idx).doubleValue()));
			}
		} catch (IOException e) {
			log.error("{}", "unable to parse distance response", e);
			throw new ParseException("Unable to parse distance response.");
		}
		
		return Collections.max(result).doubleValue();
	}

}
