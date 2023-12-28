package pl.pavetti.rockpaperscissors.game.model;

import lombok.Getter;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;
import pl.pavetti.rockpaperscissors.waitingroom.model.Waiter;

@Getter
public class RpsGame implements Waiter {
    private final RpsPlayer initiator;
    private final RpsPlayer opponent;
    private final int bet;

    public RpsGame(Player initiator, Player oponent, int bet) {
        this.bet = bet;
        this.initiator = new RpsPlayer(initiator,this);
        this.opponent = new RpsPlayer(oponent,this);
    }

    public void tryEnd(){
        if(initiator.getChoice() != null && opponent.getChoice() != null){
            RpsGameManager.getInstance().endGame(this);
        }
    }

    public void tryStartTimeToEnd(){
        if(!(initiator.getChoice() != null && opponent.getChoice() != null)){
            RpsGameManager.getInstance().startTimeToEnd(this);
        }
    }

    public RpsPlayer getOtherPlayer(RpsPlayer rpsPlayer){
        if(PlayerUtil.compare(rpsPlayer.getPlayer(), initiator.getPlayer())){
            return opponent;
        } else if (PlayerUtil.compare(rpsPlayer.getPlayer(), opponent.getPlayer())) {
            return initiator;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Object getInstance() {
        return this;
    }
}
