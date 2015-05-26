package Movies;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Movies_Init {
	String user_key = "";
	String app_key="13b722fbf7584fb182e714886dc2acc9";
	
	String torrentDir = "torrents/";
	String movieDir = "movieData/";
	String tempDir = "temp/";
	
	String moviesWant = movieDir + "want.txt";
	ArrayList<String> want= new ArrayList<String>();
	
	String moviesDownloaded =  movieDir + "downloaded.txt";
	ArrayList<String> downloaded= new ArrayList<String>();
	
	String moviesDownloading =  movieDir + "newTorrents.txt";
	ArrayList<String> newTorrents= new ArrayList<String>();
	
	public Movies_Init(){
		//System.out.println("-- Movies Loaded. --");
		updateFiles();
		bookmarks();
		updateFiles();
		//System.out.println("-- Movies Completed. --");
	}
	public void checkFiles(){
		if(!new File(torrentDir).exists())
			new File(torrentDir).mkdirs();
		if(!new File(movieDir).exists())
			new File(movieDir).mkdirs();
		if(!new File(tempDir).exists())
			new File(tempDir).mkdirs();
		new File(tempDir).deleteOnExit();	
	}
	public void updateFiles(){
		//ArrayList<String> one = getFilesinDir("D:/Movies", true);
		//ArrayList<String> two = getFilesinDir("J:/!Movies", true);
		//ArrayList<String> three = getFilesinDir("J:/Movies", true);
			ArrayList<String> fileList = new ArrayList<String>();
			fileList = getFilesinDir("C:/Users/alex/Music/File Lists/", false);
			for(int i = 0; i <fileList.size(); i++){
				downloaded = combineArrays(readFile("C:/Users/alex/Music/File Lists/"+fileList.get(i)), downloaded);
			}
		//downloaded = combineArrays(one, two, three);
		for(int i = 0; i < downloaded.size(); i++){
			downloaded.set(i, downloaded.get(i).substring(0, downloaded.get(i).length()-4));
		}
		//savetoFile(sort(downloaded), "movieData/downloaded.txt", false);
	}
	/*public void checkifHave(String str){
		updateFiles();
		for(int i = 0; i < newTorrents.size(); i++){
			if(downloaded.contains(newTorrents.get(i).substring(0, newTorrents.get(i).length()-26))){
				new File(torrentDir+newTorrents.get(i)).delete();
			}
		}
	}
	public void checkifHave(ArrayList<String> array){
		updateFiles();
		for(int i = 0; i < newTorrents.size(); i++){
			if(downloaded.contains(newTorrents.get(i).substring(0, array.get(i).length()-26))){
				new File(torrentDir+array.get(i)).delete();
			}
		}
	}*/
	///////////////////////////////
	//////////YIFY STARTS//////////
	///////////////////////////////
	public void bookmarks(){
		loginToYTS();
		String bookmarks = getBookmarks();
		parseJSON(bookmarks);
		removeBookmarks(bookmarks);
	}
	private void loginToYTS(){//logins to YTS and gets userkey
		System.out.println("\nAttempting to login to YTS...");
		String response = httpPOST("https://yts.to/api/v2/user_get_key.json?username=Completion&password=legolas97&application_key="+app_key);
		if(response.toString().contains("status\":\"ok\",\"status_message\":\"User successfully logged in")){
			user_key =response.toString().substring(response.toString().indexOf("user_key")+11, response.toString().indexOf("@meta")-4);
			System.out.println("Successfully logged in.\nUser Key: " + user_key + "\n");
		}else{
			System.out.println("Failed to log in.\n");
			System.exit(1);
		}
	}
	public String getBookmarks(){//gets bookmarks from YTS using httpPOSt
		System.out.println("Retriving bookmarks...");
		String bookmarks = httpPOST("https://yts.to/api/v2/get_movie_bookmarks.json?user_key="+ user_key);
		if(bookmarks.contains("Query was successful")){
			System.out.println("Successfully retrived bookmarks.");
		}else{
			System.out.println("Retriving bookmarks failed.");
			System.exit(1);
		}
		System.out.println("Currently have " + bookmarks.substring(bookmarks.indexOf("bookmark_count")+16, bookmarks.indexOf(",\"movies\":[")) + " bookmarked.");
		System.out.println("Successfully downloaded bookmarks.");
		return bookmarks;

	}
	public void removeBookmarks(String str){//remove bookmark from YTS using httpPOST
		System.out.println("Attempting to remove bookmarks...");
		try {
			newTorrents = getFilesinDir(torrentDir, true);
			  
		    JSONParser jsonParser = new JSONParser();
			JSONObject allJSONObject =  (JSONObject) jsonParser.parse(str);
			
			String status =  (String) allJSONObject.get("status");
			
			if(!status.equals("ok")){
				System.out.println("----------------------\nERROR YIFY STATUS NOT OK\n----------------------");
				System.exit(1);
			}
			
			JSONArray movies = (JSONArray) ((JSONObject) allJSONObject.get("data")).get("movies");
	
			for(int i = 0; i < movies.size(); i++){
				
				JSONArray torrents =  (JSONArray) ((JSONObject) movies.get(i)).get("torrents");
				for(int j=0; j<torrents.size(); j++){
					
					JSONObject torrent =  (JSONObject) torrents.get(j);
					if(torrent.get("quality").equals("720p")){
						
						String movieName = (String) ((JSONObject) movies.get(i)).get("title_long") + " ["+ torrent.get("quality") + "] YIFY - YTS.torrent";
						//while(movieName.indexOf(':') != -1){
						//	movieName = movieName.substring(0, movieName.indexOf(':')) + "-" + movieName.substring(movieName.indexOf(':')+1, movieName.length());
						//}
						//System.out.println((Long) ((JSONObject) movies.get(i)).get("id"));
						
						if(newTorrents.contains(cleanFileNames(movieName))){
							
							String response = httpPOST("https://yts.to/api/v2/delete_movie_bookmark.json?&application_key=13b722fbf7584fb182e714886dc2acc9&user_key=" + user_key + "&movie_id=" + ((JSONObject) movies.get(i)).get("id").toString());
							
							if(response.toString().contains("Movie bookmarked has been removed")){
								//user_key =response.toString().substring(response.toString().indexOf("user_key")+11, response.toString().indexOf("@meta")-4);
								//System.out.println("Bookmark removed with id " +  (String) ((JSONObject) movies.get(i)).get("id").toString() + " and name " +  ((JSONObject) movies.get(i)).get("title_long").toString());
									System.out.println("(" + (i +1) + "/" + movies.size() + ")\tRemoving\t" + (String) ((JSONObject) movies.get(i)).get("title_long"));

							}else if(response.toString().contains("Movie was never bookmarked")){
								System.out.println("--> Movie was never bookmarkeded. " + movieName);
							}else{
								System.out.println("Removing bookmark failed.");
							}
						}
					}
				}
			}
			System.out.println("Removed all bookmarks.");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	///////////////////////////////
	////////// YIFY ENDS //////////
	///////////////////////////////
	
	///////////////////////////////
	////////INTERNET STARTS////////
	///////////////////////////////
	public String httpPOST(String url){
		try {
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.flush();
			wr.close();
	 
			int responseCode = con.getResponseCode();
		
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return response.toString();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return "";
	}
	public void parseJSON(String str){ //returns magnet links to all torrents in json file
		ArrayList<String> download_Hash = new ArrayList<String>();
		ArrayList<String> download_Name = new ArrayList<String>();
		//System.out.println("Parsing JSON data....");
		boolean first = true;
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject allJSONObject =  (JSONObject) jsonParser.parse(str);
			
			String status =  (String) allJSONObject.get("status");
			
			if(!status.equals("ok")){
				System.out.println("----------------------\nERROR YIFY STATUS NOT OK\n----------------------");
				System.exit(1);
			}
			
			JSONArray movies = (JSONArray) ((JSONObject) allJSONObject.get("data")).get("movies");
			
		
			for(int i = 0; i < movies.size(); i++){
				
				JSONArray torrents =  (JSONArray) ((JSONObject) movies.get(i)).get("torrents");
				for(int j=0; j<torrents.size(); j++){
				
					JSONObject torrent =  (JSONObject) torrents.get(j);
					if(torrent.get("quality").equals("720p")){
						//System.out.println((String) ((JSONObject) movies.get(i)).get("title_long"));
						//System.out.println("\t" + torrent.get("hash"));
						String movieName = cleanFileNames((String) ((JSONObject) movies.get(i)).get("title_long") + " ["+ torrent.get("quality") + "] YIFY - YTS");
						download_Hash.add( (String) torrent.get("hash"));
						download_Name.add(movieName);
					}
				}
			}
			//System.out.println("Completed parsing.\nDownloading Files...");
			for(int i = 0; i < download_Hash.size(); i++){
				if(!(downloaded.contains(download_Name.get(i)) || (newTorrents.contains(download_Name.get(i))))){
					System.out.println("(" + (i +1) + "/" + download_Name.size() + ")\tDownloaded\t" + download_Name.get(i).substring(0, download_Name.get(i).length()-18));
				}else{
					System.out.println("(" + (i+1)+ "/" + download_Name.size() + ")\tDownloading\t" + download_Name.get(i).substring(0, download_Name.get(i).length()-18));
					downloadYTSFile(download_Name.get(i), download_Hash.get(i), torrentDir);
				}

			}
			//System.out.println("Successfully downloaded files.");
		} catch (ParseException ex) {
			ex.printStackTrace();
			System.exit(1);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
	public void downloadYTSFile(String movieName, String hash, String filePath){//download file from URL
		try {
			URL website = new URL("https://yts.to/torrent/download/" + hash +".torrent");
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			
			FileOutputStream fos = new FileOutputStream(filePath + cleanFileNames(movieName) + ".torrent");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	public ArrayList<String> getURL(String url){//reads data from URL
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
			System.exit(1);
		}
		return array;
	}
	///////////////////////////////
	/////////INTERNET ENDS/////////
	///////////////////////////////
	
	///////////////////////////////
	///////FILE UTILS STARTS///////
	///////////////////////////////
	public void savetoFile(ArrayList<String> array, String filePath, Boolean append){//saves to file. true overwrites with current arraylist. false appends to current file
		File yourFile = new File(filePath);
		if(!yourFile.exists()) {
		    try {
				yourFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		} 
		if(append){
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)))) {
			    for(int i = 0; i < array.size(); i++){
			    	out.println(array.get(i));
			    }
			   out.close();
			}catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}else{
			try {
				PrintWriter pr = new PrintWriter(filePath);
			
				for(int i = 0; i < array.size(); i++){
					pr.println(array.get(i));
				}
				pr.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		
	}
	public ArrayList<String> readFile(String filePath){//reads File
		ArrayList<String> array = new ArrayList<String>();
		try {
			File yourFile = new File(filePath);
			if(!yourFile.exists()) {
			    yourFile.createNewFile();
			} 
			String sCurrentLine;
 
			BufferedReader br = new BufferedReader(new FileReader(filePath));
 
			while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(sCurrentLine);
				array.add(sCurrentLine);
			}
			if (br != null)br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return array;
	}
	public void findDups(String filePath){//TODO: Need to fix
		ArrayList<String> array = readFile(filePath);
		for(int j = 0; j < array.size(); j++){
			while(array.indexOf(array.get(j)) != array.lastIndexOf(array.get(j)))
				array.remove(array.lastIndexOf(array.get(j)));
		}
		savetoFile(array, filePath, false);
	}
	public String cleanFileNames(String fileName){
		ArrayList<String> bad = new ArrayList<String>() {{
		    add("?");
		    add("*");
		    add("<");
		    add(">");
		    add("|");
		}};
		for(int i = 0; i < bad.size(); i++){
			while(fileName.contains(bad.get(i))){
				fileName = fileName.substring(0, fileName.indexOf(bad.get(i))) + fileName.substring(fileName.indexOf(bad.get(i))+1,fileName.length());
			}
		}
			while(fileName.contains(":")){
				fileName = fileName.substring(0, fileName.indexOf(":")) + "-"+fileName.substring(fileName.indexOf(":")+1,fileName.length());
			}
		return fileName;
	}
	public ArrayList<String> getFilesinDir(String filePath, Boolean subDirs){//if true then gets subDirectors also recursively
		ArrayList<String> array = new ArrayList<String>();
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
	    for (int k = 0; k < listOfFiles.length; k++) {
			if (listOfFiles[k].isFile()) {
				if(!(listOfFiles[k].getName().equals("desktop.ini") || listOfFiles[k].getName().equals("thumbs.db")))
					array.add(listOfFiles[k].getName());
			} else if (listOfFiles[k].isDirectory() && subDirs) {
			   getFilesinDir(listOfFiles[k].getPath(), subDirs, array);
			}
	    }
		return array;
	}
	public ArrayList<String> getFilesinDir(String filePath, Boolean subDirs, ArrayList<String> array){//if true then gets subDirectors also recursively
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
	    for (int k = 0; k < listOfFiles.length; k++) {
			if (listOfFiles[k].isFile()) {
				if(!(listOfFiles[k].getName().equals("desktop.ini") || listOfFiles[k].getName().equals("thumbs.db")))
					array.add(listOfFiles[k].getName());
			} else if (listOfFiles[k].isDirectory() && subDirs) {
			   array = getFilesinDir(listOfFiles[k].getPath(), subDirs, array);
			}
	    }
		return array;
	}
	///////////////////////////////
	////////FILE UTILS ENDS////////
	///////////////////////////////
	
	///////////////////////////////
	///////ARRAY UTIL STARTS///////
	///////////////////////////////
	public ArrayList<String> sort(ArrayList<String> array){
		for(int i = array.size()-1; i >= 0; i--) {
			for(int j = 0; j < i; j++) {
				if(array.get(j).compareTo(array.get(j + 1)) > 0) {//array.get(j) > array.get(j + 1)
					String temp = array.get(j);
					array.set(j, array.get(j + 1));
					array.set(j + 1, temp);
				}
			}
		}
		return array;
	}
	public ArrayList<String> combineArrays(ArrayList<String> ...arrays){
		ArrayList<String> newArr = new ArrayList<String>();
		for(int i = 0; i < arrays.length; i++){
			for(int j = 0; j < arrays[i].size(); j++){
				newArr.add(arrays[i].get(j));
			}
		}
		return newArr;
	}
	public ArrayList<String> findDups(ArrayList<String> array, boolean print){
		for(int j = 0; j < array.size(); j++){
			while(array.indexOf(array.get(j)) != array.lastIndexOf(array.get(j))){
				array.remove(array.lastIndexOf(array.get(j)));
				if(print){
					System.out.println(array.get(array.lastIndexOf(array.get(j))));
				}
			}
		}
		return array;
	}
	///////////////////////////////
	////////ARRAY UTIL ENDS////////
	///////////////////////////////

	public void not_needed(){
	/*
	public String searchYify(String name){
		ArrayList<String> jsonforMovie = new ArrayList<String>();
		String movieName = name.substring(0, name.length()-7);
		while(movieName.indexOf(' ') != -1){
			movieName = movieName.substring(0, movieName.indexOf(' ')) + "+" + movieName.substring(movieName.indexOf(' ')+1, movieName.length());
		}
		return httpPOST("https://yts.to/api/v2/list_movies.json?quality=720p&query_term="+movieName).toString();
		
	}
	public void moviesWanted(){
		System.out.println("\nSearching YTS for all wanted movies...");
		want = readFile(moviesWant);
		for(int i = 0; i < want.size(); i++){
			if(downloaded.contains(want.get(i))){
				System.out.println("(" + (i +1) + "/" + want.size() + ")\tDownloaded\t" + want.get(i));
				//want.remove(i);
			}else{
				System.out.println("(" + (i+1)+ "/" + want.size() + ")\tDownloading\t" + want.get(i));
				//System.out.println("Dont have " + want.get(i));
				//System.out.println(searchYify(want.get(i)));
				parseJSON(searchYify(want.get(i)));
				//want.remove(i);
			}
		}
		want = new ArrayList<String>();
		savetoFile(want, moviesWant, false);
		System.out.println("Processing Completed for wanted movies\n");*/
	}
	
}