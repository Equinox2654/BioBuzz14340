package org.firstinspires.ftc.teamcode.BioBuzz;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import static java.lang.Math.abs;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp(name="TeleOp 2026", group="TeleOp Main")
public class TeleOpBioBuzz extends OpMode {

    private Follower follower;
    private final PoseFactory p = PoseFactory.degrees();
    private final Pose startPose = p.of(4, 87, 90);
    private final ElapsedTime runTime = new ElapsedTime();
    private DcMotor pollenShooter, nectarShooter;
    private DcMotor intake;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);

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

    private boolean aimbotting = false;
    private boolean lastY = false;
    private boolean lastB = false;
    @Override
    public void loop() {
        telemetry.addData("Runtime", runTime.toString());

        if ((abs(gamepad1.left_stick_x) > 0.1 || abs(gamepad1.left_stick_y) > 0.1 || abs(gamepad1.right_stick_x) > 0.1) && aimbotting) {
            follower.stop();
            Scheduler.reset();
            aimbotting = false;
        }

        if (gamepad1.b && !lastB && !aimbotting) {
            aimbotting = true;
            aimbot();
        }
        lastB = gamepad1.b;

        if (!aimbotting) {
            follower.manual(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x,
                    -gamepad1.right_stick_x
            );
            follower.update();
        }
        else {
            follower.update();
            Scheduler.execute();
            if (!follower.isBusy()) {
                aimbotting = false;
            }
        }

        if (gamepad1.y && !lastY) shoot();
        lastY = gamepad1.y;
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

    private void aimbot() {
        Pose pose = follower.pose();
        Pose shootpose;
        if (pose.y() <= 70) {
            shootpose = p.of(59, 0, 270);
        }
        else {
            shootpose = p.of(59, 132, 90);
        }
        schedule(
                follow(follower, line(pose, shootpose).constant(shootpose))
        );
    }
}
