// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.Constants.ArmConstants;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSink;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.cscore.MjpegServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoMode.PixelFormat;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    private Command m_autonomousCommand;

    private RobotContainer m_robotContainer;

    UsbCamera camera1;
    double targetPos = 150;

    /**
     * This function is run when the robot is first started up and should be used
     * for any
     * initialization code.
     */
    @Override
    public void robotInit() {
        // Instantiate our RobotContainer. This will perform all our button bindings,
        // and put our
        // autonomous chooser on the dashboard.

        // CameraServer.startAutomaticCapture();
        // CvSink cvSink = CameraServer.getVideo();
        // // Creates UsbCamera and MjpegServer [1] and connects them
        // CameraServer.startAutomaticCapture();

        // // Creates the CvSink and connects it to the UsbCamera
        // CvSink cvSink = CameraServer.getVideo();

        // // Creates the CvSource and MjpegServer [2] and connects them
        // CvSource outputStream = CameraServer.putVideo("Blur", 640, 480);

        // Creates UsbCamera and MjpegServer [1] and connects them

        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(1);

        // UsbCamera usbCamera = new UsbCamera("USB Camera 0", 0);
        // MjpegServer mjpegServer1 = new MjpegServer("serve_USB Camera 0", 1181);
        // mjpegServer1.setSource(usbCamera);

        // // Creates the CvSink and connects it to the UsbCamera
        // CvSink cvSink = new CvSink("opencv_USB Camera 0");
        // cvSink.setSource(usbCamera);

        // // Creates the CvSource and MjpegServer [2] and connects them
        // CvSource outputStream = new CvSource("Blur", PixelFormat.kMJPEG, 640, 480, 30);
        // MjpegServer mjpegServer2 = new MjpegServer("serve_Blur", 1182);
        // mjpegServer2.setSource(outputStream);

        // camera1.setResolution(160, 120);

        m_robotContainer = new RobotContainer();

    }

    /**
     * This function is called every robot packet, no matter the mode. Use this for
     * items like
     * diagnostics that you want ran during disabled, autonomous, teleoperated and
     * test.
     *
     * <p>
     * This runs after the mode specific periodic functions, but before LiveWindow
     * and
     * SmartDashboard integrated updating.
     */
    @Override
    public void robotPeriodic() {
        // Runs the Scheduler. This is responsible for polling buttons, adding
        // newly-scheduled
        // commands, running already-scheduled commands, removing finished or
        // interrupted commands,
        // and running subsystem periodic() methods. This must be called from the
        // robot's periodic
        // block in order for anything in the Command-based framework to work.
        CommandScheduler.getInstance().run();
    }

    /** This function is called once each time the robot enters Disabled mode. */
    @Override
    public void disabledInit() {
    }

    @Override
    public void disabledPeriodic() {
    }

    /**
     * This autonomous runs the autonomous command selected by your
     * {@link RobotContainer} class.
     */
    @Override
    public void autonomousInit() {
        m_autonomousCommand = m_robotContainer.getAutonomousCommand();

        // schedule the autonomous command (example)
        if (m_autonomousCommand != null) {
            m_autonomousCommand.schedule();
        }
    }

    /** This function is called periodically during autonomous. */
    @Override
    public void autonomousPeriodic() {
        if (RobotContainer.armSubsystem.sliderEncoder.getPosition() < -42) {
            RobotContainer.armSubsystem.leftArmSlider.set(0);
            RobotContainer.armSubsystem.rightArmSlider.set(0);
        }
    }

    @Override
    public void teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (m_autonomousCommand != null) {
            m_autonomousCommand.cancel();
        }
        // RobotContainer.armSubsystem.wristRotateEncoder.setPosition(0);
        RobotContainer.armSubsystem.sliderEncoder.setPosition(0);

        // RobotContainer.swerveSubsystem.setHeading(180);

        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(1);

    }

    /** This function is called periodically during operator control. */
    @Override
    public void teleopPeriodic() {

        System.out.println("Yaw" + RobotContainer.swerveSubsystem.getYaw());

        SmartDashboard.putNumber("Yaw", RobotContainer.swerveSubsystem.getYaw());

        // Manipulator w/ restrictions
        if (ArmConstants.manipulatorManual == false
                && RobotContainer.secondaryJoystick.getRawAxis(ArmConstants.rightYPort) < -0.25) {
            ArmConstants.manipulatorManual = true;
        }
        if (ArmConstants.manipulatorManual == false
                && RobotContainer.secondaryJoystick.getRawAxis(ArmConstants.rightYPort) > 0.25) {
            ArmConstants.manipulatorManual = true;
        }
        if (ArmConstants.manipulatorManual) {
            if (ArmConstants.manipulatorOn == false) {
                RobotContainer.rotateSubsystem.armRotateMotor.set(0);
            }

            if (RobotContainer.secondaryJoystick.getRawAxis(ArmConstants.rightYPort) < -0.25
                    && RobotContainer.rotateSubsystem.armRotateEncoder.getPosition() > ArmConstants.restriction1
                    && ArmConstants.manipulatorOn == false) {
                RobotContainer.rotateSubsystem.armRotateMotor
                        .set(RobotContainer.secondaryJoystick.getRawAxis(ArmConstants.rightYPort) * 0.4);
                // targetPos = RobotContainer.armSubsystem.armRotateEncoder.getPosition();
            }

            if (RobotContainer.secondaryJoystick.getRawAxis(ArmConstants.rightYPort) > 0.25
                    && RobotContainer.rotateSubsystem.armRotateEncoder.getPosition() < ArmConstants.restriction2
                    && ArmConstants.manipulatorOn == false) {
                RobotContainer.rotateSubsystem.armRotateMotor
                        .set(RobotContainer.secondaryJoystick.getRawAxis(ArmConstants.rightYPort) * 0.4);
                // targetPos = RobotContainer.armSubsystem.armRotateEncoder.getPosition();
            }
        }

        // Slider w/ stops
        if (RobotContainer.armSubsystem.sliderEncoder.getPosition() < -42
                && RobotContainer.secondaryJoystick.getRawAxis(1) > 0.25) {
            RobotContainer.armSubsystem.leftArmSlider
                    .set(RobotContainer.secondaryJoystick.getRawAxis(1) * -Constants.ArmConstants.gSliderSpeed);
            RobotContainer.armSubsystem.rightArmSlider
                    .set(RobotContainer.secondaryJoystick.getRawAxis(1) * Constants.ArmConstants.gSliderSpeed);
        } else if (RobotContainer.armSubsystem.sliderEncoder.getPosition() > -0.5
                && RobotContainer.secondaryJoystick.getRawAxis(1) < -0.25) {
            RobotContainer.armSubsystem.leftArmSlider
                    .set(RobotContainer.secondaryJoystick.getRawAxis(1) * -Constants.ArmConstants.gSliderSpeed);
            RobotContainer.armSubsystem.rightArmSlider
                    .set(RobotContainer.secondaryJoystick.getRawAxis(1) * Constants.ArmConstants.gSliderSpeed);
        } else if (RobotContainer.armSubsystem.sliderEncoder.getPosition() < -42
                || RobotContainer.armSubsystem.sliderEncoder.getPosition() > -0.5) {
            RobotContainer.armSubsystem.leftArmSlider.set(0);
            RobotContainer.armSubsystem.rightArmSlider.set(0);
        } else {
            RobotContainer.armSubsystem.leftArmSlider
                    .set(RobotContainer.secondaryJoystick.getRawAxis(1) * -Constants.ArmConstants.gSliderSpeed);
            RobotContainer.armSubsystem.rightArmSlider
                    .set(RobotContainer.secondaryJoystick.getRawAxis(1) * Constants.ArmConstants.gSliderSpeed);
        }
    }

    @Override
    public void testInit() {
        // Cancels all running commands at the start of test mode.
        CommandScheduler.getInstance().cancelAll();
    }

    /** This function is called periodically during test mode. */
    @Override
    public void testPeriodic() {
    }
}