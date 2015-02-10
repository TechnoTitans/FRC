package org.usfirst.frc.team1683.robot.main.autonomous;


public class Auto_1 extends Autonomous{
	
	public static void run(){
		switch(presentState){
		case INIT_CASE:
		{
			timer.start();
			nextState = State.DRIVE_FORWARD;
			break;
		}
		case DRIVE_FORWARD:
		{
			driveTrain.goStraight(driveDistance);
			nextState = State.END_CASE;
			break;
		}
		case END_CASE:
		{
			driveTrain.stop();			
			nextState = State.END_CASE;
			break;
		}
		}
		printState();
		presentState = nextState;
	}
}
