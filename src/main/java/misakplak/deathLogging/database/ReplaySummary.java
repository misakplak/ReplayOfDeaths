package misakplak.deathLogging.database;

import java.util.UUID;

public record ReplaySummary(UUID replayId, long createdAt) {}
