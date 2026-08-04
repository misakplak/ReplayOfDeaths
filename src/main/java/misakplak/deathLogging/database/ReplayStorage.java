package misakplak.deathLogging.database;

import misakplak.deathLogging.replay.Replay;

import java.util.List;
import java.util.UUID;

public interface ReplayStorage {
    void save(Replay replay);
    Replay load(UUID replayId);
    Replay loadLatest();
    List<ReplaySummary> list(UUID playerId, boolean asKiller, int page, int pageSize);
    int count(UUID playerId, boolean asKiller);
}
