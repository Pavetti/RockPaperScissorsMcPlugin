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

import java.util.List;
import java.util.Optional;

public class RpsGameSubCommand implements SubCommand {

    private final Economy economy;
    private final WaitingRoomManager waitingRoomManager;

    public RpsGameSubCommand( Economy economy, WaitingRoomManager waitingRoomManager) {
        this.waitingRoomManager = waitingRoomManager;
        this.economy = economy;
    }

    @Override
    public boolean executeCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Settings settings = Settings.getInstance();

        //check args
        if (args.length < 3) {
            PlayerUtil.sendMessagePrefixed(player, settings.getBadUseRpsGameCmd());
            return true;
        }
        if (!PlayerUtil.isPlayerOnline(args[1])) {
            PlayerUtil.sendMessagePrefixed(player, settings.getPlayerNotExist().replace("{NAME}", args[1]));
            return true;
        }
        if (!args[2].matches("-?\\d+")) {
            //check bet argument
            PlayerUtil.sendMessagePrefixed(player, settings.getBadUseRpsGameCmd());
            return true;
        }

        Player enemyPlayer = Bukkit.getPlayerExact(args[1]);
        int bet = Integer.parseInt(args[2]);


        if (PlayerUtil.compare(enemyPlayer,player)) {
            PlayerUtil.sendMessagePrefixed(player, settings.getMyselfInvite());
            return true;
        }
        //check bet
        double max = settings.getMaxBet();
        double min = settings.getMinBet();
        if(bet > max || bet < min){
            PlayerUtil.sendMessagePrefixed(player,settings.getBetOutOfRange());
            return true;
        }

        //economy check
        if (economy.getBalance(player) < bet || economy.getBalance(enemyPlayer) < bet) {
            PlayerUtil.sendMessagePrefixed(player, settings.getNotEnoughMoney());
            return true;
        }

        RpsGameManager rpsGameManager =  RpsGameManager.getInstance();

        //check if already invite
        //check if enemy player already play
        List<RpsGame> games = rpsGameManager.getRpsGamesWhere(enemyPlayer);
            for (RpsGame game : games) {
                if(game.isStarted()){
                    PlayerUtil.sendMessagePrefixed(player,settings.getAlreadyPlay().replace("{NAME}",args[1]));
                    return true;
                }
                if(PlayerUtil.compare(game.getInitiator().getPlayer(), player)){
                    PlayerUtil.sendMessagePrefixed(player,settings.getAlreadyInvite());
                    return true;
                }
            }



        RpsGame rpsGame = new RpsGame(player,enemyPlayer,bet);
        waitingRoomManager.getRpsInviteWR().addWaiter(rpsGame.getOpponent());
        RpsGameManager.getInstance().registryGame(rpsGame);
        PlayerUtil.sendMessagePrefixed(enemyPlayer,
                settings.getRpsInvite()
                        .replace("{NAME}", player.getName()).replace("{BET}", String.valueOf(bet)));

        return false;
    }

    @Override
    public String getName() {
        return "game";
    }
}