package uk.ac.uea.framework.sl.utils;

import com.google.gson.*;
/**
 * This class is used to generate the JSON object from string.
 * Created by Beren on 17/01/2016.
 */
public final class JsonGenerator {
/*
 * this class is for generating JSON	
 */
	
	/*
	 * initialization of Gson class 
	 */
	private static Gson gson = new Gson();
	
	/*
	 * This class is for generating JSON from MODEL class
	 * @param Object
	 * @return String 
	 */
	public static String generateJson(Object to) {
		return null == to ? "" : gson.toJson(to);
	}

	/*
	 * Generates the transfer object from the given JSON using Google Gson.
	 * 
	 * @param String
	 * @return transfer object
	 */
	public static Object generateTOfromJson(String json, Class<?> class1) {
		return gson.fromJson(json, class1);
	}
	
}
