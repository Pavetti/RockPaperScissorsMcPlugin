package pl.pavetti.rockpaperscissors.commands.rpssubcommands;


import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.api.timsixth.SubCommand;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.RequestManager;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.game.gui.findenemygui.FindEnemyGui;
import pl.pavetti.rockpaperscissors.game.model.RpsInvitation;
import pl.pavetti.rockpaperscissors.util.TextUtil;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;

import java.time.LocalDateTime;

public class RpsGameSubCommand implements SubCommand {

    private final Economy economy;
    private final Settings settings;
    private final boolean vault;
    private final RpsGameManager rpsGameManager =  RpsGameManager.getInstance();
    private final RequestManager requestManager;
    private final FindEnemyGui findEnemyGui;

    public RpsGameSubCommand(Economy economy, RequestManager requestManager, boolean vault, FindEnemyGui findEnemyGui) {
        this.findEnemyGui = findEnemyGui;
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

        //check args  arg1 = bet , arg2 = player
        if(!areArgsValid(args,initiator)) return true;

        double bet = Double.parseDouble(args[1]);

        if(args.length == 2){
            //economy check
            if ( economy.getBalance( initiator ) < bet ) {
                PlayerUtil.sendPrefixedMessage( initiator, settings.getNotEnoughMoneyOne() );
                return true;
            }

            findEnemyGui.openGui( initiator,bet );
        }
        else {

            Player opponent = Bukkit.getPlayerExact( args[2] );

            //economy check
            if ( economy.getBalance( initiator ) < bet || economy.getBalance( opponent ) < bet ) {
                PlayerUtil.sendPrefixedMessage( initiator, settings.getNotEnoughMoneyTwo() );
                return true;
            }

            //check if player has blocked invitations
            if ( rpsGameManager.isPlayerBlockingInvitation( opponent.getUniqueId().toString() ) ) {
                PlayerUtil.sendPrefixedMessage( initiator, settings.getBlockedInvitationMessage()
                        , "{NAME}", opponent.getName() );
                return true;
            }

            //check if already invite
            boolean[] hasInvitationArray = {false};
            requestManager.getInvitations( opponent.getUniqueId() ).stream()
                    .filter( requestManager::isInvitationValid )
                    .filter( rpsInvitation -> PlayerUtil.compare( rpsInvitation.getInitiator(), initiator ) )
                    .findFirst().ifPresent( rpsInvitation -> {
                        PlayerUtil.sendPrefixedMessage( initiator, settings.getAlreadyInvite() );
                        hasInvitationArray[0] = true;
                    } );

            requestManager.clearInvalidInvitationsOf( opponent.getUniqueId() );

            if ( hasInvitationArray[0] ) return true;


            //check if opponent already play
            if ( rpsGameManager.isPlayerInGame( opponent ) ) {
                PlayerUtil.sendPrefixedMessage( initiator, settings.getAlreadyPlay(), "{NAME}", args[1] );
                return true;
            }

            // create invitation
            RpsInvitation rpsInvitation = RpsInvitation.builder()
                    .initiator( initiator )
                    .invitee( opponent )
                    .creationTime( LocalDateTime.now() )
                    .bet( bet )
                    .build();

            requestManager.addInvitation( rpsInvitation );
            sendInvitation( opponent, initiator.getName(), String.valueOf( bet ) );
            PlayerUtil.sendPrefixedMessage(
                    initiator, settings.getSuccessfullyInvite(), "{NAME}", opponent.getName() );
        }
        return false;
    }

    private boolean areArgsValid(String[] args, Player player){
        if (args.length < 2) {
            PlayerUtil.sendPrefixedMessage(player, settings.getBadUseRpsGameCmd());
            return false;
        }
        if (!args[1].matches("\\d+(\\.\\d+)?")) {
            //check bet argument
            PlayerUtil.sendPrefixedMessage(player, settings.getBadUseRpsGameCmd());
            return false;
        }
        double bet;
        double max = settings.getMaxBet();
        double min = settings.getMinBet();
        bet = Double.parseDouble(args[1]);
        if(max != 0){
            if(bet > max){
                PlayerUtil.sendPrefixedMessage(player,
                        settings.getBetOutOfRangeMax(), "{MAX}", String.valueOf(max));
                return false;
            }
        }
        if(bet < min){
            PlayerUtil.sendPrefixedMessage(
                    player,settings.getBetOutOfRangeMin(),"{MIN}",String.valueOf(min));
            return false;
        }

        if(args.length > 2){
            if (PlayerUtil.isPlayerOffline(args[2])) {
                PlayerUtil.sendPrefixedMessage(
                        player, settings.getPlayerNotExist(), "{NAME}", args[1]);
                return false;
            }
            Player opponent = Bukkit.getPlayerExact(args[2]);
            //check if opponent is vanished
            if(PlayerUtil.isVanished(opponent)){
                PlayerUtil.sendPrefixedMessage(
                        player,settings.getPlayerNotExist(),"{NAME}",args[1]);
                return false;
            }
            // check if player invite himself
            if (PlayerUtil.compare(opponent,player)) {
                PlayerUtil.sendPrefixedMessage(player, settings.getMyselfInvite());
                return false;
            }
        }
        return true;
    }

    private void sendInvitation(Player enemyPlayer,String initiator, String bet){
        Component acceptButton = TextUtil.formatMessage(
                TextUtil.replacePlaceholders(settings.getRpsInviteAcceptButton(),"{PLAYER}",initiator)
        );
        PlayerUtil.sendPrefixedMessage(enemyPlayer,settings.getRpsInvite(),
                "{NAME}",initiator,
                "{BET}",bet,
                "{EXPIRATION}",String.valueOf(settings.getAcceptTime()));
        Audience audience = PlayerUtil.getAudience(enemyPlayer);
        audience.sendMessage(acceptButton);
    }

    @Override
    public String getName() {
        return "game";
    }
}

