/* This is the master code for the robot. At this time it is not modulized
 * Add comments for each thing that you add. Try and explain what it does and where it gets
 * the parameters which it uses.
 */

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
//import edu.wpi.first.wpilibj.drive.RobotDriveBase.MotorType;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
 
 
 public class Robot extends TimedRobot {
   // Drive Train Motors
   // Each motor in the drive train is declared
   private final CANSparkMax frontLeft = new CANSparkMax(0, MotorType.kBrushless); //CAN ID is set on the sparkmax 
   private final CANSparkMax backLeft = new CANSparkMax(1, MotorType.kBrushless); // Motortype must also be set for the neo it is brushless.
   private final CANSparkMax frontRight = new CANSparkMax(2, MotorType.kBrushless);
   private final CANSparkMax backRight = new CANSparkMax(3, MotorType.kBrushless);
   // Two motor control groups are declared to control the left and right side of the robot as one group.
   private final MotorControllerGroup leftDrive = new MotorControllerGroup(frontLeft, backLeft);
   private final MotorControllerGroup rightDrive = new MotorControllerGroup(frontRight, backRight);
   // the Differential Drive is declared and named msClarke
   private final DifferentialDrive msClarke = new DifferentialDrive(leftDrive, rightDrive); //the two motor control groups are added to the differential drive as parameters
   // Arm/Claw Motors
   private final CANSparkMax leftIntake = new CANSparkMax(4, MotorType.kBrushless);
   private final CANSparkMax rightIntake = new CANSparkMax(5, MotorType.kBrushless);
   //private final DifferentialDrive clawIntake = new DifferentialDrive(leftIntake, rightIntake);
   private final CANSparkMax teleScope = new CANSparkMax(6, MotorType.kBrushless);
   private final CANSparkMax tiltArm = new CANSparkMax(7, MotorType.kBrushless);
   
   //Solenoids
   private final DoubleSolenoid m_doubleSolenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 1, 2);
   private final DoubleSolenoid secondSolenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 3, 4);
   //private static final int kSolenoidButton = 1;
   //private static final int kDoubleSolenoidForward = 2; //the button which operates the solenoid
   //private static final int kDoubleSolenoidReverse = 3;
   // Compressor Varibales [RO] still working on figuring this out.
   Compressor pcmCompressor = new Compressor(0, PneumaticsModuleType.CTREPCM);
   //Compressor phCompressor = new Compressor(1, PneumaticsModuleType.REVPH);
   //pcmCompressor.enableDigital();
   //pcmCompressor.disable();
   //boolean enabled = pcmCompressor.IsEnabled();
   //boolean pressureSwitch = pcmCompressor.getPressureSwitchValue();
   //double current = pcmCompressor.getCompressorCurrent();
   
   // Other variables
   private final XboxController driver = new XboxController(0); //Xbox controller is usually detected in port 0
   private final XboxController armController = new XboxController(1); 
   private final Timer m_timer = new Timer(); // a timer is needed for autonomous mode
 
   //This function is run when the robot is first started up and should be used for any initialization code.
   @Override
   public void robotInit() {
     //rightDrive.setInverted(true); //On side of the robot's drivetrain must be inverted so that the motors can turn in same relative direction.
     //rightIntake.setInverted(true);
     leftIntake.follow(rightIntake, true);
     m_doubleSolenoid.set(DoubleSolenoid.Value.kForward);
     secondSolenoid.set(DoubleSolenoid.Value.kForward);
   }
 
   //This function is run once each time the robot enters autonomous mode.
   @Override
   public void autonomousInit() {
     m_timer.reset();
     m_timer.start();
     leftDrive.setInverted(true);
   }
 
   //This function is called periodically (20ms) during autonomous.
   @Override
   public void autonomousPeriodic() {
     // Drive for 2 seconds
     if ((m_timer.get() >0 ) && (m_timer.get()<2)) {
       // Drive forwards half speed, make sure to turn input squaring off
       //msClarke.arcadeDrive(0.5, 0.0, isAutonomous());
       //msClarke.arcadeDrive(0.0, 0.5, isAutonomous());
       tiltArm.set(0.5); //smaller
     }
     if ((m_timer.get() >2 ) && (m_timer.get()<4)) {
      tiltArm.stopMotor();
       teleScope.set(0.5);
     }
     if ((m_timer.get() >4 ) && (m_timer.get()<6)) {
      teleScope.stopMotor();
       //m_doubleSolenoid.set(DoubleSolenoid.Value.kReverse); //only for cones (just in case)
       //secondSolenoid.set(DoubleSolenoid.Value.kReverse);
      
       rightIntake.set(0.5);
     }
     if ((m_timer.get() >6 ) && (m_timer.get()<8)) {
      rightIntake.stopMotor();
      teleScope.setInverted(true);
       teleScope.set(0.5);
     }
     if ((m_timer.get() >8 ) && (m_timer.get()<10)) {
      teleScope.stopMotor();
       tiltArm.set(0.5); //bigger
     }
     if ((m_timer.get() >10 ) && (m_timer.get()<12)) {
      tiltArm.stopMotor();
      teleScope.setInverted(false);
        teleScope.set(0.5);
     }
     if ((m_timer.get() >12 ) && (m_timer.get()<14)) {
      teleScope.stopMotor();
       //m_doubleSolenoid.set(DoubleSolenoid.Value.kForward); //for cones again*
       //secondSolenoid.set(DoubleSolenoid.Value.kForward);
     }
      teleScope.setInverted(true);
       teleScope.set(0.5);

       if (m_timer.get() >14) {
        teleScope.stopMotor();
       }
     }
    
   
 
   //This function is called once each time the robot enters teleoperated mode.
   @Override
   public void teleopInit() {
    pcmCompressor.enableDigital();
    rightDrive.setInverted(true);
    leftDrive.setInverted(false);
    teleScope.setInverted(false);
   }
 
   //This function is called periodically during teleoperated mode.
   @Override
   public void teleopPeriodic() {
     //Drive Train controls
     msClarke.arcadeDrive(-driver.getLeftY(), -driver.getRightX());
     // Arm Controls
     teleScope.set(armController.getLeftY());

     tiltArm.set(armController.getRightY());
     // Claw Controls
    // clawIntake.arcadeDrive(m_controller.getLeftTriggerAxis(), 0.0);
    if (armController.getBButtonPressed()) {
        rightIntake.set(0.5);
    }
 
     // Extend Pneumatic Cylinder
     //if (armController.getYButtonPressed()) { // use the Y button to toggle the claw open or closed.
     //  m_doubleSolenoid.toggle();
     //}
     
     if (armController.getRightBumperPressed()) { // use the A button to extend the cylinder
       m_doubleSolenoid.set(DoubleSolenoid.Value.kForward);
       secondSolenoid.set(DoubleSolenoid.Value.kForward);
     } else if (armController.getLeftBumperPressed()) { // use the B button to retract the cylinder
       m_doubleSolenoid.set(DoubleSolenoid.Value.kReverse);
       secondSolenoid.set(DoubleSolenoid.Value.kReverse);
   }
 }
 
   //This function is called once each time the robot enters test mode.
   @Override
   public void testInit() {}
 
   //This function is called periodically during test mode.
   @Override
   public void testPeriodic() {}
 }
 
 
 
 /* CHANGE LOG enter the date and changes made each time you edit this code.
  * 
  */