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

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);

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

        telemetry.update();
    }
}
