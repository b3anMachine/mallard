package com.beans;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.awt.image.*;
import java.io.*;
import sun.audio.*;
//import static java.lang.Math.*;

public class Game implements KeyListener, MouseMotionListener, MouseListener{
	
	//Our window and panel
	private MyPanel panel = new MyPanel();
	private JFrame frame = new JFrame("Mallard!");
	private int frameWidth = 950;
	private static int frameHeight = 600;
	
	//mouse stuff
	private Point mouse = new Point();
	private Robot mouseRobot;
	private boolean mouseIn;
	private int mouseX = MouseInfo.getPointerInfo().getLocation().x;

	//more mouse stuff
	private Image image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);  
	private Cursor transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisiblecursor");
	
	//music
	private InputStream musicLoc;
	private AudioStream audioStream;
	private boolean musicPlaying;
	
	//game states
	private boolean running;
	private boolean gameStarted;
	private boolean paused;
	private boolean gameOver;
	private boolean resetting;
	
	//game asset arrays
	private java.util.List<LuigiBlock> luigiList = new ArrayList<LuigiBlock>();
	private java.util.List<Fireball> fireList = new ArrayList<Fireball>();
	private java.util.List<DuckTarget> duckList = new ArrayList<DuckTarget>();

	//game asset helper arrays
	private java.util.LinkedList<DuckTarget> duckQueue = new LinkedList<DuckTarget>();
	private static java.util.LinkedList<Fireball> fireQueue = new LinkedList<Fireball>();	
	
	//misc. game variables
	float luigiGap = .618f; //the proportion of wich to raise luigi block.
	private static float initialFirePush = -.40f;
	private long pufTime;
	private long killsSinceLastBonus;
	private long duckTiming = 1000;
	private long score;
	private boolean redDuckTime;
	private long duckCountdown = duckTiming;
	private float[] flashLoc = new float[4]; 
	private int redDuckCount;
	
	private String[] pregameMessage = {"[mouse] = control", "[click] = start", "[esc] = pause", "[f] = ?"};
	private String[] postgameMessage = {"[enter] = retry"};
	
	//Main function
	public static void main(String[] args){
		new Game().bigbang();
	}
	
	//Main engine to run
	public void bigbang() {
		setup();
		gameLoop();
		System.out.println("Thanks for playing.");
	}
	
	public void setResetting(){
		resetting = true;	
	}
	
	public void reset(){
		duckList.clear();
		score = 0;
		
		fireList.clear();
		fireList.add(new Fireball());
		
		
		luigiList.clear();
		for(int i = 0; i < 2; ++i){
			LuigiBlock l = new LuigiBlock();	
			luigiList.add(l);
		
			luigiList.get(i).setY(frameHeight - luigiGap * l.getHeight() - l.getHeight());		
			luigiList.get(i).setX((frameWidth / 2) -  (l.getWidth() / 2));
		}
		
		duckQueue.clear();
		fireQueue.clear();
		pufTime = 0;
		killsSinceLastBonus = 0;
		duckCountdown = duckTiming;
		redDuckTime = false;
		redDuckCount = 0;
		
		paused = false;
		gameOver = false;
		gameStarted = false;
		resetting = false;
		
	}
	
	public void musicPlayback(){
		if(musicPlaying){
			
			AudioPlayer.player.stop(audioStream);
		
			try{
    			InputStream music = getClass().getResourceAsStream("/resources/fluffingaduck.wav");
    			audioStream = new AudioStream(music);
    		}
    		catch(Exception e){
    			e.printStackTrace();
    		}		
    	}		
    		else
    			AudioPlayer.player.start(audioStream);
    		
    	musicPlaying = !musicPlaying;
	}
	
	//Setup game and control objects
	public void setup(){
		fireList.add(new Fireball());
		
		//Populate luigilist with LuigiBlocks. 
		//Two are necessary; the second one shows up when LuigiBlock wraps around the screen.
		for(int i = 0; i < 2; ++i){
			LuigiBlock l = new LuigiBlock();	
			luigiList.add(l);
			
			luigiList.get(i).setY(frameHeight - luigiGap * l.getHeight() - l.getHeight());	
			luigiList.get(i).setX((frameWidth / 2) -  (l.getWidth() / 2));
		}
		
		//Instantiate mouseRobot which will move the mouse back to a set position when it is moved.
		//This allows us to move LuigiBlock but keep the mouse in the same location
		try{
			mouseRobot = new Robot();
		}catch(Exception e){
			e.printStackTrace();
		}
	
		
		//Let's fetch our font
		try{
			InputStream streamFont = getClass().getResourceAsStream("/fonts/emulogic.ttf");
			Font emulogic = Font.createFont(Font.TRUETYPE_FONT, streamFont);
			
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(emulogic);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}	

		//General frame and panel setup
		int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
	
		//panel.g.setFont(new Font("emulogic", Font.PLAIN,25)); //font now being changed in paint's g. Compatibility issues?
		panel.setBackground(Color.BLACK);
		panel.setForeground(Color.GREEN);
		
		frame.setLocation(screenWidth/4, screenHeight/4);
		frame.getContentPane().add(BorderLayout.CENTER,panel);
        frame.setVisible(true);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(frameWidth,frameHeight);
    	frame.setResizable(false);

		frame.setFocusTraversalKeysEnabled(false);
		frame.addKeyListener(this);
		frame.addMouseListener(this);
		frame.addMouseMotionListener(this);
		
		//music stuffs
		try{
    		InputStream music = getClass().getResourceAsStream("/sounds/music/fluffingaduck.wav");
    		audioStream = new AudioStream(music);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
		
		//Lastly, set running to true to enter gameloop.
		running = true; 
	}
	
	//key Pressed
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		if(!paused){
			switch(keyCode){ 
				case KeyEvent.VK_ESCAPE:
						pause();
					break;
				case KeyEvent.VK_ENTER:
						setResetting();
					break;
				case KeyEvent.VK_F:
						musicPlayback();
					break;															 
				default:
					e.consume();
			}
		}
	}
	
	public static float getInitialFirePush(){
		return initialFirePush;
	}
	
	public void moveLuigi(){
		for(LuigiBlock l: luigiList){
			float prev = l.getX();
			l.setX(prev + 20);	
		}	
	}
	
	//Mouse Pressed
	public void mousePressed(MouseEvent e){
	
		if(mouseIn && !gameStarted)
			gameStart();
				
		if(!mouseIn && paused)
			unpause();
			
		if(!mouseIn){	
			mouseIn = true;
			frame.setCursor(transparentCursor); 
		
			mouse.x = MouseInfo.getPointerInfo().getLocation().x ; 
			mouse.y = MouseInfo.getPointerInfo().getLocation().y;
		}
		
	}
	
	public void gameStart(){
		gameStarted = true;
		fireList.get(0).changeVelocity(0f,-initialFirePush);
	}
	
	public void pause(){
		if(mouseIn && gameStarted){
			paused = true;
			mouseIn = false;
			frame.setCursor(null);
		}
		else if(mouseIn && !gameStarted){
			mouseIn = false;
			frame.setCursor(null);
		}
		
		if(musicPlaying)
			AudioPlayer.player.stop(audioStream);			
	}
	
	public void unpause(){
		paused = false;
		
		if(musicPlaying)
			AudioPlayer.player.start(audioStream);
	}
	
	public static float getFrameHeight(){
		return frameHeight;
	}
	
	//Mouse Dragged	
	public void mouseDragged(MouseEvent e){
			mouseMoved(e);
		}
	
	//Mouse Moved
	public void mouseMoved(MouseEvent e){
	
		if(mouseIn){
			int dx = MouseInfo.getPointerInfo().getLocation().x - mouse.x;
				
			for(LuigiBlock l: luigiList){
				float prev = l.getX();
				l.setX(prev + dx);	

				mouseRobot.mouseMove(mouse.x,mouse.y);
			}
		}
	}
	
	//Unused listeners 
	//These definitions must exist because of implemented interfaces.
	public void keyReleased(KeyEvent e){}
	public void keyTyped(KeyEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	//calls all of the methods which describes 
	//how objects should interact with each other
	public void mechanics(){
		luigiWrapAround();
		ducktargetWalls();
		
		if(!gameStarted)
			preGame();
		
		else {	
			fireballWalls();
			fireballLuigiBlock();
			fireballDucktargets();
			garbageThrowOut();
		}
		
		if(!gameOver){
			scoreEvents();
			isGameOver();
		}
	}
	
	public void luigiWrapAround(){
	
		for(int i = 0; i < luigiList.size(); ++i){
			float luigiX =  luigiList.get(i).getX();
			luigiX %= frameWidth;
			if(luigiX < 0)
				luigiX += frameWidth;
			
			luigiList.get(i).setX(luigiX);
			luigiList.get(++i).setX(luigiX - frameWidth);
		}
	}
	
	//what is shown before the game is started
	public void preGame(){		
			float fireX = luigiList.get(0).getX() + luigiList.get(0).getWidth() / 2 - fireList.get(0).getWidth() / 2;
			float fireY = luigiList.get(0).getY() - fireList.get(0).getHeight();
		
			fireList.get(0).setX(fireX);
			fireList.get(0).setY(fireY);
			
			if(fireList.get(0).getX() > frameWidth - fireList.get(0).getWidth() / 2 )
				fireList.get(0).setX(fireList.get(0).getX() - frameWidth);
	}
		
	public static void addFireOn(Fireball newFireToBe){
		fireQueue.add(newFireToBe);
	}	
	
	public void fireSpawn(){
		if(!fireQueue.isEmpty()){
			Fireball newFire = fireQueue.pop();
			fireList.add(newFire);
		}
	}
	
	public void addDuck(DuckTarget newDuckToBe){
		duckQueue.add(newDuckToBe);
	}
	
	public void duckSpawn(){
		if(!duckQueue.isEmpty()){
			//starting position
			DuckTarget newDuck = duckQueue.pop();
			int initX = (int) (Math.random() * panel.getWidth());
			newDuck.setX(initX);
			newDuck.setY(panel.getHeight());
		
			//starting velocity 
			float vx = (float) (Math.signum(Math.random() -.5) * (( Math.random()  * .20) + .10f));
			float vy = (float) (Math.random() * .05 + Math.abs(vx)) * -1;
			
			if (vx == 0){
				vy = -.35f;
			}
			
			newDuck.changeMotion(vx,vy);
			
			duckList.add(newDuck);
		}
	}
	
	public void spawn(){
		duckSpawn();
		fireSpawn();
	}
	
	public void garbageThrowOut(){
		for(int i = 0; i < duckList.size(); ++i){
			if (duckList.get(i).getY() > panel.getHeight())
				duckList.remove(i);
		}
		
		for(int i = 0; i < fireList.size(); ++i){
			if (fireList.get(i).getY() > panel.getHeight())
				fireList.remove(i);
		}
	}
	
	//how the ball bounces off the block
	public void fireballLuigiBlock(){
		for(LuigiBlock l : luigiList)
			for(Fireball f : fireList)
				if(collide(f, l)){
					
					float yCompo = -l.getWidth()/2;

					float xScalingFactor = 2;
					float x = f.getX();
					float xConverTerm = l.getX() + l.getWidth()/2 - f.getWidth()/2;
					float xPrime = xScalingFactor * (x - xConverTerm) + xConverTerm;
					float xCompo = xPrime - x; 

					f.changeDirection(xCompo, yCompo);
				}
	}
	
	//describes how the fireball should interact with the walls
	//--an elastic, mirror bounce
	public void fireballWalls(){
		for(Fireball f: fireList){
			if(f.getX() < 0 && f.getVelocityX() < 0)
				f.setVelocityX(Math.abs(f.getVelocityX()));
			else if(f.getX() + f.getWidth() >= panel.getWidth() && f.getVelocityX() > 0)
				f.setVelocityX(-Math.abs(f.getVelocityX()));
		
			if(f.getY() < 0 && f.getVelocityY() < 0)
				f.setVelocityY(Math.abs(f.getVelocityY()));
		}
	}
	
	public void isGameOver(){
		Boolean test = true;
	
		for(Fireball f : fireList)
			if (f.getY() < frameHeight) 
				test = false;
		
		gameOver = test;
	}
	
	//describes how the ducktargets should interact with the walls
	//(they should bounce off of them)
	//checks for position and velocity. The velocity check is necessary somehow...
	//changes motion pattern to transition animation
	public void ducktargetWalls(){
		for(DuckTarget d : duckList){
			if(d.getAlive()){
				if(d.getX() <= 0 && d.getVelocityX() < 0)
					d.changeMotion(Math.abs(d.getVelocityX()), d.getVelocityY());
				
				else if(d.getX() + d.getWidth() >= panel.getWidth() && d.getVelocityX() > 0)
					d.changeMotion(-Math.abs(d.getVelocityX()), d.getVelocityY());
		
				if(d.getY() <= 0 && d.getVelocityY() < 0)
					d.changeMotion(d.getVelocityX(), Math.abs(d.getVelocityY()));

				else if(d.getY() + d.getHeight() >= panel.getHeight() && d.getVelocityY() > 0)
					d.changeMotion(d.getVelocityX(), -Math.abs(d.getVelocityY()));
			}
		}
	}
	 
	public void fireballDucktargets() {
	//check the setSpeed method…
 		for(DuckTarget d : duckList)
			for(Fireball f : fireList)
			
				if(d.getAlive() && collide(d,f)){
			
					f.bounceAwayFrom(d, 60);
								
					f.setSpeed(f.getSpeed() * 1.19f);
			
					if(d instanceof RedDuck){
						powerUpFlash(d.getX(), d.getY(), d.getWidth(), d.getHeight());
						redDuckCount--;
					}
					d.kill();
			
					score();
				}
	}
	
	public void score(){
		score++;
		killsSinceLastBonus++;
	}
	
	public void scoreEvents(){
		int n = fireList.size() + redDuckCount;
		
		if(3+Math.pow(2,n) <= killsSinceLastBonus){
			redDuckTime = true;
			killsSinceLastBonus = 0;
		}
	}
	
	public boolean collide(Sprite s1, Sprite s2){
		float s1LeftX = s1.getX();
		float s1RightX = s1.getX() + s1.getWidth();
		float s2LeftX = s2.getX();
		float s2RightX = s2.getX() + s2.getWidth();
		
		float s1TopY = s1.getY();
		float s1BottY = s1.getY() + s1.getHeight();
		float s2TopY = s2.getY();
		float s2BottY = s2.getY() + s2.getHeight();
	
		//If one of the horizontal edges of the one of the sprites 
		//is between the horizontal edges of the other sprite...
		if((   ((s2LeftX >= s1LeftX) && (s2LeftX <= s1RightX))
			|| ((s2RightX >= s1LeftX) && (s2RightX <= s1RightX))
			|| ((s1LeftX >= s2LeftX) && (s1LeftX <= s2RightX))
			|| ((s1RightX >= s2LeftX) && (s1RightX <= s2RightX)))
			
			//...and the vertical edges of one of the sprites is in between
			//the vertical edges of the other sprite
			&& ( ((s2TopY >= s1TopY) && (s2TopY <= s1BottY))
			     || ((s2BottY >= s1TopY) && (s2BottY <= s1BottY))
			     || ((s1TopY >= s2TopY) && (s1TopY <= s2BottY))
			     || ((s1BottY >= s2TopY) && (s1BottY <= s2BottY))))
		{
			return true;
		}
		else{
			return false;
		}
	}

	//update method extension to update game and game assets
	public void update(long timePassed){	
			
			if(pufTime > 0)
				pufTimePass(timePassed);
		
			for(Fireball f: fireList)
				f.update(timePassed);
		
			for(LuigiBlock l: luigiList)
				l.update(timePassed);
		
			for(DuckTarget d: duckList)
				d.update(timePassed);
				
			if(gameStarted && !gameOver)
				duckTimer(timePassed);
	}
	
	public void gameLoop(){
		long startTime = System.currentTimeMillis();
		long cumTime = startTime;
		long timePassed;
		
		while(running){
			timePassed = System.currentTimeMillis() - cumTime;
			cumTime += timePassed;
			
			if(!paused){
				spawn();
				update(timePassed);
				mechanics();
				if(resetting)
					reset();
				panel.repaint();
			}
		}
		frame.dispose();
	}
	
	public void powerUpFlash(float x, float y, int width, int height){
		pufTime = 100;
		flashLoc[0] = x;
		flashLoc[1] = y;
		flashLoc[2] = width;
		flashLoc[3] = height;
	}
	
	public void pufTimePass(long time){
		pufTime -= time;
	}
	
	public void duckTimer(long timePassed){
		duckCountdown -= timePassed;
		if(duckCountdown <= 0){
			if(redDuckTime) {
				addDuck(new RedDuck());
				redDuckTime = false;
				redDuckCount++;
				}
			else
				addDuck(new PurpDuck());
			duckCountdown = duckTiming;
		}
	}
	
	class MyPanel extends JPanel{
		
		public void paint(Graphics g){	
			//background
			g.setColor(panel.getBackground());
			g.fillRect(0,0,frame.getWidth(),frame.getHeight());
				
			//ducks(s)
			g.setColor(Color.WHITE);
			for(DuckTarget d: duckList)
				if(pufTime <= 0)
					g.drawImage((Image) d.getImage(),Math.round(d.getX()),Math.round(d.getY()),null);
				else{
					if(!(d instanceof PurpDuck)){
						g.fillRect(Math.round(d.getX()), Math.round(d.getY()), d.getWidth(), d.getHeight());
						g.drawImage((Image) d.getImage(),Math.round(d.getX()),Math.round(d.getY()),null);
					} 
					g.fillRect(Math.round(flashLoc[0]), Math.round(flashLoc[1]), Math.round(flashLoc[2]), Math.round(flashLoc[3]));
				}
					
			//Luigi
			for(LuigiBlock l: luigiList)
				g.drawImage(l.getImage(),Math.round(l.getX()),Math.round(l.getY()),null);
			
			//fireball(s)
			for(Fireball f: fireList)
				g.drawImage(f.getImage(),Math.round(f.getX()),Math.round(f.getY()),null);
			
			g.setColor(panel.getForeground());
			//getGraphics().setFont(new Font("emulogic", Font.PLAIN,25));
			g.setFont(new Font("emulogic", Font.PLAIN,25)); //find a way to do this only once though?
			g.drawString("x"+ score,(int) (frame.getWidth()*.025f), (int) (frame.getHeight()*.93f));
			
			if(!gameStarted){
				int h = g.getFontMetrics().getHeight();
				for (int i=0; i<pregameMessage.length; i++)
					g.drawString(pregameMessage[i], (int) (frame.getWidth() / 3.5), (int) (frame.getHeight() / 4)+(h*i) + h);
			}
			else if(gameOver){
				int h = g.getFontMetrics().getHeight();
				for (int i=0; i<postgameMessage.length; i++)
					g.drawString(postgameMessage[i], (int) (frame.getWidth() / 3.5), (int) (frame.getHeight() / 4)+(h*i) + h);
			}
			
		}
	} 
}

