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
import pl.pavetti.rockpaperscissors.game.RequestManager;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.game.model.RpsInvitation;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;

import java.time.LocalDateTime;

public class RpsGameSubCommand implements SubCommand {

    private final Economy economy;
    private final Settings settings;
    private final boolean vault;
    private final RpsGameManager rpsGameManager =  RpsGameManager.getInstance();
    private final RequestManager requestManager;

    public RpsGameSubCommand(Economy economy, RequestManager requestManager, boolean vault) {
        settings = Settings.getInstance();
        this.economy = economy;
        this.requestManager = requestManager;
        this.vault = vault;
    }

    @Override
    public boolean executeCommand(CommandSender sender, String[] args) {

        if(!vault) return true;

        Player initiator = (Player) sender;

        //check if initiator in game
        if(rpsGameManager.isPlayerInGame(initiator)){
            PlayerUtil.sendMessagePrefixed(initiator, settings.getCmdPerformWhileGame());
            return true;
        }

        //check args
        if (args.length < 3) {
            PlayerUtil.sendMessagePrefixed(initiator, settings.getBadUseRpsGameCmd());
            return true;
        }
        if (!PlayerUtil.isPlayerOnline(args[1])) {
            PlayerUtil.sendMessagePrefixed(initiator, settings.getPlayerNotExist().replace("{NAME}", args[1]));
            return true;
        }
        if (!args[2].matches("\\d+(\\.\\d+)?")) {
            //check bet argument
            PlayerUtil.sendMessagePrefixed(initiator, settings.getBadUseRpsGameCmd());
            return true;
        }
        double bet;
        double max = settings.getMaxBet();
        double min = settings.getMinBet();
        Player opponent = Bukkit.getPlayerExact(args[1]);
        bet = Double.parseDouble(args[2]);

        //check if player invite himself
        if (PlayerUtil.compare(opponent,initiator)) {
            PlayerUtil.sendMessagePrefixed(initiator, settings.getMyselfInvite());
            return true;
        }

        //check bet
        if(max != 0){
            if(bet > max){
                PlayerUtil.sendMessagePrefixed(initiator,settings.getBetOutOfRangeMax().replace("{MAX}",String.valueOf(max)));
                return true;
            }
        }
        if(bet < min){
            PlayerUtil.sendMessagePrefixed(initiator,settings.getBetOutOfRangeMin().replace("{MIN}",String.valueOf(min)));
            return true;
        }

        //economy check
        if (economy.getBalance(initiator) < bet || economy.getBalance(opponent) < bet) {
            PlayerUtil.sendMessagePrefixed(initiator, settings.getNotEnoughMoney());
            return true;
        }

        //check if player has blocked invitations
        if(rpsGameManager.isPlayerBlockingInvitation(opponent.getUniqueId().toString())){
            PlayerUtil.sendMessagePrefixed(initiator,settings.getBlockedInvitationMessage()
                    .replace("{NAME}",opponent.getName()));
            return true;
        }

        //check if already invite
        boolean[] hasInvitationArray = {false};
        requestManager.getInvitations(opponent.getUniqueId()).stream()
                .filter(requestManager::isInvitationValid)
                .filter(rpsInvitation -> PlayerUtil.compare(rpsInvitation.getInitiator(), initiator))
                .findFirst().ifPresent(rpsInvitation -> {
            PlayerUtil.sendMessagePrefixed(initiator, settings.getAlreadyInvite());
            hasInvitationArray[0] = true;
        });

        requestManager.clearInvalidInvitationsOf(opponent.getUniqueId());

        if(hasInvitationArray[0]) return true;


        //check if opponent already play
        if(rpsGameManager.isPlayerInGame(opponent)){
            PlayerUtil.sendMessagePrefixed(initiator,settings.getAlreadyPlay().replace("{NAME}",args[1]));
            return true;
        }

        // create invitation
        RpsInvitation rpsInvitation = RpsInvitation.builder()
                .initiator(initiator)
                .invitee(opponent)
                .creationTime(LocalDateTime.now())
                .bet(bet)
                .build();

        requestManager.addInvitation(rpsInvitation);
        sendInvitation(opponent,initiator.getName(),String.valueOf(bet));
        PlayerUtil.sendMessagePrefixed(initiator,settings.getSuccessfullyInvite().replace("{NAME}",opponent.getName()));

        return false;
    }

    private void sendInvitation(Player enemyPlayer,String initiator, String bet){
        String commandAccept = "/rps accept " + initiator;
        TextComponent acceptButton = new TextComponent(settings.getRpsInviteAcceptButton());
        acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,commandAccept));
        acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("✔").color(ChatColor.WHITE).create()));
        // FIXME HoverEvent depraecated
        PlayerUtil.sendMessagePrefixed(enemyPlayer,
                settings.getRpsInvite()
                        .replace("{NAME}", initiator).replace("{BET}", bet));
        enemyPlayer.spigot().sendMessage(acceptButton);

    }

    @Override
    public String getName() {
        return "game";
    }
}