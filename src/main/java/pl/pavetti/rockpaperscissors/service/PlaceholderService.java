package pl.pavetti.rockpaperscissors.service;

import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.datatransporter.GameResult;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderService {
    public static List<String> replacePlaceholdersInGlobalResultMessage(GameResult gameResult) {
        List<String> newLines = new ArrayList<>();
        for (String line : Settings.getInstance().getGlobalGameResultMessage()) {
            line = line.replace("{WINNER}", gameResult.getWinner().getPlayer().getName());
            line = line.replace("{LOSER}", gameResult.getLoser().getPlayer().getName());
            line = line.replace("{BET}", String.valueOf(gameResult.getRpsGame().getBet()));
            newLines.add(line);
        }
        return newLines;
    }
}
