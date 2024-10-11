package pl.pavetti.rockpaperscissors.config;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.pavetti.rockpaperscissors.Main;


import java.io.File;

/**
 * This class stores all plugins files
 */
@Getter
public class FileManager {

    private final Main main;

    private File guiFile;

    private YamlConfiguration guiYaml;

    public FileManager(Main main) {
        this.main = main;
        createFiles();
        loadFiles();
    }

    private void createFiles() {
        guiFile = createFile("gui.yml");
    }

    private File createFile(String name) {
        if (!main.getDataFolder().mkdir()) {
            main.getDataFolder().mkdirs();
        }
        File file = new File(main.getDataFolder(), name);
        if (!file.exists()) {
            main.saveResource(name, true);
        }
        return file;
    }

    private void loadFiles() {
        guiYaml = YamlConfiguration.loadConfiguration( guiFile );
    }

    public void reload() {

        loadFiles();

        main.reloadConfig();
        main.getGuiConfig().load();
    }
}