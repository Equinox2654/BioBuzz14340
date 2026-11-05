package org.firstinspires.ftc.teamcode.BioBuzz;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.lazy;
import static com.pedropathing.ivy.groups.Groups.race;
import static com.pedropathing.ivy.groups.Groups.sequential;
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
    private final Pose closeShootPose = p.of(50, 0, 270);
    private Limelight3A limelight;
    private DcMotor shooter;
    private Servo pollenServo;
    private final ElapsedTime elapsedTime = new ElapsedTime();
    private final ElapsedTime gameTime = new ElapsedTime();

    @Override
    public void init() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
        follower.update();

        shooter = hardwareMap.get(DcMotor.class, "PollenShooter");

        pollenServo = hardwareMap.get(Servo.class, "PollenServo");
        pollenServo.setPosition(0.8);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        telemetry.setMsTransmissionInterval(11);
        limelight.pipelineSwitch(0);
        limelight.start();
    }

    @Override
    public void start() {
        pollenServo.setPosition(0);
        schedule(
                sequential(
                    race(
                        timeCondition(),
                        sequential(
                            waitForHive(),
                            shoot()
                        )
                    ),
                    follow(follower, line(startPose, closeShootPose).linear(startPose, closeShootPose)),
                    lazy(() -> {
                            return follow(follower, line(follower.pose(), parkPose).linear(follower.pose(), parkPose));
                        }
                    )
                )
        );
    }

    private Command timeCondition() {
        return Command.build()
                .setStart(gameTime::reset)
                .setDone(() -> gameTime.seconds() > 10)
                ;
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
                    shooter.setPower(0.75);
                    elapsedTime.reset();
                })
                .setExecute(() -> {
                    if (elapsedTime.seconds() > 3)
                        pollenServo.setPosition(1);
                })
                .setDone(() -> {return elapsedTime.seconds() >= 5;})
                .setEnd(endCondition -> {
                    shooter.setPower(0);
                    pollenServo.setPosition(0.8);
                })
                ;
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
    }
}
