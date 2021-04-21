package com.sap.emission.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatrixData {
	
	 private List<List<Double>> locations;
	
	 private List<String> metrics;
	
	

}
