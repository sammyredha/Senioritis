package frc.robot.subsystems.Pivot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.PivotConstants;
import frc.robot.Robot;
import frc.robot.utilities.PIDFFController;
import frc.robot.utilities.SuperstructureConfig;
import org.littletonrobotics.junction.Logger;

public class PivotSubsystem extends SubsystemBase {

  private PivotIO io;
  private PivotInputsAutoLogged inputs;
  private final PIDFFController controller;
  private double targetAngle = 0;

  public PivotSubsystem(PivotIO pivotIO) {
    io = pivotIO;
    controller = new PIDFFController(PivotConstants.GAINS);
    inputs = new PivotInputsAutoLogged();
    io.updateInputs(inputs);
  }

  public boolean isAtTarget() {
    return Math.abs(targetAngle - inputs.absoluteEncoderAngle) < 1;
  }

  public void setTargetAngle(double newAngle) {
    if (newAngle < PivotConstants.MIN_ANGLE || newAngle > PivotConstants.MAX_ANGLE) {
      return;
    }
    targetAngle = newAngle;
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    double output = controller.calculate(inputs.absoluteEncoderAngle, targetAngle);
    io.setVoltage(output);

    output = MathUtil.clamp(output, -12, 12);
    Logger.getInstance().recordOutput("Pivot/Target Angle", targetAngle);
    Logger.getInstance().recordOutput("Pivot/Output", output);

    Logger.getInstance().processInputs("Pivot", inputs);
  }

  public static class Commands {
    public static Command setPosition(double angle) {
      return new InstantCommand(() -> Robot.pivot.setTargetAngle(angle));
    }

    public static Command setPosition(SuperstructureConfig config) {
      return setPosition(config.getPivotPosition());
    }
  }
}
