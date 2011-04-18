package com.mockey.model;

public class ApiOption {

	public ApiOption(String optionKey, ApiOptionType type, String... arg){
		for(String a: arg){
			System.out.print(a);
		}
	}
	
//	public static void main(String[] args){
//		ApiOption a = new ApiOption("name", ApiOptionType.STRING, "A", "B", "C");
//		
//	}
}
