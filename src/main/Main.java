package main;

public class Main {
    
    public static void main(String[] args){
        init();
        window = new Window();
    }// main
    
    public static void init(){
        for(int i = 0 ; i < N ; i++)
            children[i] = new BabyBird(i);
    }// init
    
    public static Window window;
    public static int N = 32;
    public static int E = 1;
    public static BabyBird children[] = new BabyBird[N];
    
    // shared variables
    public static ParentBird parent = new ParentBird();
}// Main
