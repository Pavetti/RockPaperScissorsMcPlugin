package pl.pavetti.rockpaperscissors.game;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.inventoryholder.RpsMenuInventoryHolder;
import pl.pavetti.rockpaperscissors.util.ChatUtil;

public class GameGUI {
    @Getter
    private Inventory mainInventory;
    private final Settings settings = Settings.getInstance();
    private ItemStack rock;
    private ItemStack paper;
    private ItemStack scissors;
    private ItemStack fillItem;


    public GameGUI() {
        loadItemStacks();
        createMainInventory();
    }

    private Inventory createInventory(Component title){
        Inventory inventory = Bukkit.createInventory(new RpsMenuInventoryHolder(),27, title);
        inventory.setItem(11,rock);
        inventory.setItem(13,paper);
        inventory.setItem(15,scissors);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, fillItem);
            }
        }
        return inventory;
    }
    private void createMainInventory(){
        mainInventory = createInventory(ChatUtil.formatMessage(settings.getGuiMainTitle()));
    }

    private void loadItemStacks(){
        Settings settings = Settings.getInstance();
        rock = new ItemStack(settings.getGuiRockItem(),1);
        ItemMeta rockMeta = rock.getItemMeta();
        rockMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        rockMeta.itemName(ChatUtil.formatMessage(settings.getGuiRockName()));
        rock.setItemMeta(rockMeta);
        //paper
        paper = new ItemStack(settings.getGuiPaperItem(),1);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        paperMeta.itemName(ChatUtil.formatMessage(settings.getGuiPaperName()));
        paper.setItemMeta(paperMeta);
        //scissors
        scissors = new ItemStack(settings.getGuiScissorsItem(),1);
        ItemMeta scissorsMeta = scissors.getItemMeta();
        scissorsMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        scissorsMeta.itemName(ChatUtil.formatMessage(settings.getGuiScissorsName()));
        scissors.setItemMeta(scissorsMeta);
        //fillItem
        fillItem = new ItemStack(settings.getGuiMainFillItem(),1);
        ItemMeta fillItemMeta = fillItem.getItemMeta();
        fillItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        fillItemMeta.itemName(ChatUtil.formatMessage(settings.getGuiMainFillItemName()));
        fillItem.setItemMeta(fillItemMeta);
    }

    public void reload(){
        loadItemStacks();
        createMainInventory();
    }

}
