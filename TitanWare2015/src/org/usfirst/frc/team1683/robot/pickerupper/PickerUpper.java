package org.usfirst.frc.team1683.robot.pickerupper;

import org.usfirst.frc.team1683.robot.HWR;
import org.usfirst.frc.team1683.robot.drivetrain.Encoder;
import org.usfirst.frc.team1683.robot.drivetrain.HDrive;
import org.usfirst.frc.team1683.robot.drivetrain.MotorGroup;
import org.usfirst.frc.team1683.robot.main.DriverStation;
import org.usfirst.frc.team1683.robot.pneumatics.AirSystem;
import org.usfirst.frc.team1683.robot.sensors.Photogate;
import org.usfirst.frc.team1683.robot.sensors.PressureSensor;

import edu.wpi.first.wpilibj.PIDController;


public class PickerUpper{
	MotorGroup motors;
	DualActionPistons pistons;
	MotorGroup leftLiftMotor;
	MotorGroup rightLiftMotor;
	public Encoder beltEncoder;
	int liftButton;
	final double AUTO_LIFT_SPEED = 0.5;
	PressureSensor pressure;
	boolean isForward;
	Photogate photogate;
	double beltTargetPosition;
	HDrive hDrive;
	boolean enableSensor;
	Thread currentThread;
	PIDController controller;

