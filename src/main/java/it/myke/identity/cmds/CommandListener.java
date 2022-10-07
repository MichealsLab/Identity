package it.myke.identity.cmds;

import it.myke.identity.Identity;
import it.myke.identity.inventories.Inventories;
import it.myke.identity.inventories.InventoryType;
import it.myke.identity.utils.FormatUtils;
import it.myke.identity.utils.PersonUtil;
import it.myke.identity.utils.config.ConfigLoader;
import it.myke.identity.utils.config.CustomConfigsInit;
import it.myke.identity.utils.postprocess.PostProcessCommands;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import redempt.ordinate.parser.metadata.CommandHook;

import static it.myke.identity.inventories.Inventories.inventories;

public class CommandListener {
    private final Identity main;
    private final FileConfiguration data;
    private final CustomConfigsInit customConfigsInit;
    private final PersonUtil personUtil;
    private final PostProcessCommands postProcessCommands;
    private final Inventories inventoriesUtil;

    public CommandListener(final Identity main, final CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, Inventories inventoriesUtil) {
        this.main = main;
        this.customConfigsInit = customConfigsInit;
        this.data = customConfigsInit.getDataConfig();
        this.personUtil = personUtil;
        this.postProcessCommands = postProcessCommands;
        this.inventoriesUtil = inventoriesUtil;
    }

    @CommandHook("reload")
    public void reload(CommandSender sender) {
        sender.sendMessage(FormatUtils.color("&#00FF42Reloading Configuration files..."));
        main.reloadConfig();
        customConfigsInit.reloadConfigs();

        new ConfigLoader(main, customConfigsInit);
        sender.sendMessage(FormatUtils.color("&#AEFF00Configs reload process completed!"));
    }


    @CommandHook("removePlayer")
    public void removePlayer(CommandSender sender, OfflinePlayer offlinePlayer) {
        if(customConfigsInit.getDataConfig().isConfigurationSection("data."+ offlinePlayer.getUniqueId())) {
            customConfigsInit.getDataConfig().set("data." + offlinePlayer.getUniqueId(), null);
            customConfigsInit.reload("data.yml");

            sender.sendMessage(ConfigLoader.message_playerRemovedSuccessfully.replace("%removed%", offlinePlayer.getName()));
            if(offlinePlayer.isOnline()) {
                offlinePlayer.getPlayer().kickPlayer(ConfigLoader.message_removedIdentityRejoin);
            }
        } else {
            sender.sendMessage(ConfigLoader.message_playerNotRegistered.replace("%removed%", offlinePlayer.getName()));
        }
    }

    @CommandHook("papi")
    public void papi(CommandSender sender) {
        sender.sendMessage(FormatUtils.color("&#00E230Available Placeholders &8» &f%identity_name%&#00E230,&f %identity_surname%&#00E230,&f %identity_fullname%&#00E230,&f %identity_age%&#00E230,&f %identity_gender%"));
    }

    @CommandHook("copyright")
    public void copyright(CommandSender sender) {
        sender.sendMessage(FormatUtils.color("\n&#00E230This Server is running on the " + main.getDescription().getVersion() + " version of Identity! &7(MikesLab creation | www.mikeslab.it)\n"));
    }


    @CommandHook("showupdates")
    public void showupdates(CommandSender sender) {
        inventories.get("update-" + main.getDescription().getVersion().replace(".", "-")).show((HumanEntity) sender);
    }


    @CommandHook("setName")
    public void setName(CommandSender sender, OfflinePlayer target, String firstName, String lastName)  {
        if(!ConfigLoader.name) {
            sender.sendMessage(ConfigLoader.message_name_not_enabled);
            return;
        }

        if(!ConfigLoader.invName_onlyName) {
            if(lastName == null) {
                sender.sendMessage(ConfigLoader.message_surname_needed);
                return;
            }

            String finalName = FormatUtils.firstUppercase(firstName) + " " + FormatUtils.firstUppercase(lastName);
            personUtil.setName(target.getUniqueId(), customConfigsInit, finalName);
            sender.sendMessage(ConfigLoader.message_other_name_set
                    .replace("%name%", finalName)
                    .replace("%player%", target.getName()));

        } else {
            String finalName = FormatUtils.firstUppercase(firstName);
            personUtil.setName(target.getUniqueId(), customConfigsInit, finalName);
            sender.sendMessage(ConfigLoader.message_other_name_set
                    .replace("%name%", finalName)
                    .replace("%player%", target.getName()));
        }
    }

    @CommandHook("setAge")
    public void setAge(CommandSender sender, OfflinePlayer target, int age) {
        if(!ConfigLoader.age) {
            sender.sendMessage(ConfigLoader.message_age_not_enabled);
            return;
        }

        if(age < ConfigLoader.min_age) {
            sender.sendMessage(ConfigLoader.message_minAgeReached.replace("%age%", String.valueOf(age)));
            return;
        }

        if(age > ConfigLoader.max_age) {
            sender.sendMessage(ConfigLoader.message_maxAgeReached.replace("%age%", String.valueOf(age)));
            return;
        }

        personUtil.setAge(target.getUniqueId(), customConfigsInit, age);
        sender.sendMessage(ConfigLoader.message_other_age_set.replace("%age%", String.valueOf(age)).replace("%player%", target.getName()));

    }

    @CommandHook("setGender")
    public void setGender(CommandSender sender, OfflinePlayer target, String gender) {
        if(!ConfigLoader.gender) {
            sender.sendMessage(ConfigLoader.message_gender_not_enabled);
            return;
        }

        final String male = ConfigLoader.message_male;
        final String female = ConfigLoader.message_female;
        final String nonbinary = ConfigLoader.message_non_binary;

        if(gender.equalsIgnoreCase(male)) {
            personUtil.setGender(((Player) sender).getUniqueId(), customConfigsInit, gender);
            sender.sendMessage(ConfigLoader.message_other_gender_set.replace("%player%", target.getName()).replace("%gender%", male));
        } else if(gender.equalsIgnoreCase(female)) {
            personUtil.setGender(((Player) sender).getUniqueId(), customConfigsInit, gender);
            sender.sendMessage(ConfigLoader.message_other_gender_set.replace("%player%", target.getName()).replace("%gender%", female));
        }else if(gender.equalsIgnoreCase(nonbinary)) {
            personUtil.setGender(((Player) sender).getUniqueId(), customConfigsInit, gender);
            sender.sendMessage(ConfigLoader.message_other_gender_set.replace("%player%", target.getName()).replace("%gender%", nonbinary));
        } else sender.sendMessage(ConfigLoader.message_gender_not_valid.replace("%gender%", gender));
    }



    @CommandHook("editGender")
    public void editGender(CommandSender sender) {
        if(!ConfigLoader.gender) {
            sender.sendMessage(ConfigLoader.message_gender_not_enabled);
            return;
        }

        if(!customConfigsInit.getDataConfig().isConfigurationSection("data." + ((Player) sender).getUniqueId())) {
            sender.sendMessage(ConfigLoader.message_identity_aint_set);
            return;
        }

        PersonUtil personUtil = new PersonUtil();
        personUtil.addPerson(((Player) sender).getUniqueId(), 0, null, null);
        inventoriesUtil.openGUI(InventoryType.GENDER, ConfigLoader.InventoryTypes.CHEST, main, ((Player) sender).getPlayer(), customConfigsInit, personUtil, postProcessCommands, false);

    }

    @CommandHook("editAge")
    public void editAge(CommandSender sender) {
        if(!ConfigLoader.age) {
            sender.sendMessage(ConfigLoader.message_gender_not_enabled);
            return;
        }

        if(!customConfigsInit.getDataConfig().isConfigurationSection("data." + ((Player) sender).getUniqueId())) {
            sender.sendMessage(ConfigLoader.message_identity_aint_set);
            return;
        }

        PersonUtil personUtil = new PersonUtil();
        personUtil.addPerson(((Player) sender).getUniqueId(), 0, null, null);
        inventoriesUtil.openGUI(InventoryType.AGE, ConfigLoader.invAge_Type, main, ((Player) sender).getPlayer(), customConfigsInit, personUtil, postProcessCommands, false);

    }

    @CommandHook("editName")
    public void editName(CommandSender sender) {
        if(!ConfigLoader.name) {
            sender.sendMessage(ConfigLoader.message_gender_not_enabled);
            return;
        }

        if(!customConfigsInit.getDataConfig().isConfigurationSection("data." + ((Player) sender).getUniqueId())) {
            sender.sendMessage(ConfigLoader.message_identity_aint_set);
            return;
        }

        PersonUtil personUtil = new PersonUtil();
        personUtil.addPerson(((Player) sender).getUniqueId(), 0, null, null);
        inventoriesUtil.openGUI(InventoryType.NAME, ConfigLoader.invName_Type, main, ((Player) sender).getPlayer(), customConfigsInit, personUtil, postProcessCommands, false);
    }


    @CommandHook("setup")
    public void setup(CommandSender sender) {
        personUtil.setup((Player) sender, customConfigsInit, main, postProcessCommands, inventoriesUtil, true);
    }








}
