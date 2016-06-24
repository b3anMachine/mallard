package com.beans;

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class DuckKillDrop implements DuckKillBehavior{ 
	
	public void kill(DuckTarget duck) {
		duck.setAlive(false);		
		
		if(duck.getVelocityX() > 0){
			duck.getMotionPattern().addMotion(0,0,500,duck.animations.get(DuckTarget.Angle.SHOTEAST.getValue()));
			duck.getMotionPattern().addMotion(0,.5f,0,duck.animations.get(DuckTarget.Angle.FALLEAST.getValue()));
		}
		else {
			duck.getMotionPattern().addMotion(0,0,500,duck.animations.get(DuckTarget.Angle.SHOTWEST.getValue()));
			duck.getMotionPattern().addMotion(0,.5f,0,duck.animations.get(DuckTarget.Angle.FALLWEST.getValue()));
		}
	}
}