package it.myke.identity.utils.config;

import it.myke.identity.Identity;
import it.myke.identity.utils.FormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigLoader {
    public static boolean name,age,gender, invName_onlyName,invName_titleBar_enabled, updatesShown;
    public static String message_stepCompleted, message_minAgeReached, message_maxAgeReached, message_ageConfirmed, message_removedIdentityRejoin, message_noPerms, message_playerRemovedSuccessfully;
    public static int min_age,max_age,menu_delay;
    public static String invName_name,invAge_name,invGender_name, message_HeadClicked_name_ChatMessage,message_title_Title,message_title_Subtitle,message_surname_needed,message_short_surname_needed,message_name_set,message_insert_name,message_name;
    public static String message_femaleClicked, message_maleClicked, message_playerNotRegistered, message_male, message_female, message_gender_not_valid, message_name_not_valid, message_name_not_enabled, message_age_not_enabled, message_gender_not_enabled;
    public static InventoryTypes invName_Type, invAge_Type;
    public static String message_already_have_identity, message_nonBinaryClicked, message_non_binary, message_other_name_set, message_other_age_set, message_other_gender_set;
    public static String message_identity_aint_set, message_modified_age, message_modified_name, message_modified_gender;

    public ConfigLoader(final Identity identity, final CustomConfigsInit customConfigsInit) {
        FileConfiguration lang = customConfigsInit.getLangConfig();

        try {
            updatesShown = identity.getConfig().getBoolean("updates.menu-shown");
            name = identity.getConfig().getBoolean("options.name");
            age = identity.getConfig().getBoolean("options.age");
            gender = identity.getConfig().getBoolean("options.gender");

            invName_onlyName = identity.getConfig().getBoolean("inventories.name.onlyName");
            invName_titleBar_enabled = identity.getConfig().getBoolean("inventories.name.action-element.onClick.titleBar.enabled");

            invName_Type = InventoryTypes.valueOf(identity.getConfig().getString("inventories.name.type").toUpperCase());
            invName_name = FormatUtils.color(identity.getConfig().getString("inventories.name.title"));
            invGender_name = FormatUtils.color(identity.getConfig().getString("inventories.gender.title"));
            invAge_name = FormatUtils.color(identity.getConfig().getString("inventories.age.title"));
            invAge_Type = InventoryTypes.valueOf(identity.getConfig().getString("inventories.age.type").toUpperCase());
            min_age = identity.getConfig().getInt("inventories.age.options.min-age");
            max_age = identity.getConfig().getInt("inventories.age.options.max-age");
            menu_delay = identity.getConfig().getInt("inventories.onJoin.menu-delay");
            message_already_have_identity = FormatUtils.color(lang.getString("messages.already-have-identity"));
            message_other_age_set = FormatUtils.color(lang.getString("messages.other-age-set"));
            message_other_name_set = FormatUtils.color(lang.getString("messages.other-name-set"));
            message_other_gender_set = FormatUtils.color(lang.getString("messages.other-gender-set"));
            message_identity_aint_set = FormatUtils.color(lang.getString("messages.identity-aint-set"));
            message_modified_age = FormatUtils.color(lang.getString("messages.modified-age"));
            message_modified_name = FormatUtils.color(lang.getString("messages.modified-name"));
            message_modified_gender = FormatUtils.color(lang.getString("messages.modified-gender"));

            //Message -- Commands
            message_removedIdentityRejoin = FormatUtils.color(lang.getString("messages.identity-reset-rejoin"));
            message_noPerms = FormatUtils.color(lang.getString("messages.no-perms"));

            //Message -- Process
            message_stepCompleted = FormatUtils.color(lang.getString("messages.process-finished"));
            message_playerNotRegistered = FormatUtils.color(lang.getString("messages.player-not-registered"));

            //Message -- Age
            message_maxAgeReached = FormatUtils.color(lang.getString("messages.max-age-reached"));
            message_minAgeReached = FormatUtils.color(lang.getString("messages.min-age-reached"));
            message_ageConfirmed = FormatUtils.color(lang.getString("messages.age-confirmed"));
            message_playerRemovedSuccessfully = FormatUtils.color(lang.getString("messages.player-removed-successfully"));


            //Messages -- name
            message_HeadClicked_name_ChatMessage = FormatUtils.color(identity.getConfig().getString("inventories.name.action-element.onClick.type-name-in-chat"));
            message_title_Title = FormatUtils.color(identity.getConfig().getString("inventories.name.action-element.onClick.titleBar.title"));
            message_title_Subtitle = FormatUtils.color(identity.getConfig().getString("inventories.name.action-element.onClick.titleBar.subtitle"));
            message_surname_needed = FormatUtils.color(identity.getConfig().getString("inventories.name.action-element.onClick.surname-needed"));
            message_name_set = FormatUtils.color(lang.getString("messages.name-set"));
            message_short_surname_needed = FormatUtils.color(lang.getString("messages.short-surname-needed"));
            message_insert_name = FormatUtils.color(identity.getConfig().getString("inventories.name.anvil-gui.title"));
            message_name = FormatUtils.color(lang.getString("messages.name"));

            //Messages -- Gender
            message_femaleClicked = FormatUtils.color(lang.getString("messages.female-selected"));
            message_maleClicked = FormatUtils.color(lang.getString("messages.male-selected"));
            message_male = FormatUtils.color(lang.getString("messages.male"));
            message_female = FormatUtils.color(lang.getString("messages.female"));
            message_gender_not_valid = FormatUtils.color(lang.getString("messages.gender-not-valid"));
            message_name_not_valid = FormatUtils.color(lang.getString("messages.name-not-valid"));
            message_age_not_enabled = FormatUtils.color(lang.getString("messages.age-not-enabled"));
            message_name_not_enabled = FormatUtils.color(lang.getString("messages.name-not-enabled"));
            message_gender_not_enabled = FormatUtils.color(lang.getString("messages.gender-not-enabled"));
            message_nonBinaryClicked = FormatUtils.color(lang.getString("messages.nonbinary-selected"));
            message_non_binary = FormatUtils.color(lang.getString("messages.nonbinary"));

        } catch (Exception e) {
            Bukkit.getLogger().severe("Error while loading config.yml and lang.yml. Some configuration lines are missing, delete the configurations and restart the server.");
            e.printStackTrace();
            identity.getPluginLoader().disablePlugin(identity);
        }

    }

    public enum InventoryTypes {
            CHEST,
            ANVIL,
            FURNACE
    }

}
