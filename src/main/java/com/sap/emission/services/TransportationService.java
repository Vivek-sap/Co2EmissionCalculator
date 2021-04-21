package com.sap.emission.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TransportationService {

	@Value("classpath:data/co2.json")
	private Resource resourceFile;

	private static Map<?, ?> transport = new HashMap<String, String>();
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public String co2Emission(String method) throws JsonParseException, JsonMappingException, IOException {
		if (transport.size() == 0) {
			populateData();
		}

		return (String) transport.get(method);
	}

	public boolean isValidTransportMethod(String method) throws JsonParseException, JsonMappingException, IOException {
		if (transport.size() == 0) {
			populateData();
		}
		return transport.containsKey(method);

	}

	private void populateData() throws JsonParseException, JsonMappingException, IOException {
		transport = objectMapper.readValue(resourceFile.getInputStream(), Map.class);
	}
	
	public double[] parseCoordinateData(String data) throws JsonParseException, JsonMappingException, IOException {		
		double[] result = new double[2];
		JsonNode node =  objectMapper.readTree(data);
		List<JsonNode> l1 = node.findValues("features");
		if (l1.size() > 0) {
			List<JsonNode> n = l1.get(0).findValue("geometry").findValues("coordinates");
			if (n.size() > 0) {
				JsonNode nn = n.get(0);
				IntStream.range(0,nn.size()).forEach(idx->result[idx] = nn.get(idx).doubleValue());
			}
		}
		
		
		
		return result;
	}

}
