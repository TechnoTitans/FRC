package org.usfirst.frc.team1683.robot.pickerupper;

import org.usfirst.frc.team1683.robot.HWR;
import org.usfirst.frc.team1683.robot.drivetrain.Encoder;
import org.usfirst.frc.team1683.robot.drivetrain.Motor;
import org.usfirst.frc.team1683.robot.drivetrain.MotorGroup;
import org.usfirst.frc.team1683.robot.main.DriverStation;
import org.usfirst.frc.team1683.robot.pneumatics.AirSystem;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;


public class PickerUpper {
	MotorGroup motors;
	AirSystem liftPistons;
	MotorGroup liftMotors;
	Encoder encoder;
	int liftButton;
	final double AUTO_LIFT_SPEED = 0.5;

	/**
	 * Constructor
	 * @param pickerUpperChannels
	 * @param talonSRX
	 * @param inverseDirection
	 */
	public PickerUpper(int[] pickerUpperChannels, Class talonSRX, boolean inverseDirection){
		this.motors = new MotorGroup(pickerUpperChannels, talonSRX, inverseDirection);

	}
	/**
	 * Constructor
	 * @param pickerUpperChannels
	 * @param talonSRX
	 * @param inverseDirection
	 * @param beltChannelA
	 * @param beltChannelB
	 * @param liftPiston
	 * @param leftMotor
	 * @param rightMotor
	 * @param motorType
	 */
	public PickerUpper(int[] pickerUpperChannels, Class<Motor> talonSRX, boolean inverseDirection,
			int beltChannelA, int beltChannelB, int liftPiston, int leftMotor, int rightMotor, Class<Motor> motorType){
		this.motors = new MotorGroup(pickerUpperChannels, talonSRX, inverseDirection, 
				encoder);
		int[] channelNumbers = {leftMotor, rightMotor};
		int [] piston = {liftPiston};
		liftPistons = new AirSystem(new Compressor(), piston);
		liftMotors = new MotorGroup(channelNumbers, motorType , false);
		encoder = new Encoder (beltChannelA, beltChannelB, inverseDirection);
		
	}

	public void liftMode(Joystick auxStick) {
		int joystickNum = 0;
		int button = 3;
		if (DriverStation.antiBounce(joystickNum, button)) {
			angledPickerUpper();
		}
		else {
			uprightPickerUpper();	
		}
	}


	/**
	 * Lifts the pickerupper device into the straight position
	 */
	public void uprightPickerUpper() {
		liftPistons.extend();
	}

	/**
	 * Brings back the pickerupper device into an angle
	 */
	public void angledPickerUpper() {
		liftPistons.retract();
	}


	/**
	 * runs the motors for the pickerupper
	 */
	public void run(){
		motors.set(DriverStation.auxStick.getRawAxis(DriverStation.YAxis));
	}
	
	public void runAuto (double liftDistance){
		// Need to find getDisplacement parameter value (DISTANCE_PER_PULSE) for belt motor.
		if (encoder.getDisplacement(47.0/700.0) <= liftDistance)
			motors.set(AUTO_LIFT_SPEED);
		
	}


}
