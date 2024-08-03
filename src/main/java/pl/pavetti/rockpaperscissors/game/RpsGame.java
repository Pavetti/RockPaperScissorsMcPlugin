package pl.pavetti.rockpaperscissors.game;

import lombok.Getter;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.model.RpsPlayer;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;
import pl.pavetti.rockpaperscissors.waitingroom.model.Waiter;

/**
 * Represents a Rock-Paper-Scissors game between two players.
 */
@Getter
public class RpsGame implements Waiter {
    private final RpsPlayer initiator;
    private final RpsPlayer opponent;
    private final double bet;
    private boolean isStarted;

    /**
     * Constructs a new RpsGame with the given initiator, opponent, and bet.
     *
     * @param initiator the player who initiated the game
     * @param opponent the player who is the opponent in the game
     * @param bet the amount of the bet for the game
     */
    public RpsGame(Player initiator, Player opponent, double bet) {
        isStarted = false;
        this.bet = bet;
        this.initiator = new RpsPlayer(initiator,this);
        this.opponent = new RpsPlayer(opponent,this);
    }

    /**
     * Performs actions after the player has made their choice.
     *
     * @param player the player who made the choice
     */
    public void doActionsPostChoose(RpsPlayer player){
        if(checksIfThatWasFirstChoiceMade()) doActionsPostFirstChoose(player);
        else doActionsPostSecondChoice(player);
    }

    /**
     * Sends a message and ends the game.
     *
     * @param secondChoosePlayer the player who made the second choice
     */
    private void doActionsPostSecondChoice(RpsPlayer secondChoosePlayer){
        sendAfterChoiceMessage(secondChoosePlayer);
        RpsGameManager.getInstance().endGame(this);
    }

    /**
     * Sends a message and starts the timer to end the game after
     * the first player has made their choice.
     *
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
     *
     * @param rpsPlayer the player to get the opponent of
     * @return the other player in the game
     * @throws IllegalArgumentException if the provided player is not part of the game
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
     * Checks if the first choice was made in the game.
     *
     * @return false if no players have made their choices, true otherwise
     */
    private boolean checksIfThatWasFirstChoiceMade(){
        return initiator.getChoice() == null && opponent.getChoice() != null
                || initiator.getChoice() != null && opponent.getChoice() == null;
    }

    /**
     * Sends a message to the provided player after they have made their choice.
     *
     * @param rpsPlayer the player to send the message to
     */
    private void sendAfterChoiceMessage(RpsPlayer rpsPlayer){
        PlayerUtil.sendMessagePrefixed(rpsPlayer.getPlayer(),
                Settings.getInstance().getSuccessfullyChoice()
                        .replace("{CHOICE}",rpsPlayer.getChoice().getName()));
    }

    /**
     * Starts the game.
     */
    public void start(){
        isStarted = true;
    }

    /**
     * Returns the current instance of the RpsGame class.
     *
     * @return the current instance of the RpsGame class
     */
    @Override
    public Object getInstance() {
        return this;
    }
}