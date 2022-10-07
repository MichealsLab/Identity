package it.myke.identity;

import com.google.common.base.Stopwatch;
import it.myke.identity.api.APIManager;
import it.myke.identity.cmds.CommandListener;
import it.myke.identity.inventories.Inventories;
import it.myke.identity.inventories.InventoryType;
import it.myke.identity.listener.*;
import it.myke.identity.metrics.Metrics;
import it.myke.identity.papi.CustomNamesExpansion;
import it.myke.identity.utils.GUIHolder;
import it.myke.identity.utils.PersonUtil;
import it.myke.identity.utils.config.ConfigLoader;
import it.myke.identity.utils.config.CustomConfigsInit;
import it.myke.identity.utils.postprocess.PostProcessCommands;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.ordinate.command.ArgType;
import redempt.ordinate.spigot.SpigotCommandManager;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public final class Identity extends JavaPlugin {

    @Getter private CustomConfigsInit customConfigsInit;
    @Getter private GUIHolder ageHolder,nameHolder,genderHolder;
    private PostProcessCommands postProcessCommands;



    @Override
    public void onEnable() {

        this.saveDefaultConfig();

        this.customConfigsInit = new CustomConfigsInit();
        customConfigsInit.init(this);

        customConfigsInit.initializeCustom("data.yml");
        customConfigsInit.initializeCustom("lang.yml");
        customConfigsInit.initializeCustom("inventories.yml");

        //Add PlaceHolderApi support
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceHolderAPI")) {
            this.getLogger().info("PlaceholderAPI plugin detected. You can use our Expansion! (Name: identity)");
            this.getLogger().warning("Server reloads could break the Placeholders System! Restart only.");
            new CustomNamesExpansion(this).register();
        }

        Bukkit.getLogger().info("========== Identity ==========");

        new ConfigLoader(this, customConfigsInit);

        PersonUtil personUtil = new PersonUtil();

        postProcessCommands = new PostProcessCommands();
        postProcessCommands.init(this);

        this.ageHolder = new GUIHolder(InventoryType.AGE);
        this.nameHolder = new GUIHolder(InventoryType.NAME);
        this.genderHolder = new GUIHolder(InventoryType.GENDER);

        Inventories inventoriesUtil = new Inventories(this, customConfigsInit, personUtil, postProcessCommands, true);

        Stopwatch stopWatch = Stopwatch.createStarted();

        loadListeners(personUtil, inventoriesUtil);
        setupCommandManager(personUtil, inventoriesUtil);

        stopWatch.stop();
        Bukkit.getLogger().info("Enabled in " + stopWatch.elapsed(TimeUnit.MILLISECONDS) + " ms.");
        Bukkit.getLogger().info("=======================================");



        int pluginId = 16268;
        Metrics metrics = new Metrics(this, pluginId);

    }



    @Override
    public void onDisable() {
        customConfigsInit.initializeCustom("data.yml");
        customConfigsInit.initializeCustom("lang.yml");
        customConfigsInit.initializeCustom("inventories.yml");
    }



    /**
     * > This function returns an instance of the APIManager class, which is the class that handles all the API calls
     *
     * @return An instance of the APIManager class.
     */
    public APIManager getAPI() {
        return new APIManager(customConfigsInit);
    }

    private void setupCommandManager(PersonUtil personUtil, Inventories inventoriesUtil) {
        ArgType<CommandSender, OfflinePlayer> removePlayerArgType = new ArgType("offlineplayer", (ctx, s) -> Bukkit.getOfflinePlayer((String) s)).completerStream(c -> Bukkit.getOnlinePlayers().stream().map(Player::getName));

        Properties messages = SpigotCommandManager.getDefaultMessages();
        messages.setProperty("helpFormat", "&#36D057%1 &8- &7%2");
        messages.setProperty("noPermission", ConfigLoader.message_noPerms.replace("%perm%", "%1"));

        SpigotCommandManager cmdManager = SpigotCommandManager.getInstance(this, messages);
        cmdManager.getParser().addArgTypes(removePlayerArgType).setHookTargets(new CommandListener(this, customConfigsInit, personUtil, postProcessCommands, inventoriesUtil)).parse(getResource("command.ordn")).register();
    }


    private void loadListeners(PersonUtil personUtil, Inventories inventoriesUtil) {
        new PlayerJoinEvent(this, postProcessCommands, inventoriesUtil, personUtil, customConfigsInit);
        new PlayerQuitListener(this, personUtil);

        if (ConfigLoader.name) {
            new NameInventoryListener(this, inventoriesUtil, personUtil, postProcessCommands, customConfigsInit);
        }

        if (ConfigLoader.gender) {
            new GenderInventoryListener(this, personUtil, postProcessCommands, customConfigsInit);
        }

        if (ConfigLoader.age) {
            new AgeInventoryListener(this, personUtil, postProcessCommands, customConfigsInit);
        }
    }














}
