package org.usfirst.frc.team4913.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;

public class AutoCode {
	Preferences prefs = Preferences.getInstance();
	private Timer m_timer = new Timer();

	static boolean driveStraightDeliverCube = false;
	static boolean midLeftDeliverCube = false;
	static boolean midRightDeliverCube = false;
	static boolean driveStraightNoCube = false;
	Robot realRobot;
	
	public void initiation() {
		m_timer.reset();
		m_timer.start();

		int robotPosition = prefs.getInt("robot position", 1);
		SmartDashboard.putNumber("Robot Position", robotPosition);

		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		SmartDashboard.putString("Game Data", gameData);
		if ((robotPosition == 1 && gameData.charAt(0) == 'L') || (robotPosition == 3 && gameData.charAt(0) == 'R')) {
			// we're in corner position and switch is our side
			driveStraightDeliverCube = true;
		} else if (robotPosition == 2) {
			if (gameData.charAt(0) == 'L') {
				midLeftDeliverCube = true;
			} else if (gameData.charAt(0) == 'R') {
				midRightDeliverCube = true;
			}
		} else {
			driveStraightNoCube = true;
		}
	}
	
	public void run() {
		if (driveStraightNoCube) {
			if (m_timer.get() < 15.0) {
				realRobot.m_robotDrive.arcadeDrive(-0.5, 0.0); // drive forwards half speed
			} else {
				realRobot.m_robotDrive.stopMotor(); // stop robot
			}

		} else if (driveStraightDeliverCube) {
			if (m_timer.get() < 5.0) {
				realRobot.m_robotDrive.arcadeDrive(-0.5, 0.0); // drive forwards half speed
			} else {
				realRobot.m_robotDrive.stopMotor(); // stop robot
			}
		} else if (midLeftDeliverCube) {
			if (m_timer.get() < 2.0) {
				realRobot.m_robotDrive.arcadeDrive(-0.5, 0.0); // drive forwards half speed
			} else if (m_timer.get() < 5.0) {
				realRobot.m_robotDrive.arcadeDrive(-0.0, 0.5);
			} else {
				realRobot.m_robotDrive.stopMotor(); // stop robot
			}
		} else if (midRightDeliverCube) {
			if (m_timer.get() < 2.0) { 
				realRobot.m_robotDrive.arcadeDrive(-0.5, 0.0); // drive forwards half speed
			} else if (m_timer.get() < 5.0) {
				realRobot.m_robotDrive.arcadeDrive(-0.0, -0.5);
			} else {
				realRobot.m_robotDrive.stopMotor(); // stop robot
			}

		}

	}
}
