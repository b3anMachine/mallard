package com.beans;

import java.awt.*;

import javax.swing.*;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.io.File;


public class PurpDuck extends DuckTarget{ 

	PurpDuck(){
		super("/images/duck/purp/");
		duckKillBehavior = new DuckKillDrop();
	}
	
}
