package it.myke.identity.papi;

import it.myke.identity.Identity;
import it.myke.identity.utils.FormatUtils;
import it.myke.identity.utils.config.ConfigLoader;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class CustomNamesExpansion extends PlaceholderExpansion {
    private final Identity plugin;
    private FileConfiguration data;

    public CustomNamesExpansion(final Identity identity) {
        this.plugin = identity;
        this.data = identity.getCustomConfigsInit().getDataConfig();
    }


    @Override
    public @NotNull String getIdentifier() {
        return "identity";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MikesLab";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.3.5";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }


    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        data = plugin.getCustomConfigsInit().getDataConfig();
        if (player != null && data.getConfigurationSection("data") != null && data.isConfigurationSection("data." + player.getUniqueId())) {
            switch (params) {
                case "name":
                    String name = data.getString("data." + player.getUniqueId() + ".name") + "";
                    if(name.contains(" ") && name.split(" ")[0].length() > 0) {
                        return name.split(" ")[0];
                    } else if(name.length() > 0)
                        return name;

                case "surname":
                    String surname = data.getString("data." + player.getUniqueId() + ".name") + "";
                    if(surname.contains(" ") && surname.split(" ").length > 1 && surname.split(" ")[1].length() > 0) {
                        return surname.split(" ")[1];
                    }
                case "fullname":
                    String fullName = data.getString("data." + player.getUniqueId() + ".name") + "";

                    if(!fullName.equals("")) {
                        return fullName;
                    }
                case "gender":
                    String gender = (data.getString("data." + player.getUniqueId() + ".gender") + "").toLowerCase(Locale.ROOT);
                    if(gender.equals(ConfigLoader.message_non_binary)) {
                        return FormatUtils.color(plugin.getConfig().getString("placeholders.non-binary"));
                    }

                    if(gender.equals(ConfigLoader.message_male)) {
                        return FormatUtils.color(plugin.getConfig().getString("placeholders.male"));
                    }

                    if(gender.equals(ConfigLoader.message_female)) {
                        return FormatUtils.color(plugin.getConfig().getString("placeholders.female"));
                    }
                case "age":
                    int age = data.getInt("data." + player.getUniqueId() + ".age");
                    if(age != 0)
                        return String.valueOf(age);
            }
        }
        return "";
    }
}
