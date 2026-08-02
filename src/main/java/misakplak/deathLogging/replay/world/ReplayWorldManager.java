package misakplak.deathLogging.replay.world;

import org.bukkit.*;

public class ReplayWorldManager {

    private static final String WORLD_NAME = "replay_world";
    private World world;

    private final PlotManager plotManager = new PlotManager();

    public PlotManager getPlotManager() {
        return plotManager;
    }

    public void load() {
    WorldCreator creator = new WorldCreator(WORLD_NAME);

        creator.generator(new EmptyChunkGenerator());

    world = creator.createWorld();

        world.setStorm(false);
        world.setTime(6000);
}

public World getWorld() {
    return world;
}
}
