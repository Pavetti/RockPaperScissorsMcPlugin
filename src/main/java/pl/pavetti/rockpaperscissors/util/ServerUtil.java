package pl.pavetti.rockpaperscissors.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.List;

@UtilityClass
public class ServerUtil {

    public static void broadcastMessageList(List<String> message) {
        Bukkit.getOnlinePlayers().forEach(player -> PlayerUtil.sendMessageList(player, message));
    }
}
