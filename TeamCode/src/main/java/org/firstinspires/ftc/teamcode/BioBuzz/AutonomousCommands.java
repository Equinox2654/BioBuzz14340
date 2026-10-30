package org.firstinspires.ftc.teamcode.BioBuzz;

import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.paths.Path;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;

public class AutonomousCommands {
    protected static Command routine(Follower follower, Command shoot, java.util.function.Function<Path, Command> collect, java.util.function.Function<Void, Boolean> isHiveUp) {
        return sequential(
                // Shoot at start
                shootPath(follower),
                shoot,
                // Collect from garden
                collect.apply(AutonomousPaths.collectGarden()),
                fork(isHiveUp.apply(null), follower, shoot, collect)
        );
    }

    private static Command shootPath(Follower follower) {
        return follow(follower, AutonomousPaths.shootPath(follower));
    }

    private static Command fork(boolean isHiveUp, Follower follower, Command shoot, java.util.function.Function<Path, Command> collect) {
        return isHiveUp ? sequential(
                // Shoot other side
                follow(follower, AutonomousPaths.shootOpposite1()),
                shoot,
                // Collect from opposite flower
                collect.apply(AutonomousPaths.collectOppFlower()),
                // Shoot other side again
                follow(follower, AutonomousPaths.shootOpposite2()),
                shoot,
                // Collect from close flower
                collect.apply(AutonomousPaths.collectCloseFlower()),
                // Shoot from start pose
                follow(follower, AutonomousPaths.shootFinal()),
                shoot,
                // Park (ends at shootPose)
                follow(follower, AutonomousPaths.Park(AutonomousPaths.shootPose))
        ) :
                sequential(
                        // Go to other side to shoot
                        follow(follower, AutonomousPaths.altOppShootToCloseShoot()),
                        shoot,
                        // Collect from close flower
                        collect.apply(AutonomousPaths.altCloseShootToFlower()),
                        // Shoot again
                        follow(follower, AutonomousPaths.altFinalShoot()),
                        shoot,
                        // Park (ends at altFinalEnd)
                        follow(follower, AutonomousPaths.Park(AutonomousPaths.altFinalEnd))
                );
    }
}