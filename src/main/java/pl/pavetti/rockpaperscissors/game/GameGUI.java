package pl.pavetti.rockpaperscissors.game;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.config.file.GuiConfig;
import pl.pavetti.rockpaperscissors.inventoryholder.RpsMenuInventoryHolder;
import pl.pavetti.rockpaperscissors.util.TextUtil;
import pl.pavetti.rockpaperscissors.util.ItemUtil;
import pl.pavetti.rockpaperscissors.util.ServerUtil;

public class GameGUI {

    private final GuiConfig guiConfig;

    @Getter
    private Inventory gameInventory;

    private ItemStack rock;
    private ItemStack paper;
    private ItemStack scissors;
    private ItemStack fillItem;


    public GameGUI(GuiConfig guiConfig) {
        this.guiConfig = guiConfig;
        load();
    }

    public void load(){
        loadItemStacks();
        loadInventory();
    }


    private void loadInventory(){
        gameInventory = createInventoryDependsOnEngine();
        gameInventory.setItem(11,rock);
        gameInventory.setItem(13,paper);
        gameInventory.setItem(15,scissors);
        for (int i = 0; i < gameInventory.getSize(); i++) {
            if (gameInventory.getItem(i) == null) {
                gameInventory.setItem(i, fillItem);
            }
        }
    }

    private void loadItemStacks(){
        rock = loadItemStack("rock");
        paper = loadItemStack("paper");
        scissors = loadItemStack("scissors");
        fillItem = loadItemStack("fillItem");
    }

    private ItemStack loadItemStack(String itemConfigOptionName){
        Material material = ItemUtil.getMaterialOf(
                (String)guiConfig.getByPath("gui.game." + itemConfigOptionName + ".material"));
        ItemStack itemStack = new ItemStack(material,1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        setNameDependsOnEngine(itemMeta, (String)guiConfig.getByPath("gui.game." + itemConfigOptionName + ".name"));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private void setNameDependsOnEngine(ItemMeta itemMeta, String name){
        if(ServerUtil.isPaper())
            itemMeta.itemName(TextUtil.formatMessage(name));
        else
            itemMeta.setDisplayName(TextUtil.formatMessageLegacy(name));
    }

    private Inventory createInventoryDependsOnEngine(){
        if(ServerUtil.isPaper())
            return Bukkit.createInventory(new RpsMenuInventoryHolder(),27,
                    TextUtil.formatMessage(guiConfig.getGuiGameTitle()));
        else
            return Bukkit.createInventory(new RpsMenuInventoryHolder(),27,
                TextUtil.formatMessageLegacy(guiConfig.getGuiGameTitle()));
    }

}
