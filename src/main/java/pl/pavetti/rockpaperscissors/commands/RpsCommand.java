package pl.pavetti.rockpaperscissors.commands;


import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.api.timsixth.ParentCommand;
import pl.pavetti.rockpaperscissors.commands.rpssubcommands.RpsAcceptSubCommand;
import pl.pavetti.rockpaperscissors.commands.rpssubcommands.RpsGameSubCommand;
import pl.pavetti.rockpaperscissors.commands.rpssubcommands.RpsToggleInviteSubCommand;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.RequestManager;
import pl.pavetti.rockpaperscissors.game.gui.findenemygui.FindEnemyGui;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;


public class RpsCommand extends ParentCommand {
    private final boolean vault;

    public RpsCommand(Economy economy, RequestManager requestManager, boolean vault, FindEnemyGui findEnemyGui) {
        super("", true, true, false);
        this.vault = vault;

        getSubCommands().add(new RpsGameSubCommand(economy,requestManager,vault,findEnemyGui));
        getSubCommands().add(new RpsAcceptSubCommand(economy,requestManager,vault));
        getSubCommands().add(new RpsToggleInviteSubCommand(vault));
    }

    @Override
    protected boolean executeCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(!vault)
            PlayerUtil.sendPrefixedMessage(player, Settings.getInstance().getNoVaultDependency());
        else
            PlayerUtil.sendMessageList(player, Settings.getInstance().getDescriptionCommand());

        return false;
    }
}
