package Tvshows;

public class Tvshows_Init {
	public Tvshows_Init(){
		 //System.out.println("Hello Tvshows");
	}
}
/*package Movies;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;

public class Movies_Init {
	public Movies_Init(){
		 getURL();
	}
	public ArrayList<String> getURL(String url){
		ArrayList<String> array = new ArrayList<String>();
		 try {
		 URL oracle = new URL(url);
	        BufferedReader in = new BufferedReader(
	        new InputStreamReader(oracle.openStream()));

	        String inputLine;
	        	
				while ((inputLine = in.readLine()) != null){
					array.add(inputLine);
				    //System.out.println(inputLine);
				}
	        in.close();
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return array;
	}
}
*/