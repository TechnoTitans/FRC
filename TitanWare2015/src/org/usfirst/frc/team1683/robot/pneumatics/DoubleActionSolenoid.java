package org.usfirst.frc.team1683.robot.pneumatics;

import org.usfirst.frc.team1683.robot.sensors.PressureSensor;

public class DoubleActionSolenoid {
	AirSystem controlAirSystem;
	AirSystem followingAirSystem;
	boolean inverse;
	
	/**
	 * Default Constructor
	 * @param pistons
	 * @param pressure - PressureSensor
	 * @param initState - initial state of controlling boolean value
	 */
	public DoubleActionSolenoid(int[] pistons, PressureSensor pressure) { //front piston, back Piston
		controlAirSystem = new AirSystem(new int[]{pistons[0]}, pressure);
		followingAirSystem = new AirSystem(new int[]{pistons[1]}, pressure);
	}
	
	/**
	 * Constructor to use when inverting function is needed
	 * @param pistons array of pistons
	 * @param pressure not used here, required for AirSystem
	 * @param inverse set default to false, set to true to invert functions
	 */
	public DoubleActionSolenoid(int[] pistons, PressureSensor pressure, boolean inverse) { //front piston, back Piston
		if (inverse){
			controlAirSystem = new AirSystem(new int[]{pistons[1]}, pressure);
			followingAirSystem = new AirSystem(new int[]{pistons[0]}, pressure);
		}
		else{
			controlAirSystem = new AirSystem(new int[]{pistons[0]}, pressure);
			followingAirSystem = new AirSystem(new int[]{pistons[1]}, pressure);
		}	
	}
	
	/**
	 * @author Animesh Koratana
	 * returns the front air system of dual action pistons
	 * @return frontAirSystem
	 */
	public AirSystem getFrontAirSystem(){
		return controlAirSystem;
	}
	
	/**
	 * @author Animesh Koratana
	 * returns the back air system of dual action pistons
	 * @return backAirSystem
	 */
	public AirSystem getBackAirSystem(){
		return followingAirSystem;
	}
	
	/**
	 * @author Animesh Koratana
	 * switches between the two useful states of the dual action solenoids
	 */
	public void changeState(){
		if (controlAirSystem.isExtended()){
			controlAirSystem.retract();
			followingAirSystem.extend();
		}else{
			controlAirSystem.extend();
			followingAirSystem.retract();
		}
	}
	
	public void extend(){
		controlAirSystem.extend();
		followingAirSystem.retract();
	}
	
	public void retract(){
		controlAirSystem.retract();
		followingAirSystem.extend();
	}
	
	public boolean isExtended(){
		return controlAirSystem.isExtended();
	}
}
