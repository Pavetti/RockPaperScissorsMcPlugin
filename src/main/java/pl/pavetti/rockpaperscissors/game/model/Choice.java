package pl.pavetti.rockpaperscissors.game.model;


import lombok.Getter;
import pl.pavetti.rockpaperscissors.Main;
import pl.pavetti.rockpaperscissors.config.Settings;
@Getter
public enum Choice {
    ROCK( Main.getInstance().getGuiConfig().getGuiGameRockName())
    ,PAPER(Main.getInstance().getGuiConfig().getGuiGamePaperName())
    ,SCISSORS(Main.getInstance().getGuiConfig().getGuiGameScissorsName());

    private final String name;

    Choice(String name) {
        this.name = name;
    }
}
