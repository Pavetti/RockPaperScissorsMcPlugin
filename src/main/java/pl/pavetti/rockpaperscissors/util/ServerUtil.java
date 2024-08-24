package pl.pavetti.rockpaperscissors.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.List;

@UtilityClass
public class ServerUtil {

    public static void broadcastMessageList(List<String> message) {
        Bukkit.getOnlinePlayers().forEach(player -> PlayerUtil.sendMessageList(player, message));
    }

    public static boolean isPaper(){
        try {
            // Any other works, just the shortest I could find.
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

}
