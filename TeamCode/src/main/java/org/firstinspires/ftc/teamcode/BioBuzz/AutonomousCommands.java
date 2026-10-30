package org.firstinspires.ftc.teamcode.BioBuzz;

import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.paths.Path;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;

public class AutonomousCommands {
    protected static Command routine(Follower follower, Command shoot, java.util.function.Function<Path, Command> collect) {
        return sequential(
                // Shoot at start
                shootPath(follower),
                shoot,
                // Collect from garden
                collect.apply(AutonomousPaths.collectGarden(follower)),
                // Shoot other side
                follow(follower, AutonomousPaths.shootOpposite1(follower)),
                shoot,
                // Collect from opposite flower
                collect.apply(AutonomousPaths.collectOppFlower(follower)),
                // Shoot other side again
                follow(follower, AutonomousPaths.shootOpposite2(follower)),
                shoot,
                // Collect from close flower
                collect.apply(AutonomousPaths.collectCloseFlower(follower)),
                // Shoot from start pose
                follow(follower, AutonomousPaths.shootFinal(follower)),
                shoot,
                follow(follower, AutonomousPaths.Park(follower))
        );
    }

    private static Command shootPath(Follower follower) {
        return follow(follower, AutonomousPaths.shootPath(follower));
    }
}
