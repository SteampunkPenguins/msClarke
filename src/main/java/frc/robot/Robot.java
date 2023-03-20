/* This is the master code for the robot. At this time it is not modulized
 * Add comments for each thing that you add. Try and explain what it does and where it gets
 * the parameters which it uses.
 */

package frc.robot;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import java.security.Key;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
//import edu.wpi.first.wpilibj.drive.RobotDriveBase.MotorType;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.cameraserver.CameraServer;
 
 
 public class Robot extends TimedRobot {
   // Drive Train Motors
   // Each motor in the drive train is declared
   private final CANSparkMax frontLeft = new CANSparkMax(8, MotorType.kBrushless); //CAN ID is set on the sparkmax 
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
   private final DifferentialDrive clawIntake = new DifferentialDrive(leftIntake, rightIntake);
   private final CANSparkMax teleScope = new CANSparkMax(6, MotorType.kBrushless);
   private final CANSparkMax tiltArm = new CANSparkMax(7, MotorType.kBrushless);

   private final RelativeEncoder fl_encoder = frontLeft.getEncoder();
   private final RelativeEncoder bl_encoder = backLeft.getEncoder();
   private final RelativeEncoder fr_encoder = frontRight.getEncoder();
   private final RelativeEncoder br_encoder = backRight.getEncoder();
   private final RelativeEncoder leftIntake_encoder = leftIntake.getEncoder();
   private final RelativeEncoder rightIntake_encoder = rightIntake.getEncoder();
   private final RelativeEncoder teleScope_encoder = teleScope.getEncoder();
   private final RelativeEncoder tiltArm_encoder = tiltArm.getEncoder();

//switch inputs
  private final DigitalInput cubeJet = new DigitalInput(1);
  private final DigitalInput coneJet = new DigitalInput(2);
  private final DigitalInput cubeCharge = new DigitalInput(3);
  private final DigitalInput coneCharge = new DigitalInput(4);
  
  //Safety features
  private final boolean tiltSafety = true
  private final boolean teleScopeSafety = true
  

   //Navx
   private final AHRS navX = new AHRS(SerialPort.Port.kMXP);

   //Solenoids
   private final DoubleSolenoid m_doubleSolenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 1, 2);
   //private final DoubleSolenoid secondSolenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 3, 4);
   //private static final int kSolenoidButton = 1;
   //private static final int kDoubleSolenoidForward = 2; //the button which operates the solenoid
   //private static final int kDoubleSolenoidReverse = 3;


   // Compressor Varibales [RO] still working on figuring this out.
   Compressor compressor = new Compressor(0, PneumaticsModuleType.CTREPCM);
   //Compressor phCompressor = new Compressor(1, PneumaticsModuleType.REVPH);
   
   boolean enabled = compressor.isEnabled();
   boolean pressureSwitch = compressor.getPressureSwitchValue();
   double current = compressor.getPressure();
   
   //Shuffleboard Variables
   private static final String kDefaultAuto = "Default";
   private static final String kCustomAuto = "My Auto";
   private String m_autoSelected;
   private final SendableChooser<String> m_chooser = new SendableChooser<>();

   // Other variables
   private final XboxController driver = new XboxController(0); //Xbox controller is usually detected in port 0
   private final XboxController armController = new XboxController(1); 
   private final Timer m_timer = new Timer(); // a timer is needed for autonomous mode
 
   //Autonomous
   //private final Integer bellerika = 1;
 
   //This function is run when the robot is first started up and should be used for any initialization code.
   @Override
   public void robotInit() {
     //rightDrive.setInverted(true); //On side of the robot's drivetrain must be inverted so that the motors can turn in same relative direction.
    rightIntake.setInverted(true);
     //rightIntake.follow(leftIntake, true);
     m_doubleSolenoid.set(DoubleSolenoid.Value.kForward);
     //secondSolenoid.set(DoubleSolenoid.Value.kForward);

     m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
     m_chooser.addOption("My Auto", kCustomAuto);
     SmartDashboard.putData("Auto choices", m_chooser);
    
     //Allows the NavX to be readable by the dashboard
     navX.enableLogging(true);
 
     //Enables camera server on dashboard
     CameraServer.startAutomaticCapture(0);

     //turn on compressor
    compressor.enableDigital();
    //compressor.disable();

    //fl_encoder.setDistancePerPulse();
    //bl_encoder.setDistancePerPulse(1/256);
    //fr_encoder.setDistancePerPulse(1/256);
    //br_encoder.setDistancePerPulse(1/256);
    //leftIntake_encoder.setDistancePerPulse(1/256);
    //rightIntake_encoder.setDistancePerPulse(1/256);
    //teleScope_encoder.setDistancePerPulse(1/256);
    //tiltArm_encoder.setDistancePerPulse(1/256);

   }
 
   @Override
   public void robotPeriodic() {

     //Encoder dashboard readouts
     SmartDashboard.putNumber("frontLeft Position", fl_encoder.getPosition());
     SmartDashboard.putNumber("frontRight Velocity", fr_encoder.getVelocity());
     SmartDashboard.putNumber("backLeft Velocity", bl_encoder.getVelocity());
     SmartDashboard.putNumber("backRight Velocity", br_encoder.getVelocity());
     SmartDashboard.putNumber("leftIntake Velocity", leftIntake_encoder.getVelocity());
     SmartDashboard.putNumber("rightIntake Velocity", rightIntake_encoder.getVelocity());
     SmartDashboard.putNumber("teleScope Velocity", teleScope_encoder.getVelocity());
     SmartDashboard.putNumber("tiltArm Velocity", tiltArm_encoder.getVelocity());
     SmartDashboard.putNumber("Yaw", navX.getYaw());
     SmartDashboard.putNumber("pitch", navX.getPitch());
     SmartDashboard.putNumber("roll", navX.getRoll());
     SmartDashboard.putBoolean("cubeJet", cubeJet.get());
     SmartDashboard.putBoolean("coneJet", coneJet.get());
     SmartDashboard.putBoolean("cubeCharge", cubeCharge.get());
     SmartDashboard.putBoolean("coneCharge", coneCharge.get());
     SmartDashboard.putNumber("Pressure", compressor.getPressure());
     SmartDashboard.putBoolean("Compressor ON?" compressor.isEnabled());
     
    if (compressor.getPressure() < 120){
      compressor.enableDigital();

    }
  }


   //This function is run once each time the robot enters autonomous mode.
   @Override
   public void autonomousInit() {
     m_timer.reset();
     m_timer.start();
     leftDrive.setInverted(true);

     m_autoSelected = m_chooser.getSelected();
     // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
     //System.out.println("Auto selected: " + m_autoSelected);
   }
 
   //This function is called periodically (20ms) during autonomous.
   @Override
   public void autonomousPeriodic() {
    boolean balance = false;
    if ((navX.getRoll()) <= Math.abs(20) && (balance == false)) {
      msClarke.arcadeDrive(0.5, 0.0, isAutonomous());
    }
        else if (navX.getRoll() >= -10) {
        balance = true;
        msClarke.stopMotor();
        leftDrive.setInverted(true);
        msClarke.arcadeDrive(0.1, 0.0, isAutonomous());
        }
          else if (navX.getRoll() <= 10) {
          msClarke.stopMotor();
          leftDrive.setInverted(false);
          msClarke.arcadeDrive(0.1, 0.0, isAutonomous());
          }
           else {
           msClarke.stopMotor();
          }
   
          
          
    if (!(cubeJet.get())); {
     // Drive for 2 seconds
     // cube and jet
     if ((m_timer.get() >0 ) && (m_timer.get()<2)) {
       tiltArm.set(0.5); //bigger
     }
     if ((m_timer.get() >2 ) && (m_timer.get()<4)) {
      tiltArm.stopMotor();
      teleScope.setInverted(false);
        teleScope.set(0.5);
     }
     if ((m_timer.get() >4 ) && (m_timer.get()<6)) {
      teleScope.stopMotor();
       //m_doubleSolenoid.set(DoubleSolenoid.Value.kForward); //for cones again*
       //secondSolenoid.set(DoubleSolenoid.Value.kForward);
     }
      teleScope.setInverted(true);
       teleScope.set(0.5);

       if (m_timer.get() >6) {
        teleScope.stopMotor();
       }

       if (m_timer.get() >8) {
     msClarke.arcadeDrive (0.5, 0.0, isAutonomous());
     }
    }
   
    if (!(coneJet.get())); {
     // Drive for 2 seconds
     // cone and jet
     if ((m_timer.get() >0 ) && (m_timer.get()<2)) {
       // Drive forwards half speed, make sure to turn input squaring off
       //msClarke.arcadeDrive(0.5, 0.0, isAutonomous());
       //msClarke.arcadeDrive(0.0, 0.5, isAutonomous());
       tiltArm.set(0.5); //bigger
     }
     if ((m_timer.get() >2 ) && (m_timer.get()<4)) {
      tiltArm.stopMotor();
      teleScope.setInverted(false);
        teleScope.set(0.5);
     }
     if ((m_timer.get() >4 ) && (m_timer.get()<6)) {
      teleScope.stopMotor();
      m_doubleSolenoid.set(DoubleSolenoid.Value.kForward); //for cones again*
      //secondSolenoid.set(DoubleSolenoid.Value.kForward);
     }
     if ((m_timer.get() >6 ) && (m_timer.get()<8)) {
      m_doubleSolenoid.close();
      //secondSolenoid.close();
      teleScope.setInverted(true);
       teleScope.set(0.5);

       if (m_timer.get() >8) {
        teleScope.stopMotor();
       }
       if (m_timer.get() >8) {
        msClarke.arcadeDrive (0.5, 0.0, isAutonomous());
     }
    }
    
    if (!(cubeCharge.get())); {
     // Drive for 2 seconds
     // cube and charge station
     if ((m_timer.get() >0 ) && (m_timer.get()<2)) {
       // Drive forwards half speed, make sure to turn input squaring off
       //msClarke.arcadeDrive(0.5, 0.0, isAutonomous());
       //msClarke.arcadeDrive(0.0, 0.5, isAutonomous());
       tiltArm.set(0.5); //bigger
     }
     if ((m_timer.get() >2 ) && (m_timer.get()<4)) {
      tiltArm.stopMotor();
      teleScope.setInverted(false);
        teleScope.set(0.5);
     }
     if ((m_timer.get() >4 ) && (m_timer.get()<6)) {
      teleScope.stopMotor();
       //m_doubleSolenoid.set(DoubleSolenoid.Value.kForward); //for cones again*
       //secondSolenoid.set(DoubleSolenoid.Value.kForward);
     }
      teleScope.setInverted(true);
       teleScope.set(0.5);

       if (m_timer.get() >6) {
        teleScope.stopMotor();
       }
       if (m_timer.get() >8) {
        msClarke.arcadeDrive (0.5, 0.0, isAutonomous());
     //insert navX
     }
     
     if (!(coneCharge.get())); {
     // Drive for 2 seconds
     // cone and charge station
     if ((m_timer.get() >0 ) && (m_timer.get()<2)) {
       // Drive forwards half speed, make sure to turn input squaring off
       //msClarke.arcadeDrive(0.5, 0.0, isAutonomous());
       //msClarke.arcadeDrive(0.0, 0.5, isAutonomous());
       tiltArm.set(0.5); //bigger
     }
     if ((m_timer.get() >2 ) && (m_timer.get()<4)) {
      tiltArm.stopMotor();
      teleScope.setInverted(false);
        teleScope.set(0.5);
     }
     if ((m_timer.get() >4 ) && (m_timer.get()<6)) {
      teleScope.stopMotor();
      m_doubleSolenoid.set(DoubleSolenoid.Value.kForward); //for cones again*
      //secondSolenoid.set(DoubleSolenoid.Value.kForward);
     }
     if ((m_timer.get() >6 ) && (m_timer.get()<8)) {
      m_doubleSolenoid.close();
      //secondSolenoid.close();
      teleScope.setInverted(true);
       teleScope.set(0.5);

       if (m_timer.get() >8) {
        teleScope.stopMotor();
      }
    }
      }
     }
    }
  }
  

   
    
   
 
   //This function is called once each time the robot enters teleoperated mode.
   @Override
   public void teleopInit() {
    compressor.enableDigital();
    rightDrive.setInverted(true);
    leftDrive.setInverted(false);
    teleScope.setInverted(false);
   }
 
   //This function is called periodically during teleoperated mode.
   @Override
   public void teleopPeriodic() {
     //Drive Train controls
     //msClarke.arcadeDrive(-driver.getLeftY(), -driver.getRightX());
     // Arm Controls
    //conditionals to check if the safteys are not false.
    
     teleScope.set(armController.getLeftY());
    // we must allow the arm to still move in one direction even after the safetys are tripped.
    
     tiltArm.set((armController.getRightY()/2));
    
     // Claw Controls
    //clawIntake.arcadeDrive(armController.getLeftTriggerAxis(), 0.0);
    //clawIntake.arcadeDrive((armController.getRightTriggerAxis()*-1), 0.0);
    if (armController.getBButton()) {
        rightIntake.setInverted(true);
        leftIntake.setInverted(false);
        leftIntake.set(0.5);
        rightIntake.set(0.5);
    }
    else if (armController.getBButtonReleased()){
      leftIntake.stopMotor();
      rightIntake.stopMotor();
    }
    if (armController.getYButton()) {
      rightIntake.setInverted(false);
      leftIntake.setInverted(true);
      leftIntake.set(0.5);
      rightIntake.set(0.5);
  }
  else if (armController.getYButtonReleased()){
    leftIntake.stopMotor();
    rightIntake.stopMotor();
  }
 
     // Extend Pneumatic Cylinder
     //if (armController.getYButtonPressed()) { // use the Y button to toggle the claw open or closed.
     //  m_doubleSolenoid.toggle();
     //}
     
     if (armController.getRightBumperPressed()) { // use the right bumper to extend the cylinder
       //m_doubleSolenoid.set(DoubleSolenoid.Value.kForward);
        m_doubleSolenoid.toggle(); 
       //secondSolenoid.set(DoubleSolenoid.Value.kForward);
     }
     if (armController.getLeftBumperPressed()) { // use the left bumper to retract the cylinder
      // m_doubleSolenoid.set(DoubleSolenoid.Value.kReverse);
        m_doubleSolenoid.toggle();
       //secondSolenoid.set(DoubleSolenoid.Value.kReverse);
   }
   

 }

 @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}
 
   //This function is called once each time the robot enters test mode.
   @Override
   public void testInit() {}
 
   //This function is called periodically during test mode.
   @Override
   public void testPeriodic() {}
 
 @Override
 public void simulationInit() {}

 /** This function is called periodically whilst in simulation. */
 @Override
 public void simulationPeriodic() {}
}
 
 
 
 /* CHANGE LOG enter the date and changes made each time you edit this code.
  * 
  */
