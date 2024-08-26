package pl.pavetti.rockpaperscissors.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@UtilityClass
public class ServerUtil {

    public static void broadcastMessageList(List<String> message) {
        Bukkit.getOnlinePlayers().forEach(player -> PlayerUtil.sendMessageList(player, message));
    }

    public static boolean isPaper(){
        try {
            Class<?> itemMetaClass = ItemMeta.class;
            Class<?> bukkitClass = Bukkit.class;

            Class<?>[] itemMetaParams = { Component.class };
            Class<?>[] bukkitParams = { InventoryHolder.class, int.class, Component.class };

            itemMetaClass.getMethod("itemName", itemMetaParams);
            bukkitClass.getMethod("createInventory", bukkitParams);
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        }
    }

}
