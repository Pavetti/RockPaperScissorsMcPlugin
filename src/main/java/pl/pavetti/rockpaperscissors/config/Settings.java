package pl.pavetti.rockpaperscissors.config;

import lombok.Getter;
import org.bukkit.Material;
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

    //gui
    private String guiMainTitle;
    private String guiMainFillItemName;
    private Material guiMainFillItem;
    private String guiRockName;
    private Material guiRockItem;
    private String guiPaperName;
    private Material guiPaperItem;
    private String guiScissorsName;
    private Material guiScissorsItem;
    //messages
    private List<String> descriptionCommand;
    private List<String> globalGameResultMessage;
    private String noPermission;
    private String badUseRpsGameCmd;
    private String badUseRpsAcceptCmd;
    private String myselfInvite;
    private String notEnoughMoney;
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

        //basic
        prefix = configuration.getString("settings.prefix");
        //gameConfig
        replayOnDraw = configuration.getBoolean("settings.gameConfig.replayOnDraw");
        maxBet = configuration.getDouble("settings.gameConfig.maxBet");
        minBet = configuration.getDouble("settings.gameConfig.minBet");
        acceptTime = configuration.getInt("settings.gameConfig.acceptTime");
        chooseTime = configuration.getInt("settings.gameConfig.chooseTime");
        //global game result
        globalGameResultEnable = configuration.getBoolean("settings.gameConfig.globalGameResult.enable");
        globalGameResultMinBet = configuration.getDouble("settings.gameConfig.globalGameResult.minBet");
        //gui
        guiMainTitle = configuration.getString("settings.gui.main.title");
        guiMainFillItem = ItemUtil.getMaterialOf(configuration.getString("settings.gui.main.fillItem.item"));
        guiMainFillItemName = configuration.getString("settings.gui.main.fillItem.name");
        guiRockItem = ItemUtil.getMaterialOf(configuration.getString("settings.gui.main.rock.item"));
        guiRockName = configuration.getString("settings.gui.main.rock.name");
        guiPaperItem = ItemUtil.getMaterialOf(configuration.getString("settings.gui.main.paper.item"));
        guiPaperName = configuration.getString("settings.gui.main.paper.name");
        guiScissorsItem = ItemUtil.getMaterialOf(configuration.getString("settings.gui.main.scissors.item"));
        guiScissorsName = configuration.getString("settings.gui.main.scissors.name");

        //messages
        descriptionCommand = configuration.getStringList("settings.messages.descriptionCommand");
        globalGameResultMessage = configuration.getStringList("settings.messages.globalGameResult");
        noPermission = configuration.getString("settings.messages.noPermission");
        badUseRpsGameCmd = configuration.getString("settings.messages.badUseRpsGameCmd");
        badUseRpsAcceptCmd = configuration.getString("settings.messages.badUseRpsAcceptCmd");
        myselfInvite = configuration.getString("settings.messages.myselfInvite");
        notEnoughMoney = configuration.getString("settings.messages.notEnoughMoney");
        betOutOfRangeMax = configuration.getString("settings.messages.betOutOfRangeMax");
        betOutOfRangeMin = configuration.getString("settings.messages.betOutOfRangeMin");
        playerNotExist = configuration.getString("settings.messages.playerNotExist");
        rpsInvite = configuration.getString("settings.messages.rpsInvite");
        noInvitation = configuration.getString("settings.messages.noInvitation");
        cmdPerformWhileGame = configuration.getString("settings.messages.cmdPerformWhileGame");
        alreadyPlay = configuration.getString("settings.messages.alreadyPlay");
        alreadyInvite = configuration.getString("settings.messages.alreadyInvite");
        winMessage = configuration.getString("settings.messages.winMessage");
        loseMessage = configuration.getString("settings.messages.loseMessage");
        drawNormalMessage = configuration.getString("settings.messages.drawNormalMessage");
        drawReplayMessage = configuration.getString("settings.messages.drawReplayMessage");
        successfullyChoice = configuration.getString("settings.messages.successfullyChoice");
        successfullyInvite = configuration.getString("settings.messages.successfullyInvite");
        successfullyPluginReload = configuration.getString("settings.messages.successfullyPluginReload");
        waitingForOpponent = configuration.getString("settings.messages.waitingForOpponent");
        noVaultDependency = configuration.getString("settings.messages.noVaultDependency");
        blockingInvitationOn = configuration.getString("settings.messages.blockingInvitationOn");
        blockingInvitationOff = configuration.getString("settings.messages.blockingInvitationOff");
        blockedInvitationMessage = configuration.getString("settings.messages.blockedInvitationMessage");
        collectedGameDeposit = configuration.getString("settings.messages.collectedGameDeposit");

        //buttons
        rpsInviteAcceptButton = configuration.getString("settings.chatButtons.rpsInviteAcceptButton");
    }



}
