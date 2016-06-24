package com.beans;

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;


public class RedDuck extends DuckTarget{ 

	RedDuck(){
		super("/images/duck/red/");
		duckKillBehavior = new DuckKillFire();
	}
	
}