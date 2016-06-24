package com.beans;

import java.awt.*;

import javax.swing.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
//import java.io.File;

public abstract class DuckTarget extends Sprite{ 
	
	enum Angle{
		FLYEAST("flyEast"), 
		FLYWEST("flyWest"), 
		FLYNORTH("flyNorth"), 
		FLYNORTHEAST("flyNortheast"), 
		FLYNORTHWEST("flyNorthwest"), 
		SHOTEAST("shotEast"), 
		SHOTWEST("shotWest"), 
		FALLEAST("fallEast"), 
		FALLWEST("fallWest");
		 
		private String value;
		
		String getValue(){
			return value;
		}
		
		Angle(String value){
			this.value = value;
		}
	}
	
	private boolean alive = true;
	long killPause = 1000;
	float killDropSpeed = .2f;		 
	DuckKillBehavior duckKillBehavior;
	int imageScaleFactor = 2;
	
	public DuckTarget(String assetsLoc){
		
		//The location tag and number of images expected
		HashMap<Angle, Integer> imageIndex = new HashMap<Angle, Integer>();
		
		imageIndex.put(Angle.FLYEAST, 3);
		imageIndex.put(Angle.FLYWEST, 3);
		imageIndex.put(Angle.FLYNORTH, 3);
		imageIndex.put(Angle.FLYNORTHEAST, 3);
		imageIndex.put(Angle.FLYNORTHWEST, 3);
		imageIndex.put(Angle.SHOTEAST, 1);
		imageIndex.put(Angle.SHOTWEST, 1);
		imageIndex.put(Angle.FALLEAST, 2);
		imageIndex.put(Angle.FALLWEST, 2);
		
		try{
			for (Map.Entry<Angle, Integer> index : imageIndex.entrySet()) {
				animations.put(index.getKey().getValue(), new Animation());
				for(int i = 1; i <= index.getValue() ; ++i ){
					
						String location = assetsLoc + index.getKey().getValue() + i + ".png";
						BufferedImage thisPic = ImageIO.read(this.getClass().getResource(location)); 
					
						int h = thisPic.getHeight(null);
						int w = thisPic.getWidth(null);
				
						Image thisPicScaled = thisPic.getScaledInstance(w * imageScaleFactor,h * imageScaleFactor,0);
						
						animations.get(index.getKey().getValue()).addImage(thisPicScaled);
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	//making this zero causes strange, unexpected errors
	private long flightPathTime = (long) (Math.random() * 1500) + 500;	
	
	int count;
	
	public void update(long timePassed){
		super.update(timePassed);
		updateFlightPath(timePassed);
	}
	
	public void kill(){
		duckKillBehavior.kill(this);
	}
	
	public void updateFlightPath(long timePassed){
		if(flightPathTime <= 0 && getAlive()){
					
			float newDX = (float) Math.random() - .5f;
			float newDY = (float) Math.random() - .5f;
			flightPathTime = (long) (Math.random() * 2000);
					
			//I don't think the speed method works very well.
			turn(newDX, newDY);
			//does it freeze... though?
		}
		else{
			flightPathTime -= timePassed;

			}
	}
	
	//changes the direction of the sprite to the input vector <i,j>
	//while maintaining the same speed
	public void turn(float di, float dj){
		float newDirSpeed = (float) Math.sqrt(Math.pow(di, 2) + Math.pow(dj, 2));
		float normalizedDI = di / newDirSpeed;
		float normalizedDJ = dj / newDirSpeed;
		
		float originalSpeed = (float) Math.sqrt(Math.pow(getVelocityX(), 2) + Math.pow(getVelocityY(), 2));
		float newVX = originalSpeed * normalizedDI;
		float newVY = originalSpeed * normalizedDJ;			
		
		changeMotion(newVX, newVY);
	}
	
	public void setFlightPathTime(long time){
		flightPathTime = time;
	}
	
	public void changeMotion(float vx, float vy){
		double newDegree;
		double oldDegree;
		Animation anime;

		if(vx == 0 && vy == 0)
			newDegree = -1;
		else
			newDegree = (Math.atan2(vy,-vx) + Math.PI) * 360 /(2 * Math.PI) ;
		
		if(getVelocityX() == 0 && getVelocityY() == 0)
			oldDegree = -1;
		else		
			oldDegree = (Math.atan2(getVelocityY(),-getVelocityX()) + Math.PI) * 360 /(2 * Math.PI);

		if((newDegree >= 0 && newDegree < 45 || newDegree >= 315) && !(oldDegree >= 0 && oldDegree < 45 || oldDegree >= 315))
			anime = animations.get(Angle.FLYEAST.getValue());

		else if((newDegree >= 45 && newDegree < 90) && !(oldDegree >= 45 && oldDegree < 90))
				anime = animations.get(Angle.FLYNORTHEAST.getValue());		
						
		else if(newDegree == 90 && !(oldDegree == 90))				
				anime = animations.get(Angle.FLYNORTH.getValue());	
					
		else if((newDegree > 90 && newDegree < 135) && !(oldDegree > 90 && oldDegree < 135))
				anime = animations.get(Angle.FLYNORTHWEST.getValue());	
			
		else if((newDegree >= 135 && newDegree < 225) && !(oldDegree >= 135 && oldDegree < 225))
				anime = animations.get(Angle.FLYWEST.getValue());	
			
		else if((newDegree >= 225 && newDegree <= 270) && !(oldDegree >= 225 && oldDegree <= 270))
				anime = animations.get(Angle.FLYNORTHWEST.getValue());	
				
		else if((newDegree > 270 && newDegree < 315) && !(oldDegree > 270 && oldDegree < 315))
				anime = animations.get(Angle.FLYNORTHEAST.getValue());	
		else
			anime = null;
	
		getMotionPattern().addMotion(vx,vy,0,anime);
	}
	
	public boolean getAlive(){
		return alive;
	}
	
	public void setAlive(boolean yesOrNo){
		alive = yesOrNo;
	}
	
}
