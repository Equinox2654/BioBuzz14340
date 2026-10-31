package org.firstinspires.ftc.teamcode.BioBuzz;

import static com.pedropathing.ivy.commands.Commands.lazy;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.paths.Path;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;

public class AutonomousCommands {
    protected static Command routine(Follower follower, Command shoot, java.util.function.Function<Path, Command> collect, java.util.function.Function<Void, Boolean> isHiveUp) {
        return sequential(
                // Shoot at start
                follow(follower, AutonomousPaths.shootPath(follower)),
                shoot,
                // Collect from garden
                collect.apply(AutonomousPaths.collectGarden()),
                // Go to other side
                follow(follower, AutonomousPaths.shootOpposite1()),
                lazy(() -> { return fork(isHiveUp, follower, shoot, collect); })
        );
    }

    private static Command fork(java.util.function.Function<Void, Boolean> isHiveUp, Follower follower, Command shoot, java.util.function.Function<Path, Command> collect) {
        return isHiveUp.apply(null) ? sequential(
                // Shoot other side
                shoot,
                // Collect from opposite flower
                collect.apply(AutonomousPaths.collectOppFlower()),
                // Shoot other side again
                follow(follower, AutonomousPaths.shootOpposite2()),
                lazy(() -> {return fork2(isHiveUp.apply(null), follower, shoot, collect);})
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
                        // Park
                        follow(follower, AutonomousPaths.Park(AutonomousPaths.altFinalEnd))
                );
    }

    private static Command fork2(boolean isHiveUp, Follower follower, Command shoot, java.util.function.Function<Path, Command> collect) {
        return isHiveUp ? sequential(
                shoot,
                // Collect from close flower
                collect.apply(AutonomousPaths.collectCloseFlower()),
                // Shoot from start pose
                follow(follower, AutonomousPaths.shootFinal()),
                shoot,
                // Park
                follow(follower, AutonomousPaths.Park(AutonomousPaths.shootPose))
        ) : sequential(
                // Go to close side
                follow(follower, AutonomousPaths.altOppShootToCloseShoot()),
                shoot,
                // Collect from close flower
                collect.apply(AutonomousPaths.altCloseShootToFlower()),
                // Shoot again
                follow(follower, AutonomousPaths.altFinalShoot()),
                shoot,
                // Park
                follow(follower, AutonomousPaths.Park(AutonomousPaths.altFinalEnd))
        );
    }
}