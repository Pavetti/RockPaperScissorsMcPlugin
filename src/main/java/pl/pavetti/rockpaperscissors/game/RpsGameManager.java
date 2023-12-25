package pl.pavetti.rockpaperscissors.game;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.Main;
import pl.pavetti.rockpaperscissors.game.model.Choice;
import pl.pavetti.rockpaperscissors.game.model.RpsGame;
import pl.pavetti.rockpaperscissors.game.model.RpsPlayer;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RpsGameManager {
    private static RpsGameManager instance;
    private final Set<RpsGame> activeGames = new HashSet<>();
    private final GameUI gameUI;
    private final Economy economy;

    private RpsGameManager(){
        gameUI = new GameUI();
        economy = Main.getInstance().getEconomy();
    }
    public static RpsGameManager getInstance(){
        if(instance == null){
            instance = new RpsGameManager();
        }
        return instance;
    }

    public void registryGame(RpsGame rpsGame){
        activeGames.add(rpsGame);
    }

    public Optional<RpsPlayer> getRpsPlayer(Player player){
        for (RpsGame activeGame : activeGames) {
            if(PlayerUtil.compare(activeGame.getInitiator().getPlayer(),player)){
                return Optional.of(activeGame.getInitiator());
            } else if (PlayerUtil.compare(activeGame.getOpponent().getPlayer(),player)) {
                return Optional.of(activeGame.getOpponent());
            }
        }
        return Optional.empty();
    }

    public void startGame(RpsGame rpsGame) {
        if(activeGames.contains(rpsGame)){
            rpsGame.getOpponent().getPlayer().openInventory(gameUI.getInventory());
            rpsGame.getInitiator().getPlayer().openInventory(gameUI.getInventory());
        }
    }

    public void endGame(RpsGame rpsGame){
        if(activeGames.contains(rpsGame)){
            RpsPlayer winner = getWinner(rpsGame);
            if(winner != null){
                settleBet(winner);
            }else {
                System.out.println("REMISSSS");
            }
        }
    }

    private RpsPlayer getWinner (RpsGame rpsGame){
        RpsPlayer player1 = rpsGame.getInitiator();
        RpsPlayer player2 = rpsGame.getOpponent();
        Choice choice1 = player1.getChoice();
        Choice choice2 = player2.getChoice();

        if(choice1 == Choice.ROCK && choice2 == Choice.PAPER) return player2;
        else if (choice1 == Choice.ROCK && choice2 == Choice.SCISSORS) return player1;
        else if (choice1 == Choice.PAPER && choice2 == Choice.ROCK) return player1;
        else if (choice1 == Choice.PAPER && choice2 == Choice.SCISSORS) return player2;
        else if (choice1 == Choice.SCISSORS && choice2 == Choice.ROCK) return player2;
        else if (choice1 == Choice.SCISSORS && choice2 == Choice.PAPER) return player1;
            
        return null;
    }

    private void settleBet(RpsPlayer rpsPlayer){
        Player winner = rpsPlayer.getPlayer();
        Player losser;
        RpsGame rpsGame = rpsPlayer.getRpsGame();
        int bet = rpsPlayer.getRpsGame().getBet();

        if(PlayerUtil.compare(rpsGame.getInitiator().getPlayer(),rpsPlayer.getPlayer())){
            losser = rpsGame.getOpponent().getPlayer();
        }else {
            losser = rpsGame.getInitiator().getPlayer();
        }

        economy.withdrawPlayer(losser,bet);
        economy.depositPlayer(winner,bet);
    }
}
