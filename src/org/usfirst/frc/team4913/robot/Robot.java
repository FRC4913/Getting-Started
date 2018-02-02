/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4913.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
	// private Joystick m_stick = new Joystick(0);

	private static final double MVOLTS_TO_INCHES = 9.8; // 9.8 mV per inch
	AutoCode autoCode;
	private Timer m_timer = new Timer();

	AnalogInput ultrasonic1 = new AnalogInput(1);
	AnalogInput ultrasonic2 = new AnalogInput(2);
	private XboxController controller = new XboxController(0);

	private DigitalInput pin7 = new DigitalInput(7);
	private DigitalInput pin8 = new DigitalInput(8);
	WPI_TalonSRX rightRearCANMotor = new WPI_TalonSRX(0);
	WPI_TalonSRX leftRearCANMotor = new WPI_TalonSRX(1);
	WPI_TalonSRX rightFrontCANMotor = new WPI_TalonSRX(2);
	WPI_TalonSRX leftFrontCANMotor = new WPI_TalonSRX(3);
	private WPI_TalonSRX idk = new WPI_TalonSRX(4);

	Preferences prefs;
	Grabber grabber;
	
	// Autonomous Modes
	private boolean driveStraightDeliverCube = false;
	private boolean driveStraightNoCube = false;
	private boolean midLeftDeliverCube = false;
	private boolean midRightDeliverCube = false;

	// defines the starting position of the robot in the field when looking from
	// behind the driver station
	// 1 = Left
	// 2 = Middle
	// 3 = Right
//	private int robotPosition;

	UsbCamera camera;
	

	SpeedControllerGroup leftGroup = new SpeedControllerGroup(leftFrontCANMotor, leftRearCANMotor);
	SpeedControllerGroup rightGroup = new SpeedControllerGroup(rightFrontCANMotor, rightRearCANMotor);
	DifferentialDrive m_robotDrive = new DifferentialDrive(leftGroup, rightGroup);

	double ySpeed = 0;
	double yControllerInput;
	double scaledYcontrollerInput;
	double ySpeedScale = 0.01;// placeholder
	double yDiff;
	double scaledYDiff;
	// yDiff = ySpeed - scaledYcontrollerInput
	// yDiff will be used to decelerate the robot
	double rotationSpeed;

	int ultrasonic1Val, ultrasonic2Val;

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {
		prefs = Preferences.getInstance();
		camera = CameraServer.getInstance().startAutomaticCapture();
		grabber = new Grabber(4);
	}

	/**
	 * This function is run once each time the robot enters autonomous mode.
	 */
	@Override
	public void autonomousInit() {
		//autoCode.initiation();
		m_timer.reset();
		m_timer.start();

		// 1 = Left
		// 2 = Middle
		// 3 = Right
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


	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		//autoCode.run();
		if (driveStraightNoCube) {
			if (m_timer.get() < 15.0) {
				m_robotDrive.arcadeDrive(-0.5, 0.0); // drive forwards half speed
			} else {
				m_robotDrive.stopMotor(); // stop robot
			}

		} else if (driveStraightDeliverCube) {
			if (m_timer.get() < 5.0) {
				m_robotDrive.arcadeDrive(-0.5, 0.0); // drive forwards half speed
			} else {
				m_robotDrive.stopMotor(); // stop robot
			}
		} else if (midLeftDeliverCube) {
			if (m_timer.get() < 2.0) {
				m_robotDrive.arcadeDrive(-0.5, 0.0); // drive forwards half speed
			} else if (m_timer.get() < 5.0) {
				m_robotDrive.arcadeDrive(-0.0, 0.5);
			} else {
				m_robotDrive.stopMotor(); // stop robot
			}
		} else if (midRightDeliverCube) {
			if (m_timer.get() < 2.0) { 
				m_robotDrive.arcadeDrive(-0.5, 0.0); // drive forwards half speed
			} else if (m_timer.get() < 5.0) {
				m_robotDrive.arcadeDrive(-0.0, -0.5);
			} else {
				m_robotDrive.stopMotor(); // stop robot
			}

		}

	}

	/**
	 * This function is called once each time the robot enters teleoperated mode.
	 */
	@Override
	public void teleopInit() {
	}

	/**
	 * This function is called periodically during teleoperated mode.
	 */
	@Override
	public void teleopPeriodic() {

		rotationSpeed = -controller.getX(Hand.kLeft);
		yControllerInput = controller.getY(Hand.kLeft);
		yDiff = yControllerInput - ySpeed;
		scaledYDiff = yDiff * ySpeedScale;
		ySpeed += scaledYDiff;

		m_robotDrive.arcadeDrive(ySpeed, rotationSpeed);
		// m_robotDrive.arcadeDrive(m_stick.getY(), -m_stick.getX());
		
		if(controller.getXButton()) {
			grabber.pull();
		} else if(controller.getAButton()) {
			grabber.push();
		}else {
			grabber.ikr();
		}

		
		ultrasonic1Val = (int) (ultrasonic1.getValue() / MVOLTS_TO_INCHES);
		ultrasonic2Val = (int) (ultrasonic2.getValue() / MVOLTS_TO_INCHES);
		System.out.println("us1 value: " + ultrasonic1Val);
		System.out.println("us2 value: " + ultrasonic2Val);

		SmartDashboard.putBoolean("Pin 7", pin7.get());
		SmartDashboard.putBoolean("Pin 8", pin8.get());
		SmartDashboard.putNumber("Speed of y", ySpeed);

		SmartDashboard.putNumber("us1 value", ultrasonic1Val);
		SmartDashboard.putNumber("us2 value", ultrasonic2Val);
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}