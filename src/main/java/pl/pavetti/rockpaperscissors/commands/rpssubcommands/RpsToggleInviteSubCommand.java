package pl.pavetti.rockpaperscissors.commands.rpssubcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.api.timsixth.SubCommand;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;

public class RpsToggleInviteSubCommand implements SubCommand {

    private final boolean vault;
    RpsGameManager rpsGameManager = RpsGameManager.getInstance();
    Settings settings = Settings.getInstance();

    public RpsToggleInviteSubCommand(boolean vault) {
        this.vault = vault;
    }

    @Override
    public boolean executeCommand(CommandSender sender, String[] args) {

        if(!vault) return true;

        Player player = (Player) sender;

        boolean result = rpsGameManager.toggleBlockingInvitationToPlayer(player.getUniqueId().toString());
        // result true = added to no attend set
        // result false = removed form no attend set

        if(result) PlayerUtil.sendPrefixedMessage(player, settings.getBlockingInvitationOn());
        else PlayerUtil.sendPrefixedMessage(player, settings.getBlockingInvitationOff());

        return false;
    }

    @Override
    public String getName() {
        return "toggle";
    }
}
