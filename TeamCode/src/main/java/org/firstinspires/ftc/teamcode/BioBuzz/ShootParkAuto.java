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
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous
public class ShootParkAuto extends OpMode {
    private Follower follower;
    private final PoseFactory p = PoseFactory.degrees();
    private final Pose startPose = p.of(48, 0, 270);
    private final Pose shootPose = p.of(50, 1, 270);
    private final Pose parkBezierControlPose = p.of(6, 63, 90);
    private final Pose parkPose = p.of(4, 87, 90);

    private final ElapsedTime elapsedTime = new ElapsedTime();

    private DcMotor shooter;

    @Override
    public void init() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
        follower.update();

        shooter = hardwareMap.get(DcMotor.class, "PollenShooter");
        shooter.setDirection(DcMotor.Direction.FORWARD);
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
        return path(line(currentPose, parkBezierControlPose).linear(currentPose, currentPose), line(parkBezierControlPose, parkPose).linear(parkBezierControlPose, parkPose));
    }

    private Command autoRoutine() {
        return sequential(
                follow(follower, pathShoot()),
                shoot(),
                follow(follower, pathPark())
        );
    }

    private Command shoot() {
        return Command.build()
                .setStart(() -> {
                    shooter.setPower(0.8);
                    elapsedTime.reset();
                })
                .setDone(() -> {return elapsedTime.seconds() >= 2;})
                .setEnd(endCondition -> {shooter.setPower(0);})
                ;
    }
}
