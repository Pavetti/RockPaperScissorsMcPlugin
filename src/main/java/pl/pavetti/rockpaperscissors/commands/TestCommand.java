package pl.pavetti.rockpaperscissors.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.pavetti.rockpaperscissors.game.gui.findenemygui.FindEnemyGui;

public class TestCommand implements CommandExecutor {
    FindEnemyGui findPlayerGui = new FindEnemyGui();
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        findPlayerGui.openGui((Player) commandSender);
        ((Player) commandSender).sendMessage("Test command executed");
        return false;
    }
}
