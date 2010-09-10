package com.mockey.model;

public class ApiOptionType {
	private String type = null;
	private ApiOptionType(String _type){
		this.type = _type;
	}
	public boolean equals(ApiOptionType arg){
		if(arg!=null && this.type.equals(arg.type)){
			return true;
		}
		else {
			return false;
		}
	}
	public final static ApiOptionType NUMBER = new ApiOptionType("number");
	public final static ApiOptionType STRING = new ApiOptionType("string");

}
