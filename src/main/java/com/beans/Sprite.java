package com.beans;

import java.awt.*;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;

public abstract class Sprite {
	
	
	private float x;
	private float y;	
	private float vx;	
	private float vy;

	//BufferedImage image;
	Animation a;
	HashMap<String, Animation> animations = new HashMap<String, Animation>();

	private MotionPattern MP = new MotionPattern();
	//private long motionTime;
	
	//So that subclasses can fill their art assets 
	public void color(Animation a){
		this.a = a;
	}
	
	//to change position with passed time
	public void update(long timePassed){
		if(MP.hasMotion())
			updateMotionPattern(timePassed);	
		x += vx * timePassed;
		y += vy * timePassed;
		a.update(timePassed);
	}
	
	public void updateMotionPattern(long timePassed){
		if(!MP.getCurrentMotion().getInitialized()){
			changeVelocity(MP.getCurrentMotion().getVelocityX(),MP.getCurrentMotion().getVelocityY());
			if(MP.getCurrentMotion().hasNewColor())
				color(MP.getCurrentMotion().getColor());
			MP.getCurrentMotion().initialize();
		}
		if(MP.getCurrentMotion().getPeriod() >= MP.getMotionTime())
			MP.updateMotionTime(timePassed);
		else{
			MP.setMotionTime(0);
			if(MP.getCyclical())
				MP.recycleCurrentMotion();
			else
				MP.removeCurrentMotion();
			}
	}
	
	//changes the direction of the sprite to the input vector <i,j>
	//while maintaining the same speed
	public void changeDirection(float di, float dj){
		float newDirSpeed = (float) Math.sqrt(Math.pow(di, 2) + Math.pow(dj, 2));
		float normalizedDI = di / newDirSpeed;
		float normalizedDJ = dj / newDirSpeed;
		
		float originalSpeed = (float) Math.sqrt(Math.pow(getVelocityX(), 2) + Math.pow(getVelocityY(), 2));
		float newVX = originalSpeed * normalizedDI;
		float newVY = originalSpeed * normalizedDJ;			
		
		changeVelocity(newVX, newVY);
	}
	
	//changes the speed of the sprite while maintaining its direction
	public void setSpeed(float s){
		float ratio = Math.abs(vx/vy);
		
		float newVY = Math.signum(vy) * (float) Math.sqrt(Math.pow(s, 2) / (Math.pow(ratio, 2) + 1));
		float newVX = Math.signum(vx) * (float) newVY * ratio;
		
		changeVelocity(newVX, newVY);
	}
	
	//changes direction away from input sprite
	//randomly within the angle range -delta to +delta.
	//Note delta is set to degrees, not radians. 
	public void bounceAwayFrom(Sprite s, double delta){
		
		//obtain radian conversion of delta
		double deltaRad = (delta * Math.PI) / 180;
	
		double ax = getCenterX() - s.getCenterX();
		double ay = getCenterY() - s.getCenterY();
		
		double originalAngle = Math.atan2(ay,ax); //atan2 takes in x and y in the order [y,x]
		
		if(originalAngle < 0)
			originalAngle += 2*Math.PI;
		
		double angleShift = 2 * deltaRad * Math.random() - deltaRad;
		
		double newAngle = originalAngle + angleShift;
		
		changeDirection((float)Math.cos(newAngle), (float)Math.sin(newAngle));
	}
	
	public float getSpeed(){
		return (float) Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));
	}

	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}

	public void setX(float x){
		this.x = x;
	}
	
	public void setY(float y){
		this.y = y;
	}
	
	public void changeLocation(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public int getWidth() {
		return a.getImage().getWidth(null);
	}
	
	public int getHeight() {
		return a.getImage().getHeight(null);
	}
	
	//get horizontal velocity 
	public float getVelocityX(){
		return vx;
	}
	
	//get vertical velocity 
	public float getVelocityY(){
		return vy;
	}
	
	//set horizontal velocity 
	public void setVelocityX(float vx){
		this.vx = vx;
	}
	
	//get vertical velocity 
	public void setVelocityY(float vy){
		this.vy = vy;
	}
	
	public void changeVelocity(float vx, float vy){
		setVelocityX(vx);
		setVelocityY(vy);
	}
	
	//get sprites image
	public Image getImage(){
		return a.getImage();
	}	
	
	public float getCenterX(){
		return getX() + .5f * getWidth();
	}
	
	public float getCenterY(){
		return getY() + .5f * getHeight();
	}
	
	MotionPattern getMotionPattern(){
		return MP;
	}

}