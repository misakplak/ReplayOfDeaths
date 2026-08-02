package misakplak.deathLogging.recordables;

import java.util.UUID;

public record EntityMoveRecord(
        long tick,
        UUID entityId,
        Position position
)implements Recordable{
}
