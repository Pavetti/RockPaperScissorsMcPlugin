package pl.pavetti.rockpaperscissors.config;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import pl.pavetti.rockpaperscissors.Main;
import pl.pavetti.rockpaperscissors.util.ItemUtil;

import java.util.List;

@Getter
public class  Settings {

    private static Settings instance;

    //basic
    private String prefix;
    //game config
    private boolean replayOnDraw;
    private double maxBet;
    private double minBet;
    private int acceptTime;
    private int chooseTime;
    //global game result
    private boolean globalGameResultEnable;
    private double globalGameResultMinBet;


    //messages
    private List<String> descriptionCommand;
    private List<String> globalGameResultMessage;
    private String noPermission;
    private String badUseRpsGameCmd;
    private String badUseRpsAcceptCmd;
    private String myselfInvite;
    private String notEnoughMoneyOne;
    private String notEnoughMoneyTwo;
    private String betOutOfRangeMax;
    private String betOutOfRangeMin;
    private String playerNotExist;
    private String rpsInvite;
    private String noInvitation;
    private String cmdPerformWhileGame;
    private String alreadyPlay;
    private String alreadyInvite;
    private String winMessage;
    private String loseMessage;
    private String drawNormalMessage;
    private String drawReplayMessage;
    private String successfullyChoice;
    private String successfullyInvite;
    private String successfullyPluginReload;
    private String waitingForOpponent;
    private String noVaultDependency;
    private String blockingInvitationOn;
    private String blockingInvitationOff;
    private String blockedInvitationMessage;
    private String collectedGameDeposit;
    //buttons
    private String rpsInviteAcceptButton;

    private Settings() {
        load();
    }

    public static Settings getInstance(){
        if(instance == null){
            instance = new Settings();
        }
        return instance;
    }

    public Object getByPath(String path){
        return Main.getInstance().getConfig().get(path);
    }

    public void load(){
        FileConfiguration configuration = Main.getInstance().getConfig();
        ConfigurationSection settings = configuration.getConfigurationSection("settings");
        assert settings != null : "settings section in config.yml is missing";
        ConfigurationSection gameConfig = settings.getConfigurationSection("gameConfig");
        ConfigurationSection mainGui = settings.getConfigurationSection("gui.main");
        ConfigurationSection messages = settings.getConfigurationSection("messages");
        ConfigurationSection chatButtons = settings.getConfigurationSection("chatButtons");

        //basic
        prefix = settings.getString("prefix");
        //gameConfig
        assert gameConfig != null : "gameConfig section in config.yml is missing";
        replayOnDraw = gameConfig.getBoolean("replayOnDraw");
        maxBet = gameConfig.getDouble("maxBet");
        minBet = gameConfig.getDouble("minBet");
        acceptTime = gameConfig.getInt("acceptTime");
        chooseTime = gameConfig.getInt("chooseTime");
        //global game result
        globalGameResultEnable = gameConfig.getBoolean("globalGameResult.enable");
        globalGameResultMinBet = gameConfig.getDouble("globalGameResult.minBet");


        //messages
        assert messages != null : "messages section in config.yml is missing";
        descriptionCommand = messages.getStringList("descriptionCommand");
        globalGameResultMessage = messages.getStringList("globalGameResult");
        noPermission = messages.getString("noPermission");
        badUseRpsGameCmd = messages.getString("badUseRpsGameCmd");
        badUseRpsAcceptCmd = messages.getString("badUseRpsAcceptCmd");
        myselfInvite = messages.getString("myselfInvite");
        notEnoughMoneyOne = messages.getString("notEnoughMoneyOne");
        notEnoughMoneyTwo = messages.getString("notEnoughMoneyTwo");
        betOutOfRangeMax = messages.getString("betOutOfRangeMax");
        betOutOfRangeMin = messages.getString("betOutOfRangeMin");
        playerNotExist = messages.getString("playerNotExist");
        rpsInvite = messages.getString("rpsInvite");
        noInvitation = messages.getString("noInvitation");
        cmdPerformWhileGame = messages.getString("cmdPerformWhileGame");
        alreadyPlay = messages.getString("alreadyPlay");
        alreadyInvite = messages.getString("alreadyInvite");
        winMessage = messages.getString("winMessage");
        loseMessage = messages.getString("loseMessage");
        drawNormalMessage = messages.getString("drawNormalMessage");
        drawReplayMessage = messages.getString("drawReplayMessage");
        successfullyChoice = messages.getString("successfullyChoice");
        successfullyInvite = messages.getString("successfullyInvite");
        successfullyPluginReload = messages.getString("successfullyPluginReload");
        waitingForOpponent = messages.getString("waitingForOpponent");
        noVaultDependency = messages.getString("noVaultDependency");
        blockingInvitationOn = messages.getString("blockingInvitationOn");
        blockingInvitationOff = messages.getString("blockingInvitationOff");
        blockedInvitationMessage = messages.getString("blockedInvitationMessage");
        collectedGameDeposit = messages.getString("collectedGameDeposit");

        //buttons
        assert chatButtons != null : "chatButtons section in config.yml is missing";
        rpsInviteAcceptButton = chatButtons.getString("rpsInviteAcceptButton");
    }



}
