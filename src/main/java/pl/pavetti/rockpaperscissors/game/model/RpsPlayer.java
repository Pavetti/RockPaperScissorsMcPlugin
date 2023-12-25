package pl.pavetti.rockpaperscissors.game.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.waitingroom.model.Waiter;

@Getter
@RequiredArgsConstructor
public class RpsPlayer implements Waiter {
    private final Player player;
    private final RpsGame rpsGame;
    private Choice choice;



    public void choose(Choice choice){
        this.choice = choice;
        rpsGame.tryEnd();
    }

    @Override
    public Object getInstance() {
        return this;
    }
}
