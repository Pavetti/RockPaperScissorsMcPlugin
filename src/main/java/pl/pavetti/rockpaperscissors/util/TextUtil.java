package pl.pavetti.rockpaperscissors.util;

import de.themoep.minedown.adventure.MineDown;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import pl.pavetti.rockpaperscissors.config.Settings;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;

import java.util.List;

@UtilityClass
public class TextUtil {

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

    public static ComponentWrapper wrapTextToXenoComponent(String text){
        return new AdventureComponentWrapper( MineDown.parse( text ) );
    }

    public static List<ComponentWrapper> wrapTextListToXenoComponentList(List<String> textList){
        return textList.stream()
                .map(TextUtil::wrapTextToXenoComponent)
                .toList();
    }
}
