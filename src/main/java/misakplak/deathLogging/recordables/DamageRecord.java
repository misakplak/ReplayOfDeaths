package misakplak.deathLogging.recordables;

import org.bukkit.event.entity.EntityDamageEvent;

public record DamageRecord(
        long tick,
        Position position,
        double damage,
        EntityDamageEvent.DamageCause cause
) implements Recordable {}