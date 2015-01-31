package org.usfirst.frc.team1683.robot.drivetrain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Animesh Koratana
 *
 */
public class MotorGroup {
	List<Motor> motors;
	Encoder encoder;
	/**
	 * Constructor
	 * @param channelNumbers
	 * @param motorType
	 * @param inverseDirection
	 */
	public MotorGroup(int[] channelNumbers, Class<Motor> motorType, boolean inverseDirection)  {
		motors = new ArrayList<Motor>();
		for (int i = 0; i < channelNumbers.length; i++) {
			int j = channelNumbers[i];
			if (motorType.equals(Talon.class)){
				motors.add(new Talon(j, inverseDirection));
			}else if (motorType.equals(TalonSRX.class)){
				motors.add(new TalonSRX(j, inverseDirection));
			}
		}
	}
	/**
	 * Constructor
	 * @param channelNumbers
	 * @param motorType
	 * @param inverseDirection
	 * @param encoder
	 */
	
	public MotorGroup(int[] channelNumbers, Class<Motor> motorType, boolean inverseDirection, Encoder encoder)
	{
		motors = new ArrayList<Motor>();
		for (int i = 0; i < channelNumbers.length; i++) {
			int j = channelNumbers[i];
			if (motorType.equals(Talon.class)){
				motors.add(new Talon(j, inverseDirection));
			}else if (motorType.equals(TalonSRX.class)){
				motors.add(new TalonSRX(j, inverseDirection));
			}
		}
		this.encoder = encoder;
	}
	
	/**
	 * moves the robot a certain distance
	 * @param distanceInMeters
	 */
	public void moveDistance(double distanceInMeters){
		for (Motor motor: motors){
			motor.moveDistance(distanceInMeters);
		}
	}
	
	/**
	 * moves the robot a certain amount of degrees 
	 * @param degrees
	 */
	public void moveDegrees(double degrees){
		for (Motor motor: motors){
			motor.moveDegrees(degrees);
		}
	}
	/**
	 * sets the speed of the motor
	 * @param speed
	 */
	public void set(double speed){
		for (Motor motor: motors){
			motor.set(speed);
		} 
	}
	/**
	 * stops the motor
	 */
	public void stop() {
		for (Motor motor: motors){
			motor.stop();
		} 
	}

}
