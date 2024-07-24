package pl.pavetti.rockpaperscissors.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RpsTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@Nullable CommandSender sender,
                                      @Nullable Command cmd,
                                      @Nullable String label,
                                      @Nullable String[] args) {
        List<String> compleats = new ArrayList<>();
        if(args.length == 1){
            compleats = Arrays.asList("game","accept","toggle");
        }
        else if (args.length == 2 && args[0].equals("game")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                compleats.add(player.getName());
            }
        } else if (args.length == 2 && args[0].equals("accept")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                compleats.add(player.getName());
            }
        } else if (args.length == 3 && args[0].equals("game")) {
            compleats.add("<bet>");
        }
        return compleats;
    }
}
