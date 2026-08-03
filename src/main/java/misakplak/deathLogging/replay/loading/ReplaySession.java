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
    private final Runnable onEnd;

    private final Location returnLocation;
    private final GameMode returnGameMode;

    private ReplayViewer replayViewer;

    public ReplaySession(Player viewer, Replay replay, Location plot, Runnable onEnd) {
        this.viewer = viewer;
        this.replay = replay;
        this.plotOrigin = plot;
        this.onEnd = onEnd;

        this.returnLocation = viewer.getLocation();
        this.returnGameMode = viewer.getGameMode();
    }

    public void start() {
        replayViewer = new ReplayViewer(viewer, replay, plotOrigin, this::onReplayEnd);

        viewer.teleport(plotOrigin.clone().add(0.5, 2, 0.5));
        viewer.setGameMode(GameMode.SPECTATOR);

        replayViewer.spawn();
    }


    private void onReplayEnd() {
        if (viewer.isOnline()) {
            viewer.teleport(returnLocation);
            viewer.setGameMode(returnGameMode);
        }

        if (onEnd != null) {
            onEnd.run();
        }
    }

    public void stop() {

        if (replayViewer != null) {
            replayViewer.despawn();
        }
    }
}