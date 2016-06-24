package com.beans;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.*;

public class LuigiBlock extends Sprite{ 

	private Animation luigi = new Animation();
	private int width = 108;
	private int height = 54;

	LuigiBlock(){		
		//fill the luigi animation
		try{
			for(int i = 1; i <= 4; ++i){
				String location = "/images/nes luigi sideways/luigi";
				location += i + ".png";
				BufferedImage thisPic = ImageIO.read(this.getClass().getResource(location)); 
				Image thisPicScaled = thisPic.getScaledInstance(width,height,0);
				luigi.addImage(thisPicScaled);
			
				this.color(luigi);
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
}

