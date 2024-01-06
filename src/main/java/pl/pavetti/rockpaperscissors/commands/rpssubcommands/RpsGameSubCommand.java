package pl.pavetti.rockpaperscissors.commands.rpssubcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.api.timsixth.SubCommand;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.game.model.RpsGame;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;
import pl.pavetti.rockpaperscissors.waitingroom.WaitingRoomManager;

import java.util.List;

public class RpsGameSubCommand implements SubCommand {

    private final Economy economy;
    private final Settings settings;
    private final WaitingRoomManager waitingRoomManager;

    public RpsGameSubCommand( Economy economy, WaitingRoomManager waitingRoomManager) {
        settings = Settings.getInstance();
        this.waitingRoomManager = waitingRoomManager;
        this.economy = economy;
    }

    @Override
    public boolean executeCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

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
        int bet;
        double max = settings.getMaxBet();
        double min = settings.getMinBet();
        Player enemyPlayer = Bukkit.getPlayerExact(args[1]);
        try{
            bet = Integer.parseInt(args[2]);
        }catch (NumberFormatException e){
            PlayerUtil.sendMessagePrefixed(player,settings.getBetOutOfRangeMax().replace("{MAX}",String.valueOf(max)));
            return true;
        }


        if (PlayerUtil.compare(enemyPlayer,player)) {
            PlayerUtil.sendMessagePrefixed(player, settings.getMyselfInvite());
            return true;
        }
        //check bet
        if(max != 0){
            if(bet > max){
                PlayerUtil.sendMessagePrefixed(player,settings.getBetOutOfRangeMax().replace("{MAX}",String.valueOf(max)));
                return true;
            }
        }
        if(bet < min){
            PlayerUtil.sendMessagePrefixed(player,settings.getBetOutOfRangeMin().replace("{MIN}",String.valueOf(min)));
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
        RpsGameManager.getInstance().registerGame(rpsGame);
        sendInvitation(enemyPlayer,player.getName(),String.valueOf(bet));
        PlayerUtil.sendMessagePrefixed(player,settings.getSuccessfullyInvite().replace("{NAME}",enemyPlayer.getName()));

        return false;
    }

    private void sendInvitation(Player enemyPlayer,String initiator, String bet){
        String commandAccept = "/rps accept " + initiator;
        TextComponent acceptButton = new TextComponent(settings.getRpsInviteAcceptButton());
        TextComponent denyButton = new TextComponent(settings.getRpsInviteDenyButton());
        acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,commandAccept));
        acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("✔").color(ChatColor.WHITE).create()));
        denyButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("✖").color(ChatColor.WHITE).create()));

        enemyPlayer.sendMessage("");
        PlayerUtil.sendMessagePrefixed(enemyPlayer,
                settings.getRpsInvite()
                        .replace("{NAME}", initiator).replace("{BET}", bet));
        enemyPlayer.spigot().sendMessage(acceptButton,denyButton);
        enemyPlayer.sendMessage("");

    }

    @Override
    public String getName() {
        return "game";
    }
}