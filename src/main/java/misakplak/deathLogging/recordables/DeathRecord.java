package misakplak.deathLogging.recordables;


public record DeathRecord(
        long tick,
        Position position
)implements Recordable {
}
