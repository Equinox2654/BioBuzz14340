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

    // End poses of paths, used as the start of the next path
    protected static final Pose gardenEnd = p.of(9.31, 9.26, 180);
    protected static final Pose oppFlowerEnd = p.of(47.52, 129.06, 90);
    protected static final Pose closeFlowerEnd = p.of(13.02, 46.47, 180);
    protected static final Pose altFlowerEnd = p.of(12.74, 46.8, 180);
    protected static final Pose altFinalEnd = p.of(56.38, 8.92, 270);

    // First path: starts from wherever the robot actually is
    protected static Path shootPath(Follower follower) {
        Pose currentPose = follower.pose();
        return line(currentPose, shootPose).linear(currentPose, shootPose);
    }

    protected static Path collectGarden() {
        Pose cp1 = p.of(46, 36, 180);
        Pose cp2 = p.of(34.36, 4.28, 180);
        return curve(shootPose, cp1, cp2, gardenEnd).linear(shootPose, gardenEnd);
    }

    protected static Path shootOpposite1() {
        Pose cp1 = p.of(15.16, 79.99, 90);
        Pose cp2 = p.of(34.82, 116.8, 90);
        Pose cp3 = p.of(69.67, 113.79, 90);
        return curve(gardenEnd, cp1, cp2, cp3, oppShootPose).linear(gardenEnd, oppShootPose);
    }

    protected static Path collectOppFlower() {
        Pose cp1 = p.of(57.4, 121.02, 90);
        return curve(oppShootPose, cp1, oppFlowerEnd).linear(oppShootPose, oppFlowerEnd);
    }

    protected static Path shootOpposite2() {
        Pose cp1 = p.of(57.58, 121.06, 90);
        return curve(oppFlowerEnd, cp1, oppShootPose).linear(oppFlowerEnd, oppShootPose);
    }

    protected static Path collectCloseFlower() {
        Pose cp1 = p.of(63.97, 112.64, 180);
        Pose cp2 = p.of(32.41, 108.18, 180);
        Pose cp3 = p.of(14.39, 89.34, 180);
        Pose cp4 = p.of(29.35, 51.6, 180);
        return curve(oppShootPose, cp1, cp2, cp3, cp4, closeFlowerEnd).linear(oppShootPose, closeFlowerEnd);
    }

    protected static Path shootFinal() {
        return line(closeFlowerEnd, shootPose).linear(closeFlowerEnd, shootPose);
    }

    // 'from' is where the robot is when parking starts
    protected static Path Park(Pose from) {
        Pose cp1 = p.of(13.87, 33.03, 90);
        Pose endP = p.of(4, 87, 90);
        return curve(from, cp1, endP).linear(from, endP);
    }

    protected static Path altOppShootToCloseShoot() {
        return line(gardenEnd, shootPose).linear(gardenEnd, shootPose);
    }

    protected static Path altCloseShootToFlower() {
        Pose cp1 = p.of(60.8, 46.03, 180);
        return curve(shootPose, cp1, altFlowerEnd).tangent();
    }

    protected static Path altFinalShoot() {
        return line(altFlowerEnd, altFinalEnd).linear(altFlowerEnd, altFinalEnd);
    }
}