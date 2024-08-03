package pl.pavetti.rockpaperscissors.game.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.game.RpsGame;


@Getter
@RequiredArgsConstructor
public class RpsPlayer {
    private final Player player;
    private final RpsGame rpsGame;
    private Choice choice;

    public void choose(Choice choice){
        this.choice = choice;

        rpsGame.doActionsPostChoose(this);
        player.closeInventory();
    }


}