	/**
	 * Constructor - one motor lift without encoder
	 * @param pickerUpperChannels
	 * @param motorType
	 * @param inverseDirection
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PickerUpper(int[] pickerUpperChannels, Class motorType, boolean inverseDirection){
		this.motors = new MotorGroup("Picker Upper", pickerUpperChannels, motorType, inverseDirection);
	}
	
	/**
	 * Constructor - one motor lift with encoder
	 * @param motorType
	 * @param inverseDirection
	 * @param liftSolenoids
	 * @param pickerUpperChannels
	 * @param beltChannelA
	 * @param beltChannelB
	 * @param reverseDirection
	 * @param wdpp
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PickerUpper(Class motorType, boolean inverseDirection, int[] liftSolenoids, int[] pickerUpperChannels,
			 int beltChannelA, int beltChannelB, boolean reverseDirection, double wdpp, 
			 PressureSensor pressure, Photogate photogate, HDrive hDrive){
		beltEncoder = new Encoder(beltChannelA, beltChannelB, reverseDirection, wdpp);
		this.motors = new MotorGroup("Picker Upper", pickerUpperChannels, motorType, inverseDirection, 
				beltEncoder);
		this.pressure = pressure;
		this.photogate = photogate;
		pistons = new DualActionPistons(liftSolenoids, pressure);
		this.hDrive = hDrive;
//		isForward = true;
//		pistons = new DoubleActionSolenoid(liftSolenoids, pressure);
		pistons.upright();
	}
	
	/**
	 * Constructor - one motor lift with encoder
	 * @param motorType
	 * @param inverseDirection
	 * @param liftSolenoids
	 * @param pickerUpperChannels
	 * @param beltChannelA
	 * @param beltChannelB
	 * @param reverseDirection
	 * @param wdpp
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PickerUpper(Class motorType, boolean inverseDirection, int[] liftSolenoids, int[] pickerUpperChannels,
			 int beltChannelA, int beltChannelB, boolean reverseDirection, double wdpp, 
			 PressureSensor pressure, Photogate photogate, HDrive hDrive, int index){		
		beltEncoder = new Encoder(beltChannelA, beltChannelB, reverseDirection, wdpp);
		this.motors = new MotorGroup("Picker Upper", pickerUpperChannels, motorType, inverseDirection, 
				beltEncoder);
		this.pressure = pressure;
		this.photogate = photogate;
		pistons = new DualActionPistons(liftSolenoids, pressure);
		this.hDrive = hDrive;
//		isForward = true;
//		pistons = new DoubleActionSolenoid(liftSolenoids, pressure);
		pistons.upright();
		
		enableSensor = DriverStation.getBoolean("enableSensor");	
	}
	/**
	 * Constructor - two motor lift with encoder
	 * @param motorType
	 * @param leftInverseDirection - reverseDirection for left motor
	 * @param rightInverseDirection - reverseDirection for right motor
	 * @param liftSolenoids - ports for lift pistons
	 * @param leftMotor
	 * @param rightMotor
	 * @param beltChannelA
	 * @param beltChannelB
	 * @param reverseDirection - reverseDirection for encoder
	 * @param wdpp - wheel distance per pulse for lift encoder
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PickerUpper(Class motorType, boolean leftInverseDirection, boolean rightInverseDirection,
			 int[] liftSolenoids, int leftMotor, int rightMotor,
			 int beltChannelA, int beltChannelB, boolean reverseDirection, double wdpp, Photogate photogate, PressureSensor pressure){
		this.pressure = pressure;
		beltEncoder = new Encoder(beltChannelA, beltChannelB, reverseDirection, wdpp);
		pistons = new DualActionPistons(liftSolenoids, pressure);
		leftLiftMotor = new MotorGroup("Left Lift Motor", new int[]{leftMotor}, motorType , leftInverseDirection, beltEncoder);
		rightLiftMotor = new MotorGroup("Right Lift Motor",new int[]{rightMotor}, motorType, rightInverseDirection, beltEncoder);
		this.photogate = photogate;
		pistons.upright();
		if (DriverStation.getBoolean("EnablePID")){
			enablePID();
		}
	}

	public void liftMode(int joystickNumber) {
		motors.set(DriverStation.auxStick.getRawAxis(DriverStation.YAxis));
		/*if (DriverStation.antiBounce(joystickNumber, HWR.TOGGLE_BELT_PISTON)) {
			if (isForward){
				angledPickerUpper();
			}else{
				uprightPickerUpper();
			}
		}*/
		if (DriverStation.antiBounce(joystickNumber, HWR.CALIBRATE_BELT)){
			calibrateToZero();
		}
		if (DriverStation.antiBounce(joystickNumber, HWR.GO_TO_HOME)){
			goToZero();
		}
		if (DriverStation.antiBounce(joystickNumber, HWR.LIFT_FIRST_TOTE)){
			liftFirstTote();
		}
		if (DriverStation.antiBounce(joystickNumber, HWR.LIFT_SECOND_TOTE)){
			liftSecondTote();
		}
		if (DriverStation.antiBounce(joystickNumber, 8)){
			motors.moveDistanceInches(12);
		}
		if (beltEncoder.getDistance()>HWR.MOVE_MAX)
			motors.moveDistance(HWR.MOVE_MAX-beltEncoder.getDistance());
		if (enableSensor&&photogate.get()){
			calibrateToZero();
		}
		if (DriverStation.antiBounce(joystickNumber, HWR.UPRIGHT_BELT)){
			uprightPickerUpper();
		}
		if (DriverStation.antiBounce(joystickNumber, HWR.ANGLE_BELT)){
			anglePickerUpper();
		}
		if (DriverStation.antiBounce(joystickNumber, HWR.FREEZE_BELT)){
			freezePickerUpper();
		}
	}
	
	public DualActionPistons getPistons() {
		return pistons;
	}
	
	public void enablePID(){
		double p = DriverStation.getDouble("PIDValueP");
		double i = DriverStation.getDouble("PIDValueI");
		double d = DriverStation.getDouble("PIDValueD");
		double tolerance = DriverStation.getDouble("PIDTolerance");

		motors.enablePIDController(p, i, d,tolerance, motors.getEncoder());
	}

	/**
	 * Uprights pickerUpper
	 */
	public void uprightPickerUpper() {
		pistons.upright();
	}
	
	/**
	 * Lifts the pickerupper device into the straight position
	 */
	public void uprightPickerUpperToggle() {
		if (!isForward){
			pistons.changeState();
			isForward = true;
		}
	}

	public void anglePickerUpper() {
		pistons.angle();
	}
	
	/**
	 * Brings back the pickerupper device into an angle
	 */
	public void angledPickerUpperToggle() {
		if (isForward){
			pistons.changeState();
			isForward = false;
		}
	}
	
	public void freezePickerUpper() {
		pistons.freeze();
	}
	
	public void calibrateToZero(){
		beltEncoder.reset();
	}
	
	public void goToZero(){
//		currentThread = new Thread(this);
//		currentThread.setPriority(Thread.MAX_PRIORITY);
//		currentThread.start();
		liftToHeight(0);
	}
	
	public Thread getCurrentThread(){
		return currentThread;
	}


