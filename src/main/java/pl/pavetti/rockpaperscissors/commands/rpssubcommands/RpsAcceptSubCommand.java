package pl.pavetti.rockpaperscissors.commands.rpssubcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.api.timsixth.SubCommand;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.game.model.RpsPlayer;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;
import pl.pavetti.rockpaperscissors.waitingroom.WaitingRoomManager;
import pl.pavetti.rockpaperscissors.waitingroom.model.Waiter;
import pl.pavetti.rockpaperscissors.waitingroom.model.WaitingRoom;

import java.util.List;
import java.util.Optional;

public class RpsAcceptSubCommand implements SubCommand {

    private final WaitingRoomManager waitingRoomManager;

    public RpsAcceptSubCommand(WaitingRoomManager waitingRoomManager) {
        this.waitingRoomManager = waitingRoomManager;
    }

    @Override
    public boolean executeCommand(CommandSender sender, String[] args) {

        Player opponent = (Player) sender;
        Settings settings = Settings.getInstance();
        WaitingRoom waitingRoom = waitingRoomManager.getRpsInviteWR();

        if(args.length < 2){
            PlayerUtil.sendMessagePrefixed(opponent, settings.getBadUseRpsAcceptCmd());
            return true;
        }
        if(!PlayerUtil.isPlayerOnline(args[1])){
            PlayerUtil.sendMessagePrefixed(opponent, settings.getNoInvitation().replace("{NAME}",args[1]));
            return true;
        }

        Player initiator = Bukkit.getPlayerExact(args[1]);

        //check if player has invitation
        List<Waiter> waiterList = waitingRoom.getWaiterList(opponent);
        Optional<Waiter> waiterOptional = findWaiter(waiterList,initiator);

        if(!waiterOptional.isPresent()){
            PlayerUtil.sendMessagePrefixed(opponent, settings.getNoInvitation().replace("{NAME}",args[1]));
            return true;
        }
        //start game
        Waiter waiter = waiterOptional.get();
        RpsGameManager.getInstance().startGame(
                ((RpsPlayer) waiter.getInstance()).getRpsGame());
        waitingRoom.removeWaiter(waiter);

        return false;
    }

    private Optional<Waiter> findWaiter(List<Waiter> waiters, Player initiator){
        return waiters.stream()
                .filter(waiter -> PlayerUtil.compare(
                        ((RpsPlayer) waiter.getInstance()).getRpsGame().getInitiator().getPlayer()
                        , initiator))
                .findFirst();
    }

    @Override
    public String getName() {
        return "accept";
    }
}
