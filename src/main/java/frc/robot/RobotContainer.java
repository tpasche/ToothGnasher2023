package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.Constants.OIConstants;
import frc.robot.commands.Arm.Intake.ArmIntakeInCmd;
import frc.robot.commands.Arm.Intake.ArmIntakeOutCmd;
import frc.robot.commands.Arm.Manipulator.ArmManipulatorDriveCmd;
import frc.robot.commands.Arm.Manipulator.ArmManipulatorHumanCmd;
import frc.robot.commands.Arm.Manipulator.ArmManipulatorIntakeCmd;
import frc.robot.commands.Arm.Manipulator.ArmManipulatorPlaceCmd;
import frc.robot.commands.Arm.Slider.ArmSliderBottomCmd;
import frc.robot.commands.Arm.Slider.ArmSliderHumanPlayerCmd;
import frc.robot.commands.Arm.Slider.ArmSliderLowCmd;
import frc.robot.commands.Arm.Slider.ArmSliderTopCmd;
import frc.robot.commands.Arm.Wrist.ArmWristCmd;
import frc.robot.commands.Auto.AutoDriveCmd;
import frc.robot.commands.Auto.AutoWaitCmd;
import frc.robot.commands.Auto.Intake.AutoIntakeOutCmd;
import frc.robot.commands.Auto.Movement.AutoChargingBalanceCmd;
import frc.robot.commands.Auto.Movement.Trajectories;
import frc.robot.commands.Drive.Allign.DriveAllignPoleCmd;
import frc.robot.commands.Drive.Gyro.Gyro180Cmd;
import frc.robot.commands.Drive.Gyro.GyroResetCmd;
import frc.robot.commands.Drive.SwerveJoystickCmd;
import frc.robot.commands.Drive.Allign.DriveAllignBoxCmd;
import frc.robot.subsystems.Primary.SwerveSubsystem;
import frc.robot.subsystems.Secondary.ArmSubsystem;
import frc.robot.subsystems.Secondary.RotateSubsystem;

public class RobotContainer {

        private final Command autoMiddle = new SequentialCommandGroup(
                new InstantCommand(
                                () -> swerveSubsystem.resetOdometry(Trajectories.getTraj1().getInitialPose())),
                Commands.parallel(new ArmSliderTopCmd(armSubsystem), new ArmManipulatorPlaceCmd(rotateSubsystem)),
                Commands.race(new AutoIntakeOutCmd(armSubsystem), new AutoWaitCmd(300)),
                Commands.parallel(new ArmSliderBottomCmd(armSubsystem),
                new ArmManipulatorDriveCmd(rotateSubsystem)),
                Trajectories.traj3(), // move to charge station
                Trajectories.traj1(), // move then stop
                new AutoWaitCmd(500), // stop
                Trajectories.traj2(), // go on the drive station
                Commands.race(new AutoDriveCmd(swerveSubsystem, 0.2), new AutoWaitCmd(10)),
                // new AutoChargingBalanceCmd(swerveSubsystem),
                new Gyro180Cmd(swerveSubsystem),
                new InstantCommand(() -> swerveSubsystem.stopModules()));

        private final Command autoSide = new SequentialCommandGroup(
                new InstantCommand(
                                () -> swerveSubsystem.resetOdometry(Trajectories.getTraj4().getInitialPose())),
                // new Gyro180Cmd(swerveSubsystem),
                Commands.parallel(new ArmSliderTopCmd(armSubsystem),
                new ArmManipulatorPlaceCmd(rotateSubsystem)),
                Commands.race(new AutoIntakeOutCmd(armSubsystem), new AutoWaitCmd(300)),
                Commands.parallel(new ArmSliderBottomCmd(armSubsystem),
                new ArmManipulatorDriveCmd(rotateSubsystem)),
                Trajectories.traj4(),
                new Gyro180Cmd(swerveSubsystem),
                new InstantCommand(() -> swerveSubsystem.stopModules()));

