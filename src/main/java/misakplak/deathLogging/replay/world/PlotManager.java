package misakplak.deathLogging.replay.world;

import misakplak.deathLogging.DeathLogging;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class PlotManager {

    private final int halfPlotSize;
    private final int spacing;
    private static final int PLOTS_PER_ROW = 100;

    private final Map<UUID, int[]> plots = new HashMap<>();
    private final File file;

    private int nextIndex = 0;

    public PlotManager() {

        this.halfPlotSize = DeathLogging.getInstance().getConfig().getInt("world.log-world-size");
        this.spacing = DeathLogging.getInstance().getConfig().getInt("world.plot.plot-spacing");

        this.file = new File(DeathLogging.getInstance().getDataFolder(), "plots.yml");


        load();
    }

    public Location getPlot(World world, UUID replayId) {

        int[] grid = plots.get(replayId);

        if (grid == null) {
            int index = nextIndex++;
            grid = new int[] {
                    index % PLOTS_PER_ROW,
                    index / PLOTS_PER_ROW
            };
            plots.put(replayId, grid);
            save();
        }

        int x = grid[0] * (halfPlotSize  * 2 + spacing);
        int z = grid[1] * (halfPlotSize  * 2 + spacing);

        return new Location(world, x, 64, z);
    }

    private void load() {
        if (!file.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        nextIndex = config.getInt("nextIndex", 0);

        ConfigurationSection section = config.getConfigurationSection("plots");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                int gridX = section.getInt(key + ".x");
                int gridZ = section.getInt(key + ".z");

                plots.put(UUID.fromString(key), new int[]{gridX, gridZ});
            }
        }
    }

    private void save() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("nextIndex", nextIndex);

        for (Map.Entry<UUID, int[]> entry : plots.entrySet()) {

            String path = "plots." + entry.getKey();
            config.set(path + ".x", entry.getValue()[0]);
            config.set(path + ".z", entry.getValue()[1]);

        }

        try {
            config.save(file);
        } catch (IOException e) {
            DeathLogging.getInstance().getLogger().log(Level.WARNING, "Failed to save plots.yml", e);
        }
    }

    public int getHalfPlotSize() {
        return halfPlotSize;
    }
}