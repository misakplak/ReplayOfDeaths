package misakplak.deathLogging.recordables;

public record LocationRecord(
        long tick,
        Position position
) implements Recordable {}