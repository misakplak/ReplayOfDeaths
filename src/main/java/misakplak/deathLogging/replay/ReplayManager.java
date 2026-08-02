package misakplak.deathLogging.replay;

import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReplayManager {

    private final Map<String, ReplayBuffer> buffers = new HashMap<>();

    public void create(Player player){
        buffers.put(player.getUniqueId().toString(), new ReplayBuffer());
    }

    public void remove(Player player){
        buffers.remove(player.getUniqueId().toString());
    }

    public ReplayBuffer get(Player player){
        return buffers.get(player.getUniqueId().toString());
    }

}
