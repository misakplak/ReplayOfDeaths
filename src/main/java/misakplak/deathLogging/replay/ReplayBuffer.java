package misakplak.deathLogging.replay;

import misakplak.deathLogging.recordables.Position;
import misakplak.deathLogging.recordables.Recordable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;


import java.util.*;

public class ReplayBuffer {

    private long startTick;
    private final List<Recordable> records = new ArrayList<>();
    private final Set<UUID> trackedEntities = new HashSet<>();



    public void add(Recordable record) {

        records.add(record);

        long oldestAllowedTick = record.tick() - 300;

        while (!records.isEmpty() && records.getFirst().tick() < oldestAllowedTick) {
            records.removeFirst();
        }
    }


    public ReplayBuffer() {
        this.startTick = TickTracker.getTick();
    }

    public long getStartTick() {
        return startTick;
    }

    public List<Recordable> getRecords(){
        return new ArrayList<>(records);
    }


    public void track(Entity entity) {
        trackedEntities.add(entity.getUniqueId());
    }

    public boolean isTracking(Entity entity) {
        return trackedEntities.contains(entity.getUniqueId());
    }

    public Set<UUID> getTrackedEntities() {
        return trackedEntities;
    }

    public void untrack(Entity entity) {
        trackedEntities.remove(entity.getUniqueId());
    }


    private final Map<UUID, Position> lastPositions = new HashMap<>();

    public Position getLastPosition(UUID entity) {
        return lastPositions.get(entity);
    }

    public void setLastPosition(UUID entity, Position position) {
        lastPositions.put(entity, position);
    }

    public void untrack(UUID uuid) {
        trackedEntities.remove(uuid);
        lastPositions.remove(uuid);
    }
}
