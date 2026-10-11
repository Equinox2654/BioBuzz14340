package org.firstinspires.ftc.teamcode.BioBuzz;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.pedropathing.follower.Follower;
import com.pedropathing.api.PoseFactory;
import static com.pedropathing.api.Paths.*;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import com.pedropathing.ivy.Scheduler;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous
public class AutoPrimary extends OpMode {
    private Follower follower;
    private final PoseFactory p = PoseFactory.degrees();
    private final Pose startPose = p.of(0, 48, 0);
    private final Pose shootPose = p.of(0, 50, 0);
    private final Pose parkBezierControlPose = p.of(69, 13, 0);
    private final Pose parkPose = p.of(91, 2, 0);

    @Override
    public void init() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
        follower.update();
    }

    @Override
    public void start() {
        schedule(autoRoutine());
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
    }

    private Path pathShoot() {
        final Pose currentPose = follower.pose();
        return line(currentPose, shootPose).linear(currentPose, shootPose);
    }
    private Path pathPark() {
        final Pose currentPose = follower.pose();
        return through(currentPose, parkBezierControlPose, parkPose).facingPoint(parkPose);
    }

    private Command autoRoutine() {
        return sequential(
                follow(follower, pathShoot()),
                follow(follower, pathPark())
        );
    }
}
