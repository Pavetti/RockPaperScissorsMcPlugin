package pl.pavetti.rockpaperscissors.game.model;


import lombok.Getter;
import pl.pavetti.rockpaperscissors.config.Settings;

public enum Choice {
    ROCK(Settings.getInstance().getGuiRockName())
    ,PAPER(Settings.getInstance().getGuiPaperName())
    ,SCISSORS(Settings.getInstance().getGuiScissorsName());

    @Getter
    private final String name;

    Choice(String name) {
        this.name = name;
    }
}
