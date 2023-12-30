package pl.pavetti.rockpaperscissors.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.config.Settings;

@UtilityClass
public class PlayerUtil {

    public static void sendMessagePrefixed(Player player,String message){
        String prefix = Settings.getInstance().getPrefix();
        player.sendMessage(prefix + message);
    }
    public static boolean isPlayerOnline(String nick){
        Player playerExact = Bukkit.getPlayerExact(nick);
        if(playerExact != null) return playerExact.isOnline();
        return false;
    }

    public static boolean compare(Player player1, Player player2){
        return player1.getUniqueId().equals(player2.getUniqueId());
    }
}
