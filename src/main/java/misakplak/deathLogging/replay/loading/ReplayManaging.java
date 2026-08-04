package misakplak.deathLogging.replay.loading;

import misakplak.deathLogging.database.MongoReplayStorage;
import misakplak.deathLogging.replay.Replay;
import misakplak.deathLogging.replay.world.PlotCopier;
import misakplak.deathLogging.replay.world.PlotRollback;
import misakplak.deathLogging.replay.world.ReplayWorldManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReplayManaging {

    private final Map<UUID, ReplaySession> sessions = new HashMap<>();
    private final MongoReplayStorage storage;

    private final ReplayWorldManager replayWorldManager;
    private final PlotCopier plotCopier;
    private final PlotRollback plotRollback = new PlotRollback();

    public ReplayManaging(MongoReplayStorage storage, ReplayWorldManager replayWorldManager){
        this.storage = storage;
        this.replayWorldManager = replayWorldManager;
        this.plotCopier = new PlotCopier();
    }

    public void play(Player player, UUID replayId){

        stop(player);

        Replay replay = storage.load(replayId);

        if (replay == null) {
            player.sendMessage("§c§l§nReplay not found!");
            return;
        }

        Location plot = replayWorldManager
                .getPlotManager()
                .getPlot(replayWorldManager.getWorld(), replay.getReplayId());

        plotCopier.copy(replay.getDeathlocation(), plot);
        plotRollback.rollback(replay, plot);


        ReplaySession session = new ReplaySession(player, replay, plot,
                () -> sessions.remove(player.getUniqueId())
                );

        sessions.put(player.getUniqueId(), session);
        session.start();
    }

    public void stop(Player player){

        ReplaySession session = sessions.remove(player.getUniqueId());

        if(session != null){
            session.stop();
        }

    }

    public boolean isWatching(Player player){
        return sessions.containsKey(player.getUniqueId());
    }
}
