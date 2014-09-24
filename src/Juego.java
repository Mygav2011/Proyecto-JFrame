/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Martha*/

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.LinkedList;
import javax.swing.JFrame;

//Leer y Escribir archivos 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

 
     
public class Juego extends JFrame implements Runnable, KeyListener
{

    /* objetos para manejar el buffer del Applet y este no parpadee */
    private Image    imaImagenApplet;   // Imagen a proyectar en Applet	
    private Graphics graGraficaApplet;  // Objeto grafico de la Imagen
    private Personaje pNena; 
    private Personaje pCam; 
    private Personaje pCorr; 
    private int iScore; 
    private int iVidas; 
    private int iDireccion; 
    private LinkedList lnkCam; 
    private LinkedList lnkCorr; 
    private int iVel; 
    private int iVel2;
    private int iConta;    
    private SoundClip sndCaminante;     //Objeto SoundClip sonido Caminante
    private SoundClip sndCorredor;      //Objeto SoundClip sonido Corredor
    private SoundClip sndSurprise;      //Objeto SoundClip sonido Perdio
    private Imagen iOver;
    
    //Pausa
    private Imagen iPause;    //Imagen al pausar el juego.
    private boolean bPausado;   //Boleano para pausar el juego.
    
    
    /** 
     * init
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se inizializan las variables o se crean los objetos
     * a usarse en el <code>Applet</code> y se definen funcionalidades.
     */
    
    
    public Juego(){
        init();
        start(); 
    }
    
    public void init() {
        // hago el applet de un tamaño 500,500
        setSize(800, 600);
        //Numero de vidas
        int rndV=((int)(Math.random() * 3 + 2));
        iVidas= rndV; 
        iVel=1; 
        iVel2=1;
        iConta=0; 
        
        
        int posX = (int) (Math.random() *(getWidth() / 4));    
        int posY = (int) (Math.random() *(getHeight() / 4)); 
        
        
        URL urlImagenGame=  this.getClass().getResource("gameover.png");
        
        //Se crea el game over
        iOver = new Imagen (0,0,
                Toolkit.getDefaultToolkit().getImage(urlImagenGame));
        iOver.setX(getWidth()/2 - (iOver.getAncho()/2));
        iOver.setY(getHeight()/2 - (iOver.getAlto()/2));
        
        
        URL urlImagenPause=  this.getClass().getResource("pause.jpg");
        
        //Se crea el pausado
        iPause = new Imagen (0,0,
                Toolkit.getDefaultToolkit().getImage(urlImagenPause));
        iPause.setX(getWidth()/2 - (iPause.getAncho()/2));
        iPause.setY(getHeight()/2 - (iPause.getAlto()/2));
        
        URL urlImagenNena = this.getClass().getResource("nena.gif");
        
        //Se crea el objeto Nena 
        pNena= new Personaje(0,0,
                Toolkit.getDefaultToolkit().getImage(urlImagenNena));
        pNena.setX(getWidth()/2 - (pNena.getAncho()/2));
        pNena.setY(getHeight()/2 - (pNena.getAlto()/2));
        
        
        URL urlImagenA1 = this.getClass().getResource("alien1Camina.gif");
        int rndC = (int)(Math.random() * 8 + 2);
        lnkCam= new LinkedList();
        
        for( int iI=0; iI<=rndC; iI++){ 
	pCam = new Personaje(0,(int)(Math.random() * getHeight()),
                Toolkit.getDefaultToolkit().getImage(urlImagenA1)); 
        pCam.setX((int)(Math.random() * getWidth() - getWidth() - pCam.getAncho()));
        while((pCam.getY() + pCam.getAlto() > getHeight())|| (pCam.getY() < 0))
            pCam.setY((int) (Math.random() * getHeight()));
        
        lnkCam.add(pCam);
        }
        
        URL urlImagenA2 = this.getClass().getResource("alien2Corre.gif");
        int rndA2= (int)(Math.random() * 10 + 5);
        lnkCorr= new LinkedList(); 
        
        for( int iI=0; iI<=rndA2; iI++){ //For para crear los cometas  
          pCorr= new Personaje(0,0,
                Toolkit.getDefaultToolkit().getImage(urlImagenA2));
          pCorr.setX((int)(Math.random() * getWidth() - pCorr.getAncho()));
          if(pCorr.getX()- pCorr.getAncho()<0)
              pCorr.setX((int)(Math.random() * getWidth() - pCorr.getAncho()));
          pCorr.setY((int)(Math.random() * getHeight() - getHeight() - pCorr.getAlto()));
          
            lnkCorr.add(pCorr); //Se crea un nuevo cometa
            
        }
        sndCaminante= new SoundClip("119.wav");
        sndCorredor = new SoundClip("109.wav"); 
        sndSurprise = new SoundClip("surprise.wav"); 
        
          bPausado = false; 
        
        addKeyListener(this);
        // introducir instrucciones para iniciar juego
    }
	
