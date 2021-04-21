package com.sap.emission.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatrixData {
	
	private Location[] location;
	
	private String[] metrics;
	
	private int sourceIndex;
	
	private int destinationIndex;

}
