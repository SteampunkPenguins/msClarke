// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//Encoders are part of the REV Robotics vendor library
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

//NavX library and serial communication protocol. A Timer is used for debouncing.
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;

import edu.wpi.first.wpilibj.Timer;

//RoboRIO camera library
import edu.wpi.first.cameraserver.CameraServer;
 

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

//Each individual encoder is associated with a specific CANSparkMax
  private final CANSparkMax frontLeft = new CANSparkMax(0, MotorType.kBrushless);
  private final RelativeEncoder m_encoder = frontLeft.getEncoder();

  //NavX
  private final AHRS navX = new AHRS(SerialPort.Port.kMXP);
  

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
   
    //Allows the NavX to be readable by the dashboard
    navX.enableLogging(true);

    //Enables camera server on dashboard
    CameraServer.startAutomaticCapture(0);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {



    while (isTeleopEnabled()) {
            
      //Encoder dashboard readouts
      SmartDashboard.putNumber("frontLeft Position", m_encoder.getPosition());
      SmartDashboard.putNumber("frontLeft Velocity", m_encoder.getVelocity());


      //NavX dashboard readouts
      Timer.delay(0.020);		/* wait for one motor update time period (50Hz)     */
      
      boolean zero_yaw_pressed = false; //stick.getTrigger();
      if ( zero_yaw_pressed ) {
          navX.zeroYaw();
      }

      /* Display 6-axis Processed Angle Data                                      */
      SmartDashboard.putBoolean(  "IMU_Connected",        navX.isConnected());
      SmartDashboard.putNumber(   "IMU_Yaw",              navX.getYaw());
      SmartDashboard.putNumber(   "IMU_Pitch",            navX.getPitch());
      SmartDashboard.putNumber(   "IMU_Roll",             navX.getRoll());
 

      /* These functions are compatible w/the WPI Gyro Class, providing a simple  */
      /* path for upgrading from the Kit-of-Parts gyro to the navx MXP            */
      
      SmartDashboard.putNumber(   "IMU_TotalYaw",         navX.getAngle());
      SmartDashboard.putNumber(   "IMU_YawRateDPS",       navX.getRate());
      
      /* Quaternion Data                                                          */
      /* Quaternions are fascinating, and are the most compact representation of  */
      /* orientation data.  All of the Yaw, Pitch and Roll Values can be derived  */
      /* from the Quaternions.  If interested in motion processing, knowledge of  */
      /* Quaternions is highly recommended.                                       */
      SmartDashboard.putNumber(   "QuaternionW",          navX.getQuaternionW());
      SmartDashboard.putNumber(   "QuaternionX",          navX.getQuaternionX());
      SmartDashboard.putNumber(   "QuaternionY",          navX.getQuaternionY());
      SmartDashboard.putNumber(   "QuaternionZ",          navX.getQuaternionZ());
  }

  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
