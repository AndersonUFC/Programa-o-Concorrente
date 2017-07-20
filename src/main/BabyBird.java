package main;

import java.util.concurrent.Semaphore;
import java.util.Random;

public class BabyBird {
    
    // inicializa as variáveis, 
    public BabyBird(int id){
        this.id = id;
        status = "sleeping";
        
        Random r = new Random();
        
        hungryDelay = (Math.abs(r.nextInt()))%200 + 100;
        
        hungry = new Hungry();
        lp = new LiveProcess();
    }// constructor
    
    // retorna o Id do pássaro
    public int getId(){
        return id;
    }// getId
    
    // retorna a quantidade de energia do passaro
    public int getEnergy(){
        return energy;
    }// getEnergy
    
    // retorna o status do passaro
    public String getStatus(){
        return status;
    }// getStatus
    
    // Processo relativo às açoes do pássaro filhote
    private class LiveProcess implements Runnable{

        public LiveProcess(){
            thread = new Thread(this, "Baby bird");
            thread.start();
        }// constructor
        
        @Override
        public void run(){
            while(true){
                // entra na seção crítica
                try { mutex.acquire(); } catch(InterruptedException e){}                    
                
                    // verifica se a energia está critica
                    if(energy <= 1){
                        waiting++;
                        status = "waiting";
                        // obter comida e comer
                        Main.parent.getFood(id, hungryDelay);
                        
                        // semaforo usado pra travar o processo de "sentir fome" (Hungry)
                        // enquanto que o pássaro come, assim que ele termina de comer ele
                        // libera o semaforo pra sentir fome de novo
                        eating.release();
                        sleeping++;
                    } else {
                        status = "sleeping";
                    }
                    
                // sair da seção crítica em um loop
                mutex.release();
            }// while
        }// run
        
        private Thread thread;
    }// LiveProcess
    
    // processo responsavel por fazer o passaro sentir fome
    private class Hungry implements Runnable{
        
        public Hungry(){
            thread = new Thread(this, "hungry");
            thread.start();
        }// constructor
        
        @Override
        public void run(){
            while(true){
                // quando a energia fica em estado critico, hungry para de executar
                // até que o passaro tenha terminado de comer
                if(energy <= 1)
                        try { eating.acquire(); } catch(InterruptedException e){}
                
                // adquire a exclusão mutua
                try { mutex.acquire(); } catch(InterruptedException e){}    
                    // delay para destacar separação entre comandos concorrentes
                    try { thread.sleep(hungryDelay); } catch(InterruptedException e){}
                        //  decrementa a energia
                        if(energy > 0)
                            energy--;
                mutex.release();
                
            }// forever-loop
        }// run
        
        private Thread thread;
    }// Hungry

    // incrementa energia
    public void eat(){
        energy++;
    }//eat
    
    // retorna o status do passaro
    public void setStatus(String s){
        status = s;
    }
    
    // energy: indica a quantidade de energia do passaro
    private int energy = 100;
    
    // identificação do passaro
    private int id;
    
    // exclusão mutua entre processo Hungry e LiveProcess
    private Semaphore mutex = new Semaphore(1);
    // garante que Hungry não decremente a energia enquanto o passaro come
    private Semaphore eating = new Semaphore(0);
    
    // delay para comer
    private int hungryDelay;
    
    //processes
    private Hungry hungry;
    private LiveProcess lp;
    
    // status
    private String status;
    
    // counters
    public int sleeping = 1,
                waiting = 0,
                access = 0,
                eat = 0,
                wakeParent = 0;
}// BabyBird
