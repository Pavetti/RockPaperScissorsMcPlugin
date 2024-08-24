package pl.pavetti.rockpaperscissors.util;

import de.themoep.minedown.adventure.MineDown;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import pl.pavetti.rockpaperscissors.config.Settings;

@UtilityClass
public class ChatUtil {


    public static String formatMessageLegacy(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static Component formatMessage(String text) {
        return new MineDown(text).toComponent();
    }

    public static Component formatMessageAndAddPrefix(String message){
        String prefixedMessage = Settings.getInstance().getPrefix() + message;
        return new MineDown(prefixedMessage).toComponent();
    }

    public static String replacePlaceholders(String message, String... placeholders) {
        for (int i = 0; i < placeholders.length; i += 2) {
            message = message.replace(placeholders[i], placeholders[i + 1]);
        }
        return message;
    }
}
