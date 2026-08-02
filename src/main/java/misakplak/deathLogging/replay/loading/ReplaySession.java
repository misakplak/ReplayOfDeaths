package misakplak.deathLogging.replay.loading;

import misakplak.deathLogging.replay.Replay;
import misakplak.deathLogging.replay.world.PlotCopier;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class ReplaySession {

    private final Player viewer;
    private final Replay replay;

    private final Location plotOrigin;

    private ReplayViewer replayViewer;

    public ReplaySession(Player viewer, Replay replay, Location plot) {
        this.viewer = viewer;
        this.replay = replay;
        this.plotOrigin = plot;
    }

    public void start() {

        replayViewer = new ReplayViewer(
                viewer,
                replay,
                plotOrigin
        );

        viewer.teleport(plotOrigin.clone().add(0.5, 2, 0.5));
        viewer.setGameMode(GameMode.SPECTATOR);

        replayViewer.spawn();
    }

    public void stop() {

        if (replayViewer != null) {
            replayViewer.despawn();
        }
    }
}