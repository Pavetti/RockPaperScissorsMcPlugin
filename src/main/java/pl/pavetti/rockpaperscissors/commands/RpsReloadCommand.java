package pl.pavetti.rockpaperscissors.commands;



import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.Main;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;

public class RpsReloadCommand implements CommandExecutor {
    private final Settings settings = Settings.getInstance();
    private final Main plugin = Main.getInstance();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender.hasPermission("rps.admin")){
            plugin.reloadConfig();
            settings.load();
            RpsGameManager.getInstance().reloadResources();
            if(sender instanceof Player){
                PlayerUtil.sendMessagePrefixed((Player) sender,settings.getSuccessfullyPluginReload());
            }else {
                plugin.getLogger().severe(settings.getSuccessfullyPluginReload());
            }


        }else {
            if(sender instanceof Player){
                PlayerUtil.sendMessagePrefixed((Player) sender,settings.getNoPermission());
            }
        }
        return false;
    }
}
