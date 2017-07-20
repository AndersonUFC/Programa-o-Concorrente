package main;

import java.util.Random;
import java.util.concurrent.Semaphore;

// fornece a comida para os filhotes
public class ParentBird implements Runnable{
    
    public ParentBird(){
        Random r = new Random();
        
        // cria um delay para repor comida
        foodReleaseDelay = (int)(r.nextFloat()*1000)%100 + 100;
        
        // inicializa os semaforos e o array enter
        for(int i = 0 ; i < Main.N ; i++){
            enter[i] = 0;
            token[i] = new Semaphore(0);
            exit[i] = new Semaphore(0);
        }// for
        
        tk = new TokenPassing();
        
        thread = new Thread(this, "parent");
        thread.start();
    }// public
    
    public void getFood(int id, int delay){
        
        // protocolo de entrada para a seção crítica e solicitar acesso ao
        // pote de comida
        try{entry.acquire();}catch(InterruptedException e){}
            enter[id] = 1;
        entry.release();
        
        Main.children[id].access++;
        
        // tenta adquirir o token de permissão
        try{token[id].acquire();}catch(InterruptedException e){}

        // food mutex para pegar comida
        // food mutex age como contador de recursos
        try { foodMutex.acquire(); } catch(InterruptedException e){}
        Main.children[id].eat++;
        
        // wake bird
        // condição de acordar a mãe quando o estoque acabar
        if(foodMutex.availablePermits() == 0){
            Main.children[id].wakeParent++;
            wake.release();
        }
        
        // após adquirir o token, o passaro filhote "id" vai comer
        Main.children[id].setStatus("eating");
        
        // incrementa a energia
        while(Main.children[id].getEnergy() < 100){
            try { thread.sleep((long)(delay/(1*Main.N))); } catch(InterruptedException e){}                
            Main.children[id].eat();
        }//while
        
        // ao terminar, o passaro filhote avisa ao token, que espera o semaforo liberar
        // para continuar passando a permissão
        exit[id].release();
        
    }// getFood

    @Override
    public void run() {
        while(true){
            status = "sleeping";
            
            // espera algum filhote dar release no semaforo pra repor comida
            try{ wake.acquire(); }catch(InterruptedException e){}
            repor++;
            status = "awaken";
            
            // repor comida
            for(int i = 0 ; i < MAX_FOOD ; i++){
                try{ thread.sleep(foodReleaseDelay); }catch(InterruptedException e){}
                if(foodMutex.availablePermits() <= MAX_FOOD)
                    foodMutex.release();
            }// for
        }// forever-loop
    }// run
    
    public int getTokenPosition(){
        return tk.i;
    }
    
    public String getStatus(){
        return status;
    }
    
    /**
     *  Esta classe tem a mesma função de um token ring.
     *  Ela arranja os processos em uma topologia em anel.
     *  Um token é passado de forma unidirecional entre os processos
     *  e o primeiro processo no caminho do token que tem interesse
     * em entrar na seção critica, que no caso aqui é para um passaro
     * comer uma porção de comida, captura esse token e por meio dele
     * adquire permissão para entrar na seção crítica
     * 
     */
    private class TokenPassing implements Runnable{
        public TokenPassing(){
            thread = new Thread(this, "token passing");
            thread.start();
        }// constructor

        public void run(){            
            while(true){
                // entry é o semaforo de exclusão mútua para acessar o banco de comida
                try{entry.acquire();}catch(InterruptedException e){}
                    // se o passaro de identificação i quer entrar na seção, dê a permissão
                    // caso contrário, passe para o proximo id
                    if(enter[i] == 1){
                        enter[i] = 0;
                        token[i].release();
                        
                        entry.release();
                        try{exit[i].acquire();}catch(InterruptedException e){}
                    } else{
                        entry.release();
                    }
                i = (i+1)%Main.N;
            }// forever loop
        }// run
        public int i = 0;
        private Thread thread;
    }// TokenPassing
    
    private Thread thread;

    private static int foodReleaseDelay;
    
    // quantidade máxima de comida
    public static int MAX_FOOD = Main.N-1;    
    
    // semáforo que indica o momento que o passaro é acordado para repor comida
    public static Semaphore wake = new Semaphore(0);
    
    // foodMutex: contador de recurso, serve pra indicar
    public static Semaphore foodMutex = new Semaphore(MAX_FOOD);
    
    // enter: para cada psasaro, enter indica solicitação para o token de entrar 
    //      na seção critica, que no caso é obter comida
    public static int enter[] = new int[Main.N];
    // token: barra a entrada de quem solicita até que o token chegue na vez do passaro
    //      qque solicitou e libera o semaforo correspondente
    public static Semaphore token[] = new Semaphore[Main.N];
    // exit: cada passaro que termina de comer libera o semaforo para que o token continue
    //      girando até encontrar outro passaro solicitando entrada na seção critica
    public static Semaphore exit[] = new Semaphore[Main.N];
    // entry: sinal de exclusão mútua entre os semáforos dessa classe
    public static Semaphore entry = new Semaphore(1);
    
    public static TokenPassing tk;
    private String status;
    
    // counter
    public int repor = 0;
}// ParentBird

