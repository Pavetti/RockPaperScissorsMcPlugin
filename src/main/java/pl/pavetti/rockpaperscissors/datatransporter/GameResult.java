package pl.pavetti.rockpaperscissors.datatransporter;

import lombok.Builder;
import lombok.Data;
import pl.pavetti.rockpaperscissors.game.RpsGame;
import pl.pavetti.rockpaperscissors.game.model.RpsPlayer;

@Data
@Builder
public class GameResult {
    private final RpsGame rpsGame;
    private final RpsPlayer winner;
    private final RpsPlayer loser;
}
