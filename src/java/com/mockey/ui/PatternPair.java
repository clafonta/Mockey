package com.mockey.ui;

/**
 * 
 * @author chadlafontaine
 * 
 */
public class PatternPair {

	private String origination;
	private String destination;
	public PatternPair(){}
	public PatternPair(String origination, String destination) {
		this.origination = origination;
		this.destination = destination;
	}

	public String getOrigination() {
		return origination;
	}

	public void setOrigination(String origination) {
		this.origination = origination;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

}
