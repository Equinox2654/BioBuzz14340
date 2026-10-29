package org.firstinspires.ftc.teamcode.BioBuzz;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.deadline;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous
public class MainAuto extends OpMode {

    private Follower follower;
    private final PoseFactory p = PoseFactory.degrees();
    private final Pose startPose = p.of(56, 0, 270);
    private final Pose shootPose = p.of(56, 0, 270);
    private final Pose oppShootPose = p.of(59.55, 132.85, 90);
    private final ElapsedTime elapsedTime = new ElapsedTime();
    private final ElapsedTime gameTime = new ElapsedTime();
    private DcMotor shooter, intake;

    @Override
    public void init() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
        follower.update();

        shooter = hardwareMap.get(DcMotor.class, "PollenShooter");
        shooter.setDirection(DcMotor.Direction.FORWARD);

        intake = hardwareMap.get(DcMotor.class, "Intake");
        intake.setDirection(DcMotor.Direction.FORWARD);
    }

    @Override
    public void start() {
        schedule(autoRoutine());
        gameTime.reset();
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
    }

    private Command autoRoutine() {
        return sequential(
                  deadline(
                      timeCondition(),
                      sequential(
                           follow(follower, shootPath()),
                           shoot(),
                           collect(collectGarden()),
                           follow(follower, shootOpposite1()),
                           shoot(),
                           collect(collectOppFlower()),
                           follow(follower, shootOpposite2()),
                           shoot(),
                           collect(collectCloseFlower()),
                           follow(follower, shootFinal()),
                           shoot(),
                           follow(follower, Park())
                      )
                ),
                follow(follower, Park())
        );
    }

    private Command timeCondition() {
        return Command.build()
                .setDone(() -> {return gameTime.seconds() >= 27;});
    }

    private Path shootPath() {
        Pose currentPose = follower.pose();
        return line(currentPose, shootPose).linear(currentPose, shootPose);
    }

    private Path collectGarden() {
        Pose currentPose = follower.pose();
        Pose cp1 = p.of(46, 36, 180);
        Pose cp2 = p.of(34.36, 4.28, 180);
        Pose endP = p.of(9.31, 9.26, 180);
        return curve(currentPose, cp1, cp2, endP).linear(currentPose, endP);
    }

    private Path shootOpposite1() {
        Pose currentPose = follower.pose();
        Pose cp1 = p.of(15.16, 79.99, 90);
        Pose cp2 = p.of(34.82, 116.8, 90);
        Pose cp3 = p.of(69.67, 113.79, 90);
        return curve(currentPose, cp1, cp2, cp3, oppShootPose).linear(currentPose, oppShootPose);
    }

    private Path collectOppFlower() {
        Pose currentPose = follower.pose();
        Pose cp1 = p.of(57.4, 121.02, 90);
        Pose endP = p.of(47.52, 129.06, 90);
        return curve(currentPose, cp1, endP).linear(currentPose, endP);
    }

    private Path shootOpposite2() {
        Pose currentPose = follower.pose();
        Pose cp1 = p.of(57.58, 121.06, 90);
        return curve(currentPose, cp1, oppShootPose).linear(currentPose, oppShootPose);
    }

    private Path collectCloseFlower() {
        Pose currentPose = follower.pose();
        Pose cp1 = p.of(63.97, 112.64, 180);
        Pose cp2 = p.of(32.41, 108.18, 180);
        Pose cp3 = p.of(14.39, 89.34, 180);
        Pose cp4 = p.of(29.35, 51.6, 180);
        Pose endP = p.of(13.02, 46.47, 180);
        return curve(currentPose, cp1, cp2, cp3, cp4, endP).linear(currentPose, endP);
    }

    private Path shootFinal() {
        Pose currentPose = follower.pose();
        return line(currentPose, shootPose).linear(currentPose, shootPose);
    }

    private Path Park() {
        Pose currentPose = follower.pose();
        Pose cp1 = p.of(13.87, 33.03, 90);
        Pose endP = p.of(4, 87, 90);
        return curve(currentPose, cp1, endP).linear(currentPose, endP);
    }

    private Command shoot() {
        return Command.build()
                .setStart(() -> {
                    shooter.setPower(0.8);
                    elapsedTime.reset();
                })
                .setDone(() -> elapsedTime.seconds() >= 2)
                .setEnd(endCondition -> shooter.setPower(0))
                ;
    }

    private Command collectCommand() {
        return Command.build()
                .setStart(() -> intake.setPower(1))
                .setEnd(endCondition -> intake.setPower(0))
                ;
    }

    private Command collect(Path path) {
        return parallel(
                follow(follower, path),
                collectCommand()
        );
    }
}
