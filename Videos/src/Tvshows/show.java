package Tvshows;

import java.util.ArrayList;

public class show {
	String name= "";
	int season =0;
	String[] str = new String[5];
	public show(String str, int num, int...eps){
		name=str;
		season=num;
	}
	public void setMax(int num){
		season=num;
	}
	public int getMax(){
		return season;
	}
	public void season(ArrayList<String> ...season){
		System.out.println(season.length);
	}
}
