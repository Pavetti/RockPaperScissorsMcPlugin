package pl.pavetti.rockpaperscissors.commands.rpssubcommands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.api.timsixth.SubCommand;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.RequestManager;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.game.RpsGame;
import pl.pavetti.rockpaperscissors.game.model.RpsInvitation;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;

import java.util.Optional;

public class RpsAcceptSubCommand implements SubCommand {

    private final Economy economy;
    private final RequestManager requestManager;
    private final boolean vault;
    private final RpsGameManager rpsGameManager =  RpsGameManager.getInstance();

    public RpsAcceptSubCommand(Economy economy,RequestManager requestManager,boolean vault) {
        this.requestManager = requestManager;
        this.economy = economy;
        this.vault = vault;
    }

    @Override
    public boolean executeCommand(CommandSender sender, String[] args) {

        if(!vault) return true;

        Player acceptor = (Player) sender;
        Settings settings = Settings.getInstance();

        //check if acceptor in game
        if(rpsGameManager.isPlayerInGame(acceptor)){
            PlayerUtil.sendPrefixedMessage(acceptor, settings.getCmdPerformWhileGame());
            return true;
        }

        // check args
        if(args.length < 2){
            PlayerUtil.sendPrefixedMessage(acceptor, settings.getBadUseRpsAcceptCmd());
            return true;
        }
        if(PlayerUtil.isPlayerOffline(args[1])){
            PlayerUtil.sendPrefixedMessage(acceptor, settings.getNoInvitation(),"{NAME}",args[1]);
            return true;
        }

        Player initiator = Bukkit.getPlayerExact(args[1]);

        //check if acceptor has invitation from initiator
        Optional<RpsInvitation> invitationOptional = requestManager.getInvitations(acceptor.getUniqueId()).stream() // get all invitations of player (acceptor)
                .filter(requestManager::isInvitationValid)   // check if invitations is valid
                .filter(rpsInvitation -> PlayerUtil.compare(rpsInvitation.getInitiator(), initiator)) // check if invitation is from initiator
                .findFirst();
       if(!invitationOptional.isPresent()){
           PlayerUtil.sendPrefixedMessage(acceptor, settings.getNoInvitation(),"{NAME}",args[1]);
           return true;
       }
       RpsInvitation invitation = invitationOptional.get();
       requestManager.clearInvalidInvitationsOf(acceptor.getUniqueId());

        //check if initiator in game
        if(rpsGameManager.isPlayerInGame(initiator)){
            PlayerUtil.sendPrefixedMessage(acceptor,settings.getAlreadyPlay(),"{NAME}",args[1]);
            return true;
        }

        double bet = invitation.getBet();
        //economy check
        if (economy.getBalance(initiator) < bet || economy.getBalance(acceptor) < bet) {
            PlayerUtil.sendPrefixedMessage(acceptor, settings.getNotEnoughMoney());
            return true;
        }

        //register and start game
        RpsGame rpsGame = new RpsGame(initiator, acceptor, bet);
        rpsGameManager.registerGame(rpsGame);
        rpsGameManager.startGame(rpsGame, false);
        //remove invitation
        requestManager.removeInvitation(invitation);

        return false;
    }


    @Override
    public String getName() {
        return "accept";
    }
}