//	/**
//	 * Goes To Position
//	 */
//	@Override
//	public void run(){
//		while(beltEncoder.getDistance() > 0) {
//			motors.set(-AUTO_LIFT_SPEED);
//		}
//		while (beltEncoder.getDistance()<0){
//			motors.set(AUTO_LIFT_SPEED);
//		}
////		while (!photogate.get()){
////			motors.set(-AUTO_LIFT_SPEED);
////		}
//	}
	
	/**
	 * 
	 * @param changeInHeight - measured in inches
	 */
	public void liftHeight(double changeInHeight)
	{
		double changeInBeltPosition = changeInHeight/HWR.SLOPE;
		motors.moveDistanceInches(changeInBeltPosition);
		currentThread = motors.getCurrentThread();
	}
	
	public void liftToHeight(double targetHeight){
		DriverStation.sendData("Target Height", targetHeight);
		double b = HWR.B1 +getHeightFromHDrive();
		beltTargetPosition = (targetHeight-b)/HWR.SLOPE;
		DriverStation.sendData("Belt Target Position", beltTargetPosition);
		double beltMove = beltTargetPosition - beltEncoder.getDisplacement(HWR.liftEncoderWDPP);
		double relativeDistanceToMove = beltMove - (beltEncoder.getDistance());
		motors.moveDistanceInches(relativeDistanceToMove);
		currentThread = motors.getCurrentThread();
		DriverStation.sendData("Belt Move", relativeDistanceToMove);
	}
	
	/*
	public void liftToHeight(double targetHeight){
		DriverStation.sendData("targetHeight", targetHeight);
		double currentHeight = getCurrentHeight();
		DriverStation.sendData("currentHeight", currentHeight);
		double changeInHeight = targetHeight-currentHeight;
		double changeInBeltPosition = changeInHeight/HWR.SLOPE;
		DriverStation.sendData("changeInBeltPosition", changeInBeltPosition);
		motors.moveDistanceInches(changeInBeltPosition);
	}
	*/
	
	public void liftFirstTote(){
		liftToHeight(HWR.SINGLE_TOTE_HEIGHT+getHeightFromHDrive());
	}
	
	public void liftSecondTote(){
		liftToHeight(HWR.DOUBLE_TOTE_HEIGHT+getHeightFromHDrive());
	}
	
	public void liftThirdTote(){
		liftToHeight(HWR.TRIPLE_TOTE_HEIGHT+getHeightFromHDrive());
	}
	
	public void liftFourthTote(){
		liftToHeight(HWR.FOURTH_TOTE_HEIGHT+getHeightFromHDrive());
	}
	
	public void liftBarrel(){
		liftToHeight(HWR.BARREL_HEIGHT+getHeightFromHDrive());
	}
	
	public void drop(){
		goToZero();
	}
	
	public void setToZero(){
		while (!photogate.get()){
			motors.set(-AUTO_LIFT_SPEED);
		}
	}
	
	public double getHeightFromHDrive(){
		if (hDrive.isDeployed())
			return HWR.H_DRIVE_HEIGHT;
		else
			return 0;
	}
	
	public double getCurrentHeight(){
		double b = getHeightFromHDrive() + HWR.ROBOT_HEIGHT + HWR.DISTANCE_TO_INDEX*HWR.SLOPE;
		return HWR.SLOPE*beltEncoder.getDisplacement(beltEncoder.getDistancePerPulse())+b;
	}
	

	public class DualActionPistons{
		AirSystem frontAirSystem;
		AirSystem backAirSystem;
		public DualActionPistons(int[] pistons, PressureSensor pressure) { //front piston, back Piston
			frontAirSystem = new AirSystem(new int[]{pistons[0]}, pressure);
			backAirSystem = new AirSystem(new int[]{pistons[1]}, pressure);
			isForward = false;
		}
		
		public AirSystem getFrontAirSystem(){
			return frontAirSystem;
		}
		public AirSystem getBackAirSystem(){
			return backAirSystem;
		}
		
		public void changeState(){
			if (frontAirSystem.isExtended()){
				frontAirSystem.retract();
				backAirSystem.extend();
			}else{
				frontAirSystem.extend();
				backAirSystem.retract();
			}
		}
		
		public void upright(){
			frontAirSystem.retract();
			backAirSystem.retract();
		}
		
		public void angle(){
			frontAirSystem.extend();
			backAirSystem.extend();
		}
		
		public void freeze(){
			frontAirSystem.extend();
			backAirSystem.retract();
		}
	}
	
	
}