    /** 
     * start
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se crea e inicializa el hilo
     * para la animacion este metodo es llamado despues del init o 
     * cuando el usuario visita otra pagina y luego regresa a la pagina
     * en donde esta este <code>Applet</code>
     * 
     */
    public void start () {
        // Declaras un hilo
        Thread th = new Thread (this);
        // Empieza el hilo
        th.start ();
    }

	
    /** 
     * run
     * 
     * Metodo sobrescrito de la clase <code>Thread</code>.<P>
     * En este metodo se ejecuta el hilo, que contendrá las instrucciones
     * de nuestro juego.
     * 
     */
    public void run () {
        // se realiza el ciclo del juego en este caso nunca termina
        while (iVidas>0) {
            /* mientras dure el juego, se actualizan posiciones de jugadores
               se checa si hubo colisiones para desaparecer jugadores o corregir
               movimientos y se vuelve a pintar todo
            */ 
            
            if(!bPausado){
            actualiza();
            checaColision();
            }
            repaint();
            try	{
                // El thread se duerme.
                Thread.sleep (20);
            }
            catch (InterruptedException iexError)	{
                System.out.println("Hubo un error en el juego " + 
                        iexError.toString());
            }
	}
    }
	
    /** 
     * actualiza
     * 
     * Metodo que actualiza la posicion del objeto elefante 
     * 
     */
    public void actualiza(){
        // instrucciones para actualizar personajes
        switch(iDireccion) {
            case 1: { //se mueve hacia arriba
                pNena.arriba();
                break;    
            }
            case 2: { //se mueve hacia abajo
                pNena.abajo();
                break;    
            }
            case 3: { //se mueve hacia izquierda
                pNena.izquierda();
                break;    
            }
            case 4: { //se mueve hacia derecha
                pNena.derecha();
                break;    	
            }
        }
        for(Object objCam : lnkCam){
            pCam= (Personaje)objCam;
            iVel= (int)(Math.random() * 3 + 2);
            pCam.derecha();
            pCam.setVelocidad(iVel); 
            
        }
        for(Object objCorr : lnkCorr){
            pCorr= (Personaje)objCorr;
            pCorr.abajo();
        }
        
        if(iConta==5){
            iConta=0;
            iVidas--; 
             pCorr.setVelocidad(iVel2++);
                    
        }
    }
    
    public void reposicionA1(){
        pCam.setX((int)(Math.random() * getWidth() - getWidth() - pCam.getAncho()));
        while((pCam.getY() + pCam.getAlto() > getHeight())|| (pCam.getY() < 0))
            pCam.setY((int) (Math.random() * getHeight()));    
    }
    
    public void reposicionA2(){
        pCorr.setX((int)(Math.random() * getWidth() - pCorr.getAncho()));
          if(pCorr.getX()- pCorr.getAncho()<0)
              pCorr.setX((int)(Math.random() * getWidth() - pCorr.getAncho()));
          pCorr.setY((int)(Math.random() * getHeight() - getHeight() - pCorr.getAlto()));
        
    }
	
