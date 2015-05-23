import Movies.Movies_Init;
import Tvshows.Tvshows_Init;


public class Main {
	public Main(){
		System.out.println("-- Program Loaded. --");
		Movies_Init mov = new Movies_Init();
		Tvshows_Init tv = new Tvshows_Init();
		System.out.println("-- Program Terminated. --");
	}
    public static void main(String[] args) {
    	Main main = new Main();
        //System.out.println("Hello World");
    }
}
