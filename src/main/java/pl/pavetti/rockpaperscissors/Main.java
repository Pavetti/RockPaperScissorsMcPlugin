package pl.pavetti.rockpaperscissors;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import pl.pavetti.rockpaperscissors.commands.RpsCommand;
import pl.pavetti.rockpaperscissors.listener.InventoryClickListener;
import pl.pavetti.rockpaperscissors.listener.InventoryCloseListener;
import pl.pavetti.rockpaperscissors.waitingroom.WaitingRoomManager;

@Getter
public final class Main extends JavaPlugin {
    @Getter
    private static Main instance;
    private Economy economy;
    private WaitingRoomManager waitingRoomManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        instance = this;
        initConfiguration();
        initMangers();
        registerListener();
        registerCommand();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void initConfiguration() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
    private void initMangers(){
        waitingRoomManager = new WaitingRoomManager();
    }

    private void registerListener(){
        getServer().getPluginManager().registerEvents(new InventoryClickListener(),this);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(),this);
    }

    private void registerCommand(){
        this.getCommand("rps").setExecutor(new RpsCommand(economy,waitingRoomManager));
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

}