    /**
     * checaColision
     * 
     * Metodo usado para checar la colision del objeto elefante
     * con las orillas del <code>Applet</code>.
     * 
     */
    public void checaColision(){
        // instrucciones para checar colision y reacomodar personajes si 
        // es necesario
        if(pNena.getY() < 0)
              pNena.setY(0);

          if(pNena.getY() + pNena.getAlto() > getHeight())
              pNena.setY( getHeight() - pNena.getAlto()); 

          if( pNena.getX() < 0)
              pNena.setX(0); 
          
          if(pNena.getX() + pNena.getAncho() > getWidth())
              pNena.setX( getWidth() - pNena.getAncho()); 
          
           
        for(Object objCam : lnkCam){
         pCam= (Personaje)objCam;
         if(pCam.getX() + pCam.getAncho() > getWidth())
             reposicionA1();    
         if(pCam.colisiona(pNena)){     //Colisiona
             reposicionA1(); 
             iScore++; 
             sndCaminante.play();
             
         }
        }
           
        for(Object objCorr : lnkCorr){
             pCorr= (Personaje)objCorr;    
           if(pCorr.getY() + pCorr.getAlto() > getHeight()) // y esta pasando el limite
              reposicionA2(); 
           if(pCorr.colisiona(pNena)){  //Colisiona
              reposicionA2(); 
              iConta++;
              sndCorredor.play(); 
                 
         }
        }
            
    }
    
	
    /**
     * paint 
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>,
     * heredado de la clase Container.<P>
     * En este metodo lo que hace es actualizar el contenedor y 
     * define cuando usar ahora el paint
     * @param graGrafico es el <code>objeto grafico</code> usado para dibujar.
     * 
     */
    public void paint (Graphics graGrafico){
        // Inicializan el DoubleBuffer
        if (imaImagenApplet == null){
                imaImagenApplet = createImage (this.getSize().width, 
                        this.getSize().height);
                graGraficaApplet = imaImagenApplet.getGraphics ();
        }
        
        URL urlImagenBack = this.getClass().getResource("espacio.jpg");
        Image imaImagenPlaya = Toolkit.getDefaultToolkit().getImage(urlImagenBack);
        
        graGraficaApplet.drawImage(imaImagenPlaya, 0, 0, 
                getWidth(), getHeight(), this);

        // Actualiza el Foreground.
        graGraficaApplet.setColor (getForeground());
        paintAux(graGraficaApplet);

        // Dibuja la imagen actualizada
        graGrafico.drawImage (imaImagenApplet, 0, 0, this);
    }
    
    /**
     * paintAux
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>,
     * heredado de la clase Container.<P>
     * En este metodo se dibuja la imagen con la posicion actualizada,
     * ademas que cuando la imagen es cargada te despliega una advertencia.
     * @param g es el <code>objeto grafico</code> usado para dibujar.
     * 
     */
    public void paintAux (Graphics g) {
        
        if(iVidas > 0){
        // si la imagen ya se cargo
        //Se dibuja en pantalla: 
            
            g.setFont(new Font("default", Font.BOLD, 20)); //letra
            g.setColor(Color.red); //color
            g.drawString("Vidas: " + iVidas, 40, 60); // vidas               
            g.drawString("Score: " + iScore, 40, 80); // score
                         
             
        if (pNena != null && lnkCam != null && lnkCorr != null) {
            
                //Dibuja la imagen de Nena
                g.drawImage(pNena.getImagen(), pNena.getX(),
                        pNena.getY(), this);
//               
                //Dibuja la imagen de Alien 1
                for(Object objCam : lnkCam){
                     pCam= (Personaje)objCam;
                     g.drawImage(pCam.getImagen(), pCam.getX(),
                        pCam.getY(), this);
                }            
                
                for(Object objCorr : lnkCorr){
                     pCorr= (Personaje)objCorr;
                     g.drawImage(pCorr.getImagen(), pCorr.getX(),
                        pCorr.getY(), this);
                }  
                       
                if (bPausado) {
                g.drawImage(iPause.getImagen(), iPause.getX(),
                        iPause.getY(), this);
            }

        } // sino se ha cargado se dibuja un mensaje 
        else {
                //Da un mensaje mientras se carga el dibujo	
                g.drawString("No se cargo la imagen..", 20, 20);

        }
        }
        else { 
           g.drawImage(iOver.getImagen(), iOver.getX(),    //Se dibuja la imagen game over. 
                        iOver.getY(), this);
           
         sndSurprise.play();  //Sonido de alerta de que el jugador perdio. 
        }
    }

    
    public void keyTyped(KeyEvent e) {
    }

    
    public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == KeyEvent.VK_W) {    
                iDireccion = 1;  // cambio la direcciÃ³n arriba
        }
        // si presiono flecha para abajo
        else if(keyEvent.getKeyCode() == KeyEvent.VK_S) {    
                iDireccion = 2;   // cambio la direccion para abajo
        }
            else if(keyEvent.getKeyCode() == KeyEvent.VK_A) {    
                    iDireccion = 3;   // cambio la direccion para izquierda
            }
                else if(keyEvent.getKeyCode() == KeyEvent.VK_D) {    
                        iDireccion = 4;   // cambio la direccion para derecha
                }
        
         // Se presiona "P" para pausar
        if(keyEvent.getKeyCode()  == KeyEvent.VK_P){
            bPausado = !bPausado;   // la variable se pone opuesta
        }        
      
    }
   
    public void keyReleased(KeyEvent e) {
    }

    private SoundClip getClip() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}