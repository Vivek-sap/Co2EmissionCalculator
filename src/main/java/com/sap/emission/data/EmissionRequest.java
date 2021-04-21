package com.sap.emission.data;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmissionRequest {

	@NotEmpty(message = "Please provide source")
	String start;
	
	@NotEmpty(message = "Please provide destination")
	String end;
	
	@NotEmpty(message = "Please provide transporation method")
	String method;

}