        private final Command autoPlace = new SequentialCommandGroup(
                // new Gyro180Cmd(swerveSubsystem),
                new InstantCommand(() -> swerveSubsystem.resetOdometry(Trajectories.getTraj1().getInitialPose())),
                Commands.parallel(new ArmSliderTopCmd(armSubsystem), new ArmManipulatorPlaceCmd(rotateSubsystem)),
                Commands.race(new AutoIntakeOutCmd(armSubsystem), new AutoWaitCmd(300)),
                Commands.parallel(new ArmSliderBottomCmd(armSubsystem),
                new ArmManipulatorDriveCmd(rotateSubsystem)),
                new Gyro180Cmd(swerveSubsystem),
                new InstantCommand(() -> swerveSubsystem.stopModules()));

        // A chooser for autonomous commands
        SendableChooser<Command> m_chooser = new SendableChooser<>();

        public final static SwerveSubsystem swerveSubsystem = new SwerveSubsystem();
        public final static ArmSubsystem armSubsystem = new ArmSubsystem();
        public final static RotateSubsystem rotateSubsystem = new RotateSubsystem();

        public final XboxController driverJoytick = new XboxController(OIConstants.kDriverControllerPort);
        public final static XboxController secondaryJoystick = new XboxController(
                        OIConstants.kSecondaryDriverControllerPort);

        public RobotContainer() {

                m_chooser.setDefaultOption("Just place", autoPlace);
                m_chooser.addOption("Side (place + go out of community)", autoSide);
                m_chooser.addOption("Middle (place + charge station)", autoMiddle);
                SmartDashboard.putData("Auto choices", m_chooser);

                // Put the chooser on the dashboard
                Shuffleboard.getTab("Autonomous").add(m_chooser);

                swerveSubsystem.setDefaultCommand(new SwerveJoystickCmd(
                                swerveSubsystem,
                                () -> -driverJoytick.getRawAxis(OIConstants.kDriverYAxis),
                                () -> -driverJoytick.getRawAxis(OIConstants.kDriverXAxis),
                                () -> -driverJoytick.getRawAxis(OIConstants.kDriverRotAxis),
                                () -> driverJoytick.getRawAxis(2),
                                () -> driverJoytick.getRawButton(5),
                                () -> driverJoytick.getRawButton(6),
                                () -> driverJoytick.getRawButton(1)));

                configureButtonBindings();
        }

        private void configureButtonBindings() {

                // Secondary

                new JoystickButton(secondaryJoystick, 1).onTrue(Commands.parallel(new ArmSliderBottomCmd(armSubsystem),
                                new ArmManipulatorDriveCmd(rotateSubsystem)));
                new JoystickButton(secondaryJoystick, 2).onTrue(Commands.parallel(new ArmSliderLowCmd(armSubsystem),
                                new ArmManipulatorPlaceCmd(rotateSubsystem)));
                new JoystickButton(secondaryJoystick, 3)
                                .onTrue(Commands.parallel(new ArmSliderHumanPlayerCmd(armSubsystem),
                                                new ArmManipulatorHumanCmd(rotateSubsystem)));
                new JoystickButton(secondaryJoystick, 4).onTrue(Commands.parallel(new ArmSliderTopCmd(armSubsystem),
                                new ArmManipulatorPlaceCmd(rotateSubsystem)));

                new JoystickButton(secondaryJoystick, 8).onTrue(new ArmManipulatorIntakeCmd(rotateSubsystem));

                new JoystickButton(secondaryJoystick, 5).onTrue(
                                new ArmIntakeInCmd(armSubsystem, () -> secondaryJoystick.getRawButton(5)));
                new JoystickButton(secondaryJoystick, 6).onTrue(
                                new ArmIntakeOutCmd(armSubsystem, () -> secondaryJoystick.getRawButton(6)));

                new JoystickButton(secondaryJoystick, 7).onTrue(new ArmWristCmd(armSubsystem));

                // Primary

                new JoystickButton(driverJoytick, 3)
                                .onTrue(new DriveAllignPoleCmd(swerveSubsystem, () -> driverJoytick.getRawButton(3)));

                new JoystickButton(driverJoytick, 2)
                                .onTrue(new DriveAllignBoxCmd(swerveSubsystem, () -> driverJoytick.getRawButton(2)));

                new JoystickButton(driverJoytick, 4).onTrue(new GyroResetCmd(swerveSubsystem));

        }

        public Command getAutonomousCommand() {
                return m_chooser.getSelected();
        }
}