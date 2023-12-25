package pl.pavetti.rockpaperscissors.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ChatUtil {

    public static String chatColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> chatColor(List<String> stringList) {
        List<String> strings = new ArrayList<>();
        for (String str : stringList) {
            String msg = ChatColor.translateAlternateColorCodes('&', str);
            strings.add(msg);
        }
        return strings;
    }
}
