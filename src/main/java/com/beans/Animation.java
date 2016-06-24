package com.beans;

import java.util.*;
import java.awt.*;


public class Animation {

	private ArrayList<Image> images = new ArrayList<Image>(); 
	private int fps = 7;
	private boolean cyclical = true;
	private long movieLengthTime;
	private long currentTime; 
	private int imagesIndex;
	private boolean finishedAnimation;
	
	public void update(long timeSinceLastUpdate) {
		if(cyclical || !(imagesIndex+1 == images.size())) {
			currentTime += timeSinceLastUpdate;
			
			if(currentTime >= movieLengthTime){
				currentTime = 0;
				imagesIndex = 0;
			}
		
			long time = (imagesIndex+1)*(movieLengthTime / images.size());
				
			if(currentTime > time){
				imagesIndex++;
			}
		}
		else
			finishedAnimation = true;
	}
	
	public void addImage(Image i){
		images.add(i);
		this.movieLengthTime += ( ((double) 1 /  fps) * 1000);
	}
	
	public Image getImage(){
		if(images.size() == 0 || finishedAnimation) 
			return null;
		else
			return images.get(imagesIndex);
	}
	
	public void changeFPS(int fps){
		this.fps = fps;
		movieLengthTime = images.size() / fps * 1000;
	}
	
	public void cyclical(boolean yesOrNo){
		this.cyclical = yesOrNo;
	}

}