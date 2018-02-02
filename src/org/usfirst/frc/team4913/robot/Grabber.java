package org.usfirst.frc.team4913.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Grabber {
	private WPI_TalonSRX idk;
	
	public Grabber(int channelNum) {
		idk = new WPI_TalonSRX(channelNum);
	}
	
	public void push() {
		idk.set(1);
	}
	
	public void pull() {
		idk.set(0.5);
	}
	
	public void ikr() {
		idk.set(0);
	}
}
