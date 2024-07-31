package pl.pavetti.rockpaperscissors.commands.rpssubcommands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.api.timsixth.SubCommand;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.game.model.RpsGame;
import pl.pavetti.rockpaperscissors.game.model.RpsPlayer;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;
import pl.pavetti.rockpaperscissors.waitingroom.WaitingRoomManager;
import pl.pavetti.rockpaperscissors.waitingroom.model.Waiter;
import pl.pavetti.rockpaperscissors.waitingroom.model.WaitingRoom;

import java.util.List;
import java.util.Optional;

public class RpsAcceptSubCommand implements SubCommand {

    private final Economy economy;
    private final WaitingRoomManager waitingRoomManager;
    private final boolean vault;

    public RpsAcceptSubCommand(Economy economy,WaitingRoomManager waitingRoomManager,boolean vault) {
        this.economy = economy;
        this.waitingRoomManager = waitingRoomManager;
        this.vault = vault;
    }

    @Override
    public boolean executeCommand(CommandSender sender, String[] args) {

        if(!vault) return true;

        Player acceptor = (Player) sender;
        Settings settings = Settings.getInstance();
        WaitingRoom waitingRoom = waitingRoomManager.getRpsInviteWR();

        if(args.length < 2){
            PlayerUtil.sendMessagePrefixed(acceptor, settings.getBadUseRpsAcceptCmd());
            return true;
        }
        if(!PlayerUtil.isPlayerOnline(args[1])){
            PlayerUtil.sendMessagePrefixed(acceptor, settings.getNoInvitation().replace("{NAME}",args[1]));
            return true;
        }

        Player initiator = Bukkit.getPlayerExact(args[1]);

        //check if player has invitation
        List<Waiter> waiterList = waitingRoom.getWaiterListOfPlayer(acceptor);
        Optional<Waiter> waiterOptional = findWaiter(waiterList,initiator);
        if(!waiterOptional.isPresent()){
            PlayerUtil.sendMessagePrefixed(acceptor, settings.getNoInvitation().replace("{NAME}",args[1]));
            return true;
        }


        Waiter waiter = waiterOptional.get();
        //check if initiator already play
        // jeżeli ktoś zaczyna gre (odpala się gui) wyrejstrowuje wszytstki inne gry z nim zwiazen
        // wiec tej gry juz nie bedzie
        if(!(RpsGameManager.getInstance().getRpsPlayer(acceptor).isPresent())){
            PlayerUtil.sendMessagePrefixed(acceptor,settings.getAlreadyPlay().replace("{NAME}",args[1]));
            waitingRoom.removeWaiter(waiter);
            return true;
        }

        RpsGame rpsGame = ((RpsPlayer) waiter.getInstance()).getRpsGame();
        double bet = rpsGame.getBet();
        //economy check
        if (economy.getBalance(initiator) < bet || economy.getBalance(acceptor) < bet) {
            PlayerUtil.sendMessagePrefixed(acceptor, settings.getNotEnoughMoney());
            return true;
        }


        //start game
        RpsGameManager.getInstance().startGame(
                ((RpsPlayer) waiter.getInstance()).getRpsGame(),
                false);
        waiterList.forEach(waitingRoom::removeWaiter);

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
