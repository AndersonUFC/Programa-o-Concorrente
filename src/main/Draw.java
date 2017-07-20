package main;

import java.awt.*;
import javax.swing.*;

public class Draw extends JPanel{
    
    public Draw(){
        
    }// constructor
    
    public void paintComponent(Graphics g2){
        super.paintComponent(g2);
        
        Graphics2D g = (Graphics2D)g2;
        RenderingHints rh = new RenderingHints(
                     RenderingHints.KEY_ANTIALIASING,
                     RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHints(rh);
        
        // fundo
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0,Window.x,Window.y);
        g.setColor(new Color(255,226,165));
        g.fillRect(2,2,Window.x-4,Window.y-4);
        
        // lista birds
            g.setColor(Color.BLACK);
            g.fillRect(10,20,370,Window.y-40);

            g.setColor(new Color(255,255,255));
            g.fillRect(15,25,360,Window.y-50);
        
        g.setColor(Color.BLACK);
        
        int xd = (Window.x)/size;
        int yd = (Window.y - 60)/size;
        
        Color c = Color.red;
        
        // parent bird
        g.drawString("parent bird status: " + Main.parent.getStatus(), 400, 15);
        
        g.drawString("bird id", 20, 15);
        g.drawString("energy", 100, 15);
        g.drawString("status", 220, 15);
        
        int sleepAc = 0, waitingAc = 0, accessAc = 0, eatingAc = 0, wakeParentAc = 0;
        
        g.drawString("sl", 280, 15);
        g.drawString("wa", 300, 15);
        g.drawString("ac", 320, 15);
        g.drawString("ea", 340, 15);
        g.drawString("wp", 360, 15);
        for(int i = 0 ; i < size ; i++){
            int en = Main.children[i].getEnergy();
            if(en > 100) en = 100;
            
            g.setColor(Color.BLACK);
            g.drawString("bird [" + Main.children[i].getId() + "]", 20, 40 + yd*i);
            g.drawString(Main.children[i].getStatus(), 220, 40 + yd*i);
            
            g.setColor(new Color((((100 - en)*255)/100),(en*255)/100,0));
            g.fillRect(100, 30 + yd*i, en, 10);
            
            g.setColor(Color.BLACK);
            g.drawRect(100, 30 + yd*i, 100, 10);
            
            g.drawString("" + Main.children[i].sleeping, 280, 40 + yd*i);
            g.drawString("" + Main.children[i].waiting, 300, 40 + yd*i);
            g.drawString("" + Main.children[i].access, 320, 40 + yd*i);
            g.drawString("" + Main.children[i].eat, 340, 40 + yd*i);
            g.drawString("" + Main.children[i].wakeParent, 360, 40 + yd*i);            
            
            sleepAc += Main.children[i].sleeping;
            waitingAc += Main.children[i].waiting;
            accessAc += Main.children[i].access;
            wakeParentAc += Main.children[i].wakeParent;
            eatingAc += Main.children[i].eat;
        }
        g.drawString("________________________________", 400, 15);
        g.drawString("sleep counter: " + sleepAc, 400, 30);
        g.drawString("waiting counter: " + waitingAc, 400, 40);
        g.drawString("access counter: " + accessAc, 400, 50);
        g.drawString("eating counter: " + eatingAc, 400, 60);
        g.drawString("________________________________", 400, 65);
        g.drawString("wakeParent counter: " + wakeParentAc, 400, 80);
        g.drawString("replace counter: " + Main.parent.repor, 400, 90);
        
        int food = Main.parent.foodMutex.availablePermits();
        int maxFood = Main.parent.MAX_FOOD;
        if(food > maxFood)
            food = maxFood;
        float aspect_ratio = ((float)food)/((float)maxFood);
        g.setColor(Color.BLACK);
        g.drawString("Food:      " + food + "/" + maxFood,450, 445);
        
        g.setColor(new Color((int)(255 - aspect_ratio*255), (int)(aspect_ratio*255),0));
        g.fillRect(500, 455, (int)(aspect_ratio*100), 10);
        
        g.setColor(Color.BLACK);
        g.drawRect(500, 455, 100, 10);
        
        paintGraphToken(g);
    }// paintComponent
    
    private int gx = 390, gy = 100;
    
    public void paintGraphToken(Graphics2D g){

        g.setColor(Color.BLACK);
        g.fillRect(gx - 5,gy - 5,310,310);
        
        g.setColor(Color.WHITE);
        g.fillRect(gx,gy,300,300);
        
        double size = Main.N;
        double degree = 2*Math.PI/size;
        
        for(int i = 0 ; i < Main.N ; i++){
            g.setColor(Color.BLACK);

            double x1 = 120*Math.sin(degree*i) + gx + 150;
            double y1 = 120*Math.cos(degree*i) + gy + 150;
            double x2 = 120*Math.sin(degree*(i+1)) + gx + 150;
            double y2 = 120*Math.cos(degree*(i+1)) + gy + 150;
            
            g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
        }// for
        
        for(int i = 0 ; i < Main.N ; i++){
            g.setColor(Color.BLACK);
            double x = 120*Math.sin(degree*i) + gx + 150;
            double y = 120*Math.cos(degree*i) + gy + 150;
            g.fillOval((int)(x-12), (int)(y-12), 24, 24);
            
            if(Main.children[i].getStatus().equals("eating"))
                g.setColor(Color.GREEN);
            else if(Main.children[i].getStatus().equals("waiting"))
                g.setColor(Color.YELLOW);
            else
                g.setColor(Color.LIGHT_GRAY);
            
            g.fillOval((int)(x-10), (int)(y-10), 20, 20);
            
            if(i == Main.parent.getTokenPosition()){
                double tx = 140*Math.sin(degree*i) + gx + 150;
                double ty = 140*Math.cos(degree*i) + gy + 150;  
                
                g.setColor(Color.BLACK);
                g.fillOval((int)(tx-6), (int)(ty-6), 12, 12);
                g.setColor(Color.ORANGE);
                g.fillOval((int)(tx-5), (int)(ty-5), 10, 10); 
            }// if
            double sx = 145*Math.sin(degree*i) + gx + 150;
            double sy = 145*Math.cos(degree*i) + gy + 150;
            
            g.setColor(Color.BLACK);
            g.drawString(""+i, (int)sx-5, (int)sy+5);
        }// for
        
    }// painGraphToken
    
    private int size = Main.N;
}// Draw
