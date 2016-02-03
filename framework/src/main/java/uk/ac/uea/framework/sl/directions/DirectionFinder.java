package uk.ac.uea.framework.sl.directions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.ac.uea.framework.sl.directions.mapper.DirectionsMapper;
import uk.ac.uea.framework.sl.directions.mapper.Legs;
import uk.ac.uea.framework.sl.directions.mapper.Routes;
import uk.ac.uea.framework.sl.directions.mapper.Steps;
import uk.ac.uea.framework.sl.utils.JsonGenerator;
/**
 * This class is the main DirectionFinder that is used to get the directions to a destination.
 * Created by Beren on 17/01/2016.
 */
public class DirectionFinder {
  public static void main(String args[]) throws IOException {
	  URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&avoid=highways&mode=bicycling");
	  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	  conn.setRequestMethod("GET");
	  String line, outputString = "";
	  BufferedReader reader = new BufferedReader(
	  new InputStreamReader(conn.getInputStream()));
	  while ((line = reader.readLine()) != null) {
	       outputString += line;
	  }
	  System.out.println(outputString);
	  DirectionsMapper dp = (DirectionsMapper) JsonGenerator.generateTOfromJson(outputString, DirectionsMapper.class);
	  for(Routes route:dp.getRoutes()) {
		  System.out.println("----- Route Begins ------");
		  for(Legs leg:route.getLegs()) {
			  System.out.println("Total Distance "+leg.getDistance().getText());
			  for(Steps step:leg.getSteps()) {
				  System.out.println(step.getDistance().getText());
				  System.out.println(step.getDuration().getText());
			  }
		  }
		  System.out.println("----- Route Ends ------");
	  }
	  System.out.println();
  }
}