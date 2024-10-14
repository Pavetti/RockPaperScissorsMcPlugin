package pl.pavetti.rockpaperscissors.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.pavetti.rockpaperscissors.Main;
import pl.pavetti.rockpaperscissors.config.FileManager;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;

public class RpsReloadCommand implements CommandExecutor {
    private final FileManager fileManager;
    private final Settings settings = Settings.getInstance();
    private final Main plugin = Main.getInstance();

    public RpsReloadCommand(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if(sender.hasPermission("rps.admin.reload")){
            fileManager.reload();
            settings.load();
            RpsGameManager.getInstance().reloadResources();
            if(sender instanceof Player){
                PlayerUtil.sendPrefixedMessage((Player) sender,settings.getSuccessfullyPluginReload());
            }else {
                plugin.getLogger().severe(settings.getSuccessfullyPluginReload());
            }


        }else {
            if(sender instanceof Player){
                PlayerUtil.sendPrefixedMessage((Player) sender,settings.getNoPermission());
            }
        }
        return false;
    }
}
