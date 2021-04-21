 package com.sap.emission.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.emission.data.EmissionResult;
import com.sap.emission.services.EmissionService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "emission/v1/calculate")
@Slf4j
@Validated
public class EmissionController {
	
	@Autowired
	private EmissionService emissionService;


	@GetMapping(path = "start/{start}/end/{end}/transportMethod/{transportMethod}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmissionResult> getDeploymentByNamespaceAndLabel(@Valid @PathVariable String start,
			@Valid @PathVariable String end, @Valid @PathVariable String transportMethod) {
		log.info("Calculation of CO2 emmission between the cities with transportation method start...{} "  +  start, end, transportMethod);
		try {
			return ResponseEntity.ok(emissionService.calculateDistance(start, end, transportMethod));
		} catch (Exception e) {
            log.error("Error calculating ={}", start, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
	}
}
