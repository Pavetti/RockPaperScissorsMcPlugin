package pl.pavetti.rockpaperscissors.datatransporter;

import lombok.Builder;
import lombok.Data;
import pl.pavetti.rockpaperscissors.game.model.RpsGame;
import pl.pavetti.rockpaperscissors.game.model.RpsPlayer;

@Data
@Builder
public class GameResult {
    private RpsGame rpsGame;
    private RpsPlayer winner;
    private RpsPlayer loser;
}
