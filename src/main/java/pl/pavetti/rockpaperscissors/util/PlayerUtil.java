package pl.pavetti.rockpaperscissors.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import pl.pavetti.rockpaperscissors.Main;

import java.util.List;

@UtilityClass
public class PlayerUtil {


    public static boolean isPlayerOnline(String nick){
        Player playerExact = Bukkit.getPlayerExact(nick);
        if(playerExact != null) return playerExact.isOnline();
        return false;
    }

    public static boolean compare(Player player1, Player player2){
        return player1.getUniqueId().equals(player2.getUniqueId());
    }


    public static void sendPrefixedMessage(Player player, String message, String... placeholders){
        Audience audience = getAudience(player);
        String messageWithPlaceholders = ChatUtil.replacePlaceholders(message,placeholders);
        Component formattedMessage = ChatUtil.formatMessageAndAddPrefix(messageWithPlaceholders);
        audience.sendMessage(formattedMessage);
    }

    public static void sendMessageList(Player player, List<String> messages, String... placeholders){
        Audience audience = getAudience(player);
        for(String line : messages){
            String lineWithPlaceholders = ChatUtil.replacePlaceholders(line,placeholders);
            audience.sendMessage(ChatUtil.formatMessage(lineWithPlaceholders));
        }
    }

    public boolean isVanished(final Player player) {
        final List<MetadataValue> list = player.getMetadata("vanished");
        for (MetadataValue value : list) {
            if (value.asBoolean()) {
                return true;
            }
        }
        return false;
    }

    public Audience getAudience(Player player) {
        if(ServerUtil.isPaper())
            return Audience.audience(player);
        else
            return Main.getInstance().adventure().player(player);
    }

}
