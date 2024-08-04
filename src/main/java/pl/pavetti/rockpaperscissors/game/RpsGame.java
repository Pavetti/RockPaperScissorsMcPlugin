package pl.pavetti.rockpaperscissors.game;

import lombok.Getter;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.model.RpsPlayer;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;
import pl.pavetti.rockpaperscissors.waitingroom.model.Waiter;

/**
 * This class represents a Rock Paper Scissors game.
 * It includes the initiator and opponent of the game, and the bet amount.
 */
@Getter
public class RpsGame implements Waiter {
    private final RpsPlayer initiator;
    private final RpsPlayer opponent;
    private final double bet;

    /**
     * Constructor for the RpsGame class.
     * @param initiator the player who initiated the game
     * @param opponent the player who is the opponent in the game
     * @param bet the bet amount for the game
     */
    public RpsGame(Player initiator, Player opponent, double bet) {
        this.bet = bet;
        this.initiator = new RpsPlayer(initiator,this);
        this.opponent = new RpsPlayer(opponent,this);
    }

    /**
     * Performs actions after a player makes a choice.
     * @param player the player who made the choice
     */
    public void doActionsPostChoose(RpsPlayer player){
        if(checksIfThatWasFirstChoiceMade()) doActionsPostFirstChoose(player);
        else doActionsPostSecondChoice(player);
    }

    /**
     * Performs actions after the second player makes a choice.
     * @param secondChoosePlayer the player who made the second choice
     */
    private void doActionsPostSecondChoice(RpsPlayer secondChoosePlayer){
        sendAfterChoiceMessage(secondChoosePlayer);
        RpsGameManager.getInstance().endGame(this);
    }

    /**
     * Performs actions after the first player makes a choice.
     * @param firstChoosePlayer the player who made the first choice
     */
    private void doActionsPostFirstChoose(RpsPlayer firstChoosePlayer){
        sendAfterChoiceMessage(firstChoosePlayer);
        PlayerUtil.sendMessagePrefixed(firstChoosePlayer.getPlayer(),
                Settings.getInstance().getWaitingForOpponent());
        RpsGameManager.getInstance().displayTimeForSecondChoicePlayer(this);
    }

    /**
     * Returns the other player in the game.
     * @param rpsPlayer the player to compare
     * @return the other player in the game
     */
    public RpsPlayer getOtherPlayer(RpsPlayer rpsPlayer){
        if(PlayerUtil.compare(rpsPlayer.getPlayer(), initiator.getPlayer())){
            return opponent;
        } else if (PlayerUtil.compare(rpsPlayer.getPlayer(), opponent.getPlayer())) {
            return initiator;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Checks if the first choice was made.
     * @return true if the first choice was made, false otherwise
     */
    private boolean checksIfThatWasFirstChoiceMade(){
        return initiator.getChoice() == null && opponent.getChoice() != null
                || initiator.getChoice() != null && opponent.getChoice() == null;
    }

    /**
     * Sends a message after a player makes a choice.
     * @param rpsPlayer the player who made the choice
     */
    private void sendAfterChoiceMessage(RpsPlayer rpsPlayer){
        PlayerUtil.sendMessagePrefixed(rpsPlayer.getPlayer(),
                Settings.getInstance().getSuccessfullyChoice()
                        .replace("{CHOICE}",rpsPlayer.getChoice().getName()));
    }

    /**
     * Returns the instance of the game.
     * @return the instance of the game
     */
    @Override
    public Object getInstance() {
        return this;
    }
}