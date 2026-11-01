package org.firstinspires.ftc.teamcode.BioBuzz;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import static java.lang.Math.abs;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp(name="TeleOp 2026", group="TeleOp Main")
public class TeleOpBioBuzz extends OpMode {

    private Follower follower;
    private final PoseFactory p = PoseFactory.degrees();
    private final Pose startPose = p.of(4, 87, 90);
    private final ElapsedTime runTime = new ElapsedTime();
    private DcMotor Fl, Bl, Fr, Br;
    private double FlP, BlP, FrP, BrP;
    private DcMotor pollenShooter, nectarShooter;
    private DcMotor intake;
    private Servo pollenServo;
    //private Servo nectarServo;
    private Limelight3A limelight;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);

        Fl = hardwareMap.get(DcMotor.class, "FL");
        Bl = hardwareMap.get(DcMotor.class, "BL");
        Fr = hardwareMap.get(DcMotor.class, "FR");
        Br = hardwareMap.get(DcMotor.class, "BR");
        Fl.setDirection(DcMotor.Direction.REVERSE);
        Bl.setDirection(DcMotor.Direction.REVERSE);
        Fr.setDirection(DcMotor.Direction.FORWARD);
        Br.setDirection(DcMotor.Direction.FORWARD);
        Fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pollenShooter  = hardwareMap.get(DcMotor.class, "PollenShooter");
        nectarShooter = hardwareMap.get(DcMotor.class, "NectarShooter");
        pollenShooter.setDirection(DcMotor.Direction.FORWARD);
        nectarShooter.setDirection(DcMotor.Direction.REVERSE);

        intake = hardwareMap.get(DcMotor.class, "Intake");
        intake.setDirection(DcMotor.Direction.FORWARD);

        pollenServo = hardwareMap.get(Servo.class, "PollenShooter");

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        telemetry.setMsTransmissionInterval(11);
        limelight.pipelineSwitch(0);
        limelight.start();

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        telemetry.addData("Status", "Initialised");
        telemetry.update();
    }

    @Override
    public void start() {
        runTime.reset();
        pollenServo.setPosition((double) 10/180);
    }

    @Override
    public void loop() {
        telemetry.addData("Runtime", runTime.toString());

        double max;
        double axial = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double yaw = gamepad1.right_stick_x;

        FlP = axial + lateral + yaw;
        BlP = axial - lateral + yaw;
        FrP = axial - lateral - yaw;
        BrP = axial + lateral - yaw;

        max = Math.max(Math.abs(FlP), Math.max(Math.abs(BlP), Math.max(Math.abs(FrP), Math.abs(BrP))));
        if (max > 1.0) {
            FlP /= max;
            BlP /= max;
            FrP /= max;
            BrP /= max;
        }

        Fl.setPower(FlP);
        Bl.setPower(BlP);
        Fr.setPower(FrP);
        Br.setPower(BrP);

        if (gamepad1.yWasPressed()) shoot();
        intake();

        telemetry.update();
    }

    private void shoot() {
        if (pollenShooter.getPower() > 0) {
            pollenShooter.setPower(0);
            nectarShooter.setPower(0);
        }
        else {
            pollenShooter.setPower(0.7);
            nectarShooter.setPower(0.8);
        }
    }

    private void intake() {
        if (gamepad1.a) intake.setPower(1);
        else intake.setPower(0);
    }
}
