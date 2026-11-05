package org.firstinspires.ftc.teamcode.BioBuzz;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

@TeleOp
public class ArmTest extends OpMode {
    private DcMotor arm;
    private FlowerArmPiDController pid;

    // Tunables
    private double kP = 0.003, kI = 0.0, kD = 0.0;
    protected static final double MAX_POWER = 0.6;     // cap while testing
    protected static final int TOLERANCE = 10;         // ticks, stop inside this
    protected static final int MIN_TARGET = 0;         // soft limits (ticks)
    protected static final int MAX_TARGET = 7665;       // set from your measurements

    private double target = 0;

    @Override
    public void init() {
        arm = hardwareMap.get(DcMotor.class, "arm");
        arm.setDirection(DcMotorSimple.Direction.REVERSE);
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pid = new FlowerArmPiDController(kP, kI, kD);
    }

    boolean t = true;
    @Override
    public void init_loop() {
        if (gamepad1.a) t = false;
    }

    @Override
    public void loop() {
        if (t) tloop();
        else {
            arm.setPower(-gamepad1.left_stick_y * 0.3);
            telemetry.addData("pos", arm.getCurrentPosition());
            telemetry.update();
        }
    }

    public void tloop() {
        // Presets
        if (gamepad1.a) target = MIN_TARGET;
        if (gamepad1.b) target = MAX_TARGET / 2.0;
        if (gamepad1.y) target = MAX_TARGET;

        // Live kP tuning (edge-detected so it changes once per press)
        if (gamepad1.dpadUpWasPressed())     kP += 0.0005;
        if (gamepad1.dpadDownWasPressed()) kP = Math.max(0, kP - 0.0005);
        pid.setPID(0.01, 0, 0);

        target = Range.clip(target, MIN_TARGET, MAX_TARGET);

        double pos = arm.getCurrentPosition();
        double error = target - pos;

        // Always call calculate so dt stays small, then decide whether to use it
        double power = pid.calculate(target, pos);

        if (Math.abs(error) < TOLERANCE) {
            arm.setPower(0);   // worm gear holds the arm
        } else {
            arm.setPower(Range.clip(power, -MAX_POWER, MAX_POWER));
        }

        telemetry.addData("target", target);
        telemetry.addData("pos", pos);
        telemetry.addData("error", error);
        telemetry.addData("power", power);
        telemetry.addData("kP", kP);
        telemetry.update();
    }
}
