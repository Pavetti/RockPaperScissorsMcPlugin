package pl.pavetti.rockpaperscissors.commands;


import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.api.timsixth.ParentCommand;
import pl.pavetti.rockpaperscissors.commands.rpssubcommands.RpsAcceptSubCommand;
import pl.pavetti.rockpaperscissors.commands.rpssubcommands.RpsGameSubCommand;
import pl.pavetti.rockpaperscissors.commands.rpssubcommands.RpsToggleInviteSubCommand;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;
import pl.pavetti.rockpaperscissors.waitingroom.WaitingRoomManager;


public class RpsCommand extends ParentCommand {
    private final boolean vault;

    public RpsCommand(Economy economy, WaitingRoomManager waitingRoomManager ,boolean vault) {
        super("", true, true, false);
        this.vault = vault;

        getSubCommands().add(new RpsGameSubCommand(economy,waitingRoomManager,vault));
        getSubCommands().add(new RpsAcceptSubCommand(economy,waitingRoomManager,vault));
        getSubCommands().add(new RpsToggleInviteSubCommand(vault));
    }

    @Override
    protected boolean executeCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(!vault)
            PlayerUtil.sendMessagePrefixed(player, Settings.getInstance().getNoVaultDependency());
        else
            Settings.getInstance().getDescriptionCommand().forEach(player::sendMessage);

        return false;
    }
}
