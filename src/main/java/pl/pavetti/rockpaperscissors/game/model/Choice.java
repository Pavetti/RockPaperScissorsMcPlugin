package pl.pavetti.rockpaperscissors.game.model;


import lombok.Getter;
import pl.pavetti.rockpaperscissors.config.Settings;
@Getter
public enum Choice {
    ROCK(Settings.getInstance().getGuiRockName())
    ,PAPER(Settings.getInstance().getGuiPaperName())
    ,SCISSORS(Settings.getInstance().getGuiScissorsName());

    private final String name;

    Choice(String name) {
        this.name = name;
    }
}
