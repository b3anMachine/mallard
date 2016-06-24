package com.beans;

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class DuckKillFire implements DuckKillBehavior{ 
	
	public void kill(DuckTarget duck) {

		Fireball bonus = new Fireball();
		bonus.changeLocation(duck.getX(), duck.getY());
		
		bonus.changeVelocity((float) Math.random(), (float) Math.random());	
		bonus.changeDirection((float) Math.random(), (float) Math.random());
		
		bonus.setSpeed(Game.getInitialFirePush());
		
		Game.addFireOn(bonus);
		
		duck.setAlive(false);		
		duck.setY(Game.getFrameHeight() + 1);
	}
}