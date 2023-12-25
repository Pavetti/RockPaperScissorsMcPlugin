package pl.pavetti.rockpaperscissors.game;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.inventoryholder.RpsMenuInventoryHolder;

public class GameUI {
    private Inventory inventory;

    public GameUI() {
        createInventory();
    }

    public void createInventory(){
        Settings settings = Settings.getInstance();
        inventory = Bukkit.createInventory(new RpsMenuInventoryHolder(),27,settings.getGuiMainTitle());
        //rock
        ItemStack rock = new ItemStack(settings.getGuiRockItem(),1);
        ItemMeta rockMeta = rock.getItemMeta();
        rockMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        rockMeta.setDisplayName(settings.getGuiRockName());
        rock.setItemMeta(rockMeta);
        //paper
        ItemStack paper = new ItemStack(settings.getGuiPaperItem(),1);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        paperMeta.setDisplayName(settings.getGuiPaperName());
        paper.setItemMeta(paperMeta);
        //scissors
        ItemStack scissors = new ItemStack(settings.getGuiScissorsItem(),1);
        ItemMeta scissorsMeta = scissors.getItemMeta();
        scissorsMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        scissorsMeta.setDisplayName(settings.getGuiScissorsName());
        scissors.setItemMeta(scissorsMeta);

        inventory.setItem(11,rock);
        inventory.setItem(13,paper);
        inventory.setItem(15,scissors);
    }

    public Inventory getInventory() {
        return inventory;
    }
}
