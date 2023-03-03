/* This is the master code for the robot. At this time it is not modulized
 * Add comments for each thing that you add. Try and explain what it does and where it gets
 * the parameters which it uses.
 */

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

//import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;


public class Robot extends TimedRobot {
  // Drive Train Motors
  // Each motor in the drive train is declared
  private final CANSparkMax frontLeft = new CANSparkMax(0); //CAN ID is set on the sparkmax 
  private final CANSparkMax backLeft = new CANSparkMax(1); // Motortype must also be set for the neo it is brushless.
  private final CANSparkMax frontRight = new CANSparkMax(2);
  private final CANSparkMax backRight = new CANSparkMax(3);
  // Two motor control groups are declared to control the left and right side of the robot as one group.
  private final MotorControllerGroup leftDrive = new MotorControllerGroup(frontLeft, backLeft);
  private final MotorControllerGroup rightDrive = new MotorControllerGroup(frontRight, backRight);
  // the Differential Drive is declared and named msClarke
  private final DifferentialDrive msClarke = new DifferentialDrive(leftDrive, rightDrive); //the two motor control groups are added to the differential drive as parameters
  // Arm/Claw Motors

  //Solenoids
  private final DoubleSolenoid m_doubleSolenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 1, 2);
  private static final int kSolenoidButton = 1;
  private static final int kDoubleSolenoidForward = 2;
  private static final int kDoubleSolenoidReverse = 3;
  // Other varibables
  private final XboxController m_controller = new XboxController(0); //Xbox controller is usually detected in port 0
  private final Timer m_timer = new Timer(); // a timer is needed for autonomous mode

  //This function is run when the robot is first started up and should be used for any initialization code.
  @Override
  public void robotInit() {
    rightDrive.setInverted(true); //On side of the robot's drivetrain must be inverted so that the motors can turn in same relative direction.
  }

  //This function is run once each time the robot enters autonomous mode.
  @Override
  public void autonomousInit() {
    m_timer.restart();
  }

  //This function is called periodically (20ms) during autonomous.
  @Override
  public void autonomousPeriodic() {
    // Drive for 2 seconds
    if (m_timer.get() < 2.0) {
      // Drive forwards half speed, make sure to turn input squaring off
      msClarke.arcadeDrive(0.5, 0.0, isAutonomous());
    } else {
      msClarke.stopMotor(); // stop robot
    }
  }

  //This function is called once each time the robot enters teleoperated mode.
  @Override
  public void teleopInit() {}

  //This function is called periodically during teleoperated mode.
  @Override
  public void teleopPeriodic() {
    //Drive Train controls
    msClarke.arcadeDrive(-m_controller.getLeftY(), -m_controller.getRightX());
    // Arm Controls

    // Claw Controls

    // Extend Pnumatic Cylinder
    if (m_controller.getRawButton(kDoubleSolenoidForward)) {
      m_doubleSolenoid.set(DoubleSolenoid.Value.kForward);
    } else if (m_controller.getRawButton(kDoubleSolenoidReverse)) {
      m_doubleSolenoid.set(DoubleSolenoid.Value.kReverse);
  }
}

  //This function is called once each time the robot enters test mode.
  @Override
  public void testInit() {}

  //his function is called periodically during test mode.
  @Override
  public void testPeriodic() {}
}



/* CHANGE LOG enter the date and changes made each time you edit this code.
 * 
 */