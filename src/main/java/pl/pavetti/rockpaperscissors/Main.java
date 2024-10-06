package pl.pavetti.rockpaperscissors;

import lombok.NonNull;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import pl.pavetti.rockpaperscissors.api.Metrics;
import pl.pavetti.rockpaperscissors.commands.RpsCommand;
import pl.pavetti.rockpaperscissors.commands.RpsReloadCommand;
import pl.pavetti.rockpaperscissors.commands.RpsTabCompleter;
import pl.pavetti.rockpaperscissors.commands.TestCommand;
import pl.pavetti.rockpaperscissors.game.RequestManager;
import pl.pavetti.rockpaperscissors.listener.InventoryClickListener;
import pl.pavetti.rockpaperscissors.listener.InventoryCloseListener;
import pl.pavetti.rockpaperscissors.waitingroom.WaitingRoomManager;
import pl.pavetti.rockpaperscissors.api.UpdateChecker;

@Getter
public final class Main extends JavaPlugin {
    @Getter
    private static Main instance;
    private Economy economy;
    private WaitingRoomManager waitingRoomManager;
    private RequestManager  requestManager;
    private final int resourceID = 118164;
    private final int bStatsPluginID = 22697;
    private boolean vault = true;
    private BukkitAudiences bukkitAdventure;

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (!setupEconomy() ) {
            getLogger().severe(" No Vault dependency found! Plugin wont work correctly." );
            vault = false;
        }

        instance = this;
        this.bukkitAdventure = BukkitAudiences.create(this);
        initConfiguration();
        initMangers();
        registerListener();
        registerCommand();
        registerTabCompleter();
        updateCheck();
        Metrics metrics = new Metrics(this, bStatsPluginID);
    }

    @Override
    public void onDisable() {
        if(this.bukkitAdventure != null) {
            this.bukkitAdventure.close();
            this.bukkitAdventure = null;
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }


    public @NonNull BukkitAudiences adventure() {
        if(this.bukkitAdventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.bukkitAdventure;
    }


    private void initConfiguration() {
        getConfig().options().copyDefaults(true);
        saveConfig();

    }

    private void initMangers(){
        waitingRoomManager = new WaitingRoomManager();
        requestManager = new RequestManager();
    }

    private void registerListener(){
        getServer().getPluginManager().registerEvents(new InventoryClickListener(),this);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(),this);
    }

    private void registerTabCompleter(){
        this.getCommand("rps").setTabCompleter(new RpsTabCompleter());
    }

    private void registerCommand(){
        this.getCommand("rps").setExecutor(new RpsCommand(economy,requestManager,vault));
        this.getCommand("rpsreload").setExecutor(new RpsReloadCommand());
        this.getCommand("rpstest").setExecutor(new TestCommand());
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    private void updateCheck() {
        new UpdateChecker(this, resourceID).getVersion(version -> {
            if (!this.getDescription().getVersion().equals(version)) {
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE +
                        "[RPS] THERE IS A NEW UPDATE AVAILABLE! \n " +
                        " Current version: " + this.getDescription().getVersion() + "\n"+
                        " Recent version:  " + version + "\n" +
                        "https://www.spigotmc.org/resources/rock-paper-scissors-simple-gambling-game.118164/ \n" +
                        "Waring! New version of the plugin may require resetting the configuration file.");
            }
        });
    }
}