package pl.pavetti.rockpaperscissors.commands;


import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.api.timsixth.ParentCommand;
import pl.pavetti.rockpaperscissors.commands.rpssubcommands.RpsAcceptSubCommand;
import pl.pavetti.rockpaperscissors.commands.rpssubcommands.RpsGameSubCommand;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.waitingroom.WaitingRoomManager;


public class RpsCommand extends ParentCommand {

    public RpsCommand(Economy economy, WaitingRoomManager waitingRoomManager ) {
        super("", true, true, false);

        getSubCommands().add(new RpsGameSubCommand(economy,waitingRoomManager));
        getSubCommands().add(new RpsAcceptSubCommand(waitingRoomManager));
    }

    @Override
    protected boolean executeCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        player.sendMessage("info do dodania");
        Settings.getInstance().getDescriptionCommand().forEach(player::sendMessage);

        return false;
    }
}
