package Tvshows;

import java.awt.PageAttributes.MediaType;
import java.util.ArrayList;

import javax.xml.ws.Response;

import com.sun.security.ntlm.Client;

public class Tvshows_Init {
	String torrentDir = "torrents/";
	String tvDir = "tvDir/";
	String moviesWant = tvDir + "want.txt";
	ArrayList<String> want= new ArrayList<String>();
	String moviesDownloaded =  tvDir + "downloaded.txt";
	ArrayList<String> downloaded= new ArrayList<String>();
	String moviesDownloading =  tvDir + "haveTorrent.txt";
	ArrayList<String> haveTorrent= new ArrayList<String>();
	
	public Tvshows_Init(){
		System.out.println("-- TV Loaded. --");
		updateFiles("D:/TvShows");
		System.out.println("-- TV Completed. --");
	}
	public void updateFiles(String filePath){
		
	}
}
