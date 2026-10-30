package org.firstinspires.ftc.teamcode.BioBuzz;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

public class AutonomousPaths {
    protected static final PoseFactory p = PoseFactory.degrees();
    protected static final Pose shootPose = p.of(56, 0, 270);
    protected static final Pose oppShootPose = p.of(59.55, 132.85, 90);

    protected static Path shootPath(Follower follower) {
        Pose currentPose = follower.pose();
        return line(currentPose, shootPose).linear(currentPose, shootPose);
    }

    protected static Path collectGarden(Follower follower) {
        Pose currentPose = follower.pose();
        Pose cp1 = p.of(46, 36, 180);
        Pose cp2 = p.of(34.36, 4.28, 180);
        Pose endP = p.of(9.31, 9.26, 180);
        return curve(currentPose, cp1, cp2, endP).linear(currentPose, endP);
    }

    protected static Path shootOpposite1(Follower follower) {
        Pose currentPose = follower.pose();
        Pose cp1 = p.of(15.16, 79.99, 90);
        Pose cp2 = p.of(34.82, 116.8, 90);
        Pose cp3 = p.of(69.67, 113.79, 90);
        return curve(currentPose, cp1, cp2, cp3, oppShootPose).linear(currentPose, oppShootPose);
    }

    protected static Path collectOppFlower(Follower follower) {
        Pose currentPose = follower.pose();
        Pose cp1 = p.of(57.4, 121.02, 90);
        Pose endP = p.of(47.52, 129.06, 90);
        return curve(currentPose, cp1, endP).linear(currentPose, endP);
    }

    protected static Path shootOpposite2(Follower follower) {
        Pose currentPose = follower.pose();
        Pose cp1 = p.of(57.58, 121.06, 90);
        return curve(currentPose, cp1, oppShootPose).linear(currentPose, oppShootPose);
    }

    protected static Path collectCloseFlower(Follower follower) {
        Pose currentPose = follower.pose();
        Pose cp1 = p.of(63.97, 112.64, 180);
        Pose cp2 = p.of(32.41, 108.18, 180);
        Pose cp3 = p.of(14.39, 89.34, 180);
        Pose cp4 = p.of(29.35, 51.6, 180);
        Pose endP = p.of(13.02, 46.47, 180);
        return curve(currentPose, cp1, cp2, cp3, cp4, endP).linear(currentPose, endP);
    }

    protected static Path shootFinal(Follower follower) {
        Pose currentPose = follower.pose();
        return line(currentPose, shootPose).linear(currentPose, shootPose);
    }

    protected static Path Park(Follower follower) {
        Pose currentPose = follower.pose();
        Pose cp1 = p.of(13.87, 33.03, 90);
        Pose endP = p.of(4, 87, 90);
        return curve(currentPose, cp1, endP).linear(currentPose, endP);
    }
}
