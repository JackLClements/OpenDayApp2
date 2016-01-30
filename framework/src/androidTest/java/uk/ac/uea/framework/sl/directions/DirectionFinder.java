package uk.ac.uea.framework.sl.directions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.ac.uea.framework.sl.directions.pojos.DirectionsPojo;
import uk.ac.uea.framework.sl.directions.pojos.Legs;
import uk.ac.uea.framework.sl.directions.pojos.Routes;
import uk.ac.uea.framework.sl.directions.pojos.Steps;
import uk.ac.uea.framework.sl.utils.JsonGenerator;

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
	  DirectionsPojo dp = (DirectionsPojo) JsonGenerator.generateTOfromJson(outputString, DirectionsPojo.class);
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