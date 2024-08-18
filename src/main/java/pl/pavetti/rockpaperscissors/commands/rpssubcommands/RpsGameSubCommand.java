package pl.pavetti.rockpaperscissors.commands.rpssubcommands;


import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.api.timsixth.SubCommand;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.RequestManager;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.game.model.RpsInvitation;
import pl.pavetti.rockpaperscissors.util.ChatUtil;
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
            PlayerUtil.sendPrefixedMessage(initiator, settings.getCmdPerformWhileGame());
            return true;
        }

        //check args
        if (args.length < 3) {
            PlayerUtil.sendPrefixedMessage(initiator, settings.getBadUseRpsGameCmd());
            return true;
        }
        if (!PlayerUtil.isPlayerOnline(args[1])) {
            PlayerUtil.sendPrefixedMessage(
                    initiator, settings.getPlayerNotExist(), "{NAME}", args[1]);
            return true;
        }
        if (!args[2].matches("\\d+(\\.\\d+)?")) {
            //check bet argument
            PlayerUtil.sendPrefixedMessage(initiator, settings.getBadUseRpsGameCmd());
            return true;
        }
        double bet;
        double max = settings.getMaxBet();
        double min = settings.getMinBet();
        Player opponent = Bukkit.getPlayerExact(args[1]);
        bet = Double.parseDouble(args[2]);

        //check if opponent is vanished
        if(PlayerUtil.isVanished(opponent)){
            PlayerUtil.sendPrefixedMessage(
                    initiator,settings.getPlayerNotExist(),"{NAME}",args[1]);
            return true;
        }


        //check if player invite himself
        if (PlayerUtil.compare(opponent,initiator)) {
            PlayerUtil.sendPrefixedMessage(initiator, settings.getMyselfInvite());
            return true;
        }

        //check bet
        if(max != 0){
            if(bet > max){
                PlayerUtil.sendPrefixedMessage(initiator,
                        settings.getBetOutOfRangeMax(), "{MAX}", String.valueOf(max));
                return true;
            }
        }
        if(bet < min){
            PlayerUtil.sendPrefixedMessage(
                    initiator,settings.getBetOutOfRangeMin(),"{MIN}",String.valueOf(min));
            return true;
        }

        //economy check
        if (economy.getBalance(initiator) < bet || economy.getBalance(opponent) < bet) {
            PlayerUtil.sendPrefixedMessage(initiator, settings.getNotEnoughMoney());
            return true;
        }

        //check if player has blocked invitations
        if(rpsGameManager.isPlayerBlockingInvitation(opponent.getUniqueId().toString())){
            PlayerUtil.sendPrefixedMessage(initiator,settings.getBlockedInvitationMessage()
                    ,"{NAME}",opponent.getName());
            return true;
        }

        //check if already invite
        boolean[] hasInvitationArray = {false};
        requestManager.getInvitations(opponent.getUniqueId()).stream()
                .filter(requestManager::isInvitationValid)
                .filter(rpsInvitation -> PlayerUtil.compare(rpsInvitation.getInitiator(), initiator))
                .findFirst().ifPresent(rpsInvitation -> {
            PlayerUtil.sendPrefixedMessage(initiator, settings.getAlreadyInvite());
            hasInvitationArray[0] = true;
        });

        requestManager.clearInvalidInvitationsOf(opponent.getUniqueId());

        if(hasInvitationArray[0]) return true;


        //check if opponent already play
        if(rpsGameManager.isPlayerInGame(opponent)){
            PlayerUtil.sendPrefixedMessage(initiator,settings.getAlreadyPlay(),"{NAME}",args[1]);
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
        PlayerUtil.sendPrefixedMessage(
                initiator,settings.getSuccessfullyInvite(),"{NAME}",opponent.getName());

        return false;
    }

    private void sendInvitation(Player enemyPlayer,String initiator, String bet){
        Component acceptButton = ChatUtil.formatMessage(
                ChatUtil.replacePlaceholders(settings.getRpsInviteAcceptButton(),"{PLAYER}",initiator)
        );
        PlayerUtil.sendPrefixedMessage(enemyPlayer,settings.getRpsInvite(),
                "{NAME}",initiator,
                "{BET}",bet,
                "{EXPIRATION}",String.valueOf(settings.getAcceptTime()));
        enemyPlayer.sendMessage(acceptButton);
    }

    @Override
    public String getName() {
        return "game";
    }
}