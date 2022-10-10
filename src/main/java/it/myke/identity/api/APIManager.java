package it.myke.identity.api;

import it.myke.identity.utils.PersonUtil;
import it.myke.identity.utils.config.CustomConfigsInit;
import it.myke.identity.obj.Person;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

@AllArgsConstructor
public class APIManager extends APIMethods {
    private final CustomConfigsInit customConfigsInit;
    private final PersonUtil personUtil;




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

    @Override
    public void setGender(UUID playerUUID, String gender) {
        personUtil.setGender(playerUUID, customConfigsInit, gender);
    }

    @Override
    public void setName(UUID playerUUID, String name) {
        personUtil.setName(playerUUID, customConfigsInit, name);
    }

    @Override
    public void setAge(UUID playerUUID, int age) {
        personUtil.setAge(playerUUID, customConfigsInit, age);
    }

    @Deprecated
    @Override
    public void setGender(String name, String gender) {
        personUtil.setGender(Bukkit.getOfflinePlayer(name).getUniqueId(), customConfigsInit, gender);
    }

    @Deprecated
    @Override
    public void setName(String player, String name) {
        personUtil.setName(Bukkit.getOfflinePlayer(player).getUniqueId(), customConfigsInit, name);
    }

    @Deprecated
    @Override
    public void setAge(String name, int age) {
        personUtil.setAge(Bukkit.getOfflinePlayer(name).getUniqueId(), customConfigsInit, age);
    }


}



abstract class APIMethods {

    public abstract Person getUserIdentity(String playerName);

    @Deprecated
    public abstract Person getUserIdentity(UUID playerUUID);

    public abstract void setGender(UUID playerUUID, String gender);

    public abstract void setName(UUID playerUUID, String name);

    public abstract void setAge(UUID playerUUID, int age);

    @Deprecated
    public abstract void setGender(String name, String gender);

    @Deprecated
    public abstract void setName(String player, String name);

    @Deprecated
    public abstract void setAge(String name, int age);
}
