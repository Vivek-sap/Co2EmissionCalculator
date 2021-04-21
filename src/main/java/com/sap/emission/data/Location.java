package com.sap.emission.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {	

	private Coordinate[] source;
	
	private Coordinate[] destination;

}
