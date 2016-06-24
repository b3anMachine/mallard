package com.beans;

import java.util.*;
import java.awt.Image;

public class MotionPattern {
	
    LinkedList<Motion> motionList = new LinkedList<Motion>();
	private boolean cyclical;
	private long motionTime;
	
	public void addMotion(float VY, float VX, long period){
		Motion m = new Motion(VY, VX, period);
		motionList.add(m);
	}
	
	public void addMotion(float VY, float VX, long period, Animation color){
		Motion m = new Motion(VY, VX, period, color);
		motionList.add(m);
	}
	
	public void setCyclical(boolean yesOrNo){
		cyclical = yesOrNo;
	} 
	
	public boolean getCyclical(){
		return cyclical;
	}
	
	public void clearPattern(){
		motionList.clear();
	}
	
	public boolean hasMotion(){
		return !motionList.isEmpty();
	}
	
	public Motion getCurrentMotion(){
		return motionList.get(0);
	}
	
	public void removeCurrentMotion(){
		motionList.removeFirst();
	}
	
	public void recycleCurrentMotion(){
		motionList.addLast(motionList.removeFirst());
	}
	
	public long getMotionTime() {
		return motionTime;
	}

	public void setMotionTime(long motionTime) {
		this.motionTime = motionTime;
	}
	
	public void updateMotionTime(long timePassed){
		motionTime += timePassed;
	}

	class Motion{
		private float VY;
		private float VX;
		private long period;
		private Animation color;
		private boolean initialized;
		
		Motion(float VX, float VY, long period){
			this.VX = VX;
			this.VY = VY;
			this.period = period;
		}
		
		Motion(float VX, float VY, long period, Animation a){
			this.VX = VX;
			this.VY = VY;
			this.period = period;
			this.color = a;
		}
		
		public float getVelocityX(){
			return VX;
		}

		public float getVelocityY(){
			return VY;
		}
		
		public long getPeriod(){
			return period;
		}
		
		public boolean hasNewColor(){
			if(color == null)
				return false;
			else
				return true;
		}
		
		public Animation getColor(){
			return color;
		}
		
		public void initialize(){
			initialized = true;
		}
		
		public boolean getInitialized(){
			return initialized;
		}
	}
}