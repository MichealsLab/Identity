package it.myke.identity.api;

import it.myke.identity.utils.config.CustomConfigsInit;
import it.myke.identity.obj.Person;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class APIManager extends APIMethods {
    private final CustomConfigsInit customConfigsInit;

    public APIManager(final CustomConfigsInit customConfigsInit) {
        this.customConfigsInit = customConfigsInit;
    }




    /**
     * If the player's data exists, return a new Person object with the player's name, gender, and age.
     *
     * @param playerName The Username of the player you want to get the identity of.
     * @return A Person object.
     */
    @Deprecated
    @Override
    public Person getUserIdentity(String playerName) {
        FileConfiguration data = customConfigsInit.getDataConfig();

        if(!data.isConfigurationSection("data." + Bukkit.getOfflinePlayer(playerName).getUniqueId())) {
            return null;
        }

        ConfigurationSection playerSection = data.getConfigurationSection("data." + Bukkit.getOfflinePlayer(playerName).getUniqueId());
        String name = playerSection.getString("name");
        String gender = playerSection.getString("gender");
        int age = playerSection.getInt("age");

        return new Person(name, gender, age);
    }





    /**
     * If the player's data exists, return a new Person object with the player's name, gender, and age.
     *
     * @param playerUUID The UUID of the player you want to get the identity of.
     * @return A Person object.
     */
    @Override
    public Person getUserIdentity(UUID playerUUID) {
        FileConfiguration data = customConfigsInit.getDataConfig();

        if(!data.isConfigurationSection("data." + playerUUID.toString())) {
            return null;
        }

        ConfigurationSection playerSection = data.getConfigurationSection("data." + playerUUID);
        String name = playerSection.getString("name");
        String gender = playerSection.getString("gender");
        int age = playerSection.getInt("age");

        return new Person(name, gender, age);
    }
}



abstract class APIMethods {

    public abstract Person getUserIdentity(String playerName);

    public abstract Person getUserIdentity(UUID playerUUID);
}
