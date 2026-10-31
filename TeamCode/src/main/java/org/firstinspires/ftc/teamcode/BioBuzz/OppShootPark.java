package org.firstinspires.ftc.teamcode.BioBuzz;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous
public class OppShootPark extends OpMode {

    private Follower follower;
    private final PoseFactory p = PoseFactory.degrees();
    private final Pose startPose = p.of(9, 132, 0);
    private final Pose parkPose = p.of(4, 87, 90);
    private Limelight3A limelight;
    private DcMotor shooter;
    private Servo pollenServo;
    private final ElapsedTime elapsedTime = new ElapsedTime();

    @Override
    public void init() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
        follower.update();

        shooter = hardwareMap.get(DcMotor.class, "PollenShooter");

        pollenServo = hardwareMap.get(Servo.class, "PollenServo");

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        telemetry.setMsTransmissionInterval(11);
        limelight.pipelineSwitch(0);
        limelight.start();
    }

    @Override
    public void start() {
        schedule(
                waitForHive(),
                shoot(),
                follow(follower, line(startPose, parkPose).linear(startPose, parkPose))
        );
    }

    private Command waitForHive() {
        return Command.build()
                .setDone(() -> {
                    LLResult result = limelight.getLatestResult();
                    if (result != null && result.isValid())
                        return !result.getFiducialResults().isEmpty();
                    return false;
                })
                ;
    }

    private Command shoot() {
        return Command.build()
                .setStart(() -> {
                    shooter.setPower(0.8);
                    elapsedTime.reset();
                })
                .setExecute(() -> {
                    if (elapsedTime.seconds() > 1)
                        pollenServo.setPosition(0);
                })
                .setDone(() -> {return elapsedTime.seconds() >= 2;})
                .setEnd(endCondition -> {
                    shooter.setPower(0);
                    pollenServo.setPosition(0.2);
                })
                ;
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
    }
}
