package com.beans;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Fireball extends Sprite{ 

	Animation fire = new Animation();
	private int width = 27;
	private int height = 27;	
	
	Fireball(){	
	
	//fill the fire animation
	try{
		for(int i = 1; i <= 4; ++i){
			String location = "/images/fireball/fireball";
			location += i + ".png";
			BufferedImage thisPic = ImageIO.read(this.getClass().getResource(location));
			Image thisPicScaled = thisPic.getScaledInstance(width,height,0);
			fire.addImage(thisPicScaled);
			}
	}
	catch(IOException e){
		e.printStackTrace();
	}
		
		this.color(fire);
	}

}