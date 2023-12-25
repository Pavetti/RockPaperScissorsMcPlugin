package pl.pavetti.rockpaperscissors.game.model;

import lombok.Getter;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;

@Getter
public class RpsGame {
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
}
