package main;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame{
    
    public Window(){
        super("The Hungry Birds");
        
        Container c = getContentPane();
        c.setLayout(new BorderLayout(5,5));
        
        draw = new Draw();
        c.add(draw, BorderLayout.CENTER);
        
        updater = new Updater();

        setVisible(true);
        setSize(x,y);
        setLocation(400,100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }// constructor

    private class Updater implements Runnable{
        public Updater(){
            thread = new Thread(this, "updater");
            thread.start();
        }// constructor
        
        @Override
        public void run(){
            while(true){
                draw.repaint();
            }//forever-loop
        }// run
        
        private Thread thread;
    }// Updater

    private Updater updater;
    private Draw draw;
    
    public final static int x = 700, y = 500;
    
}// Window
