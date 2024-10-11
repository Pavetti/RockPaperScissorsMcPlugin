package pl.pavetti.rockpaperscissors.config.file;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.pavetti.rockpaperscissors.config.FileManager;
import pl.pavetti.rockpaperscissors.config.model.GuiItemModel;
import pl.pavetti.rockpaperscissors.config.model.GuiModel;
import pl.pavetti.rockpaperscissors.util.ItemUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Getter
public class GuiConfig {
    private final FileManager fileManager;

    private GuiModel findEnemyGuiModel;

    public GuiConfig(FileManager fileManger ) {
        this.fileManager = fileManger;
        load();
    }

    public void load(){
        findEnemyGuiModel = loadGui( "findEnemy", List.of("fillItem", "playerHead","pageForward", "pageBack") );
    }

    private GuiModel loadGui(String guiPathName, List<String> itemsPathNames){
        YamlConfiguration yamlConfiguration = fileManager.getGuiYaml();
        ConfigurationSection section = yamlConfiguration.getConfigurationSection("gui." + guiPathName);
        assert section != null;

        Map<String,GuiItemModel> items = new HashMap<>();
        itemsPathNames.forEach( itemPath -> items.put(itemPath, loadItem(section, "item." + itemPath)) );

        return new GuiModel(
                section.getStringList("structure"),
                section.getString("title"),
                items
        );
    }


    private GuiItemModel loadItem(ConfigurationSection section, String pathItemName){
        Material material = Material.BARRIER;
        if(section.getString(pathItemName + ".material") != null)
            material = ItemUtil.getMaterialOf( section.getString(pathItemName + ".material") );
        return new GuiItemModel(
                section.getString(pathItemName + ".name"),
                section.getStringList(pathItemName + ".lore"),
                section.getStringList(pathItemName + ".hasNextPageLore"),
                section.getStringList(pathItemName + ".hasNotNextPageLore"),
                material,
                section.getInt(pathItemName + ".modelId")
        );
    }

}
