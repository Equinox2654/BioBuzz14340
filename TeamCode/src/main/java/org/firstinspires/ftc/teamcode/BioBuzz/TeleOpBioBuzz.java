package org.firstinspires.ftc.teamcode.BioBuzz;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp(name="TeleOp 2026", group="TeleOp Main")
public class TeleOpBioBuzz extends OpMode {

    private Follower follower;

    private final ElapsedTime runTime = new ElapsedTime();
    private DcMotor pollenShooter, nectarShooter;
    private DcMotor intake;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);

        pollenShooter  = hardwareMap.get(DcMotor.class, "PollenShooter");
        nectarShooter = hardwareMap.get(DcMotor.class, "NectarShooter");
        pollenShooter.setDirection(DcMotor.Direction.FORWARD);
        nectarShooter.setDirection(DcMotor.Direction.FORWARD);

        intake = hardwareMap.get(DcMotor.class, "Intake");
        intake.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialised");
        telemetry.update();
    }

    @Override
    public void start() { runTime.reset(); }

    @Override
    public void loop() {
        telemetry.addData("Runtime", runTime.toString());

        follower.manual(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                -gamepad1.right_stick_x
        );
        follower.update();

        if (gamepad1.y) shoot();
        intake();

        telemetry.update();
    }

    private void shoot() {
        if (pollenShooter.getPower() > 0) {
            pollenShooter.setPower(0);
            nectarShooter.setPower(0);
        }
        else {
            pollenShooter.setPower(0.8);
            nectarShooter.setPower(0.8);
        }
    }

    private void intake() {
        if (gamepad1.a) intake.setPower(1);
        else intake.setPower(0);
    }
}
