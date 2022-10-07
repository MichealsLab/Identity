package it.myke.identity.inventories;

import com.cryptomorin.xseries.XSound;
import it.myke.identity.Identity;
import it.myke.identity.obj.Person;
import it.myke.identity.utils.PersonUtil;
import it.myke.identity.utils.config.ConfigLoader;
import it.myke.identity.utils.config.CustomConfigsInit;
import it.myke.identity.utils.inventory.GenderCommands;
import it.myke.identity.utils.postprocess.PostProcessCommands;
import org.bukkit.entity.Player;

public class InventoryManager {



    public String getNextInventory(Player player, PersonUtil personUtil) {
        if(personUtil.isPerson(player.getUniqueId())) {
            Person playerIdentity = personUtil.getPerson(player.getUniqueId());
            if(playerIdentity.getName() == null && ConfigLoader.name) {
                return "name";
            } else if(playerIdentity.getGender() == null && ConfigLoader.gender) {
                return "gender";
            } else if(playerIdentity.getAge() == 0 && ConfigLoader.age) {
                return "age";
            }
        }
        return null;
    }


    /**
     * This is needed because you can decide to disable a feature of this plugin, so that become necessary
     * to get the new step automatically.
     * @param player Player to execute the openInventory action
     */

    public void openNextInventory(Player player, Identity plugin, PersonUtil personUtil, Inventories inventories, PostProcessCommands postProcessCommands, CustomConfigsInit customConfigsInit, boolean setup) {
        if (setup) {
            String nxtInventory = getNextInventory(player, personUtil);
            if (nxtInventory == null) {
                player.sendMessage(ConfigLoader.message_stepCompleted);
                customConfigsInit.saveInConfig(player.getUniqueId(), personUtil);
                String gender = personUtil.getPerson(player.getUniqueId()).getGender();
                personUtil.removePerson(player.getUniqueId());
                player.closeInventory();

                //Post setup commands
                new GenderCommands(plugin.getConfig(), gender, player);
                postProcessCommands.start(player);

                XSound.play(player, "ENTITY_EXPERIENCE_ORB_PICKUP");
            } else {
                player.closeInventory();
                switch (nxtInventory) {
                    case "name":
                        inventories.openGUI(InventoryType.NAME, ConfigLoader.invName_Type, plugin, player, customConfigsInit, personUtil, postProcessCommands, setup);
                        break;

                    case "gender":
                        inventories.openGUI(InventoryType.GENDER, ConfigLoader.InventoryTypes.CHEST, plugin, player, customConfigsInit, personUtil, postProcessCommands, setup);
                        break;

                    case "age":
                        inventories.openGUI(InventoryType.AGE, ConfigLoader.invAge_Type, plugin, player, customConfigsInit, personUtil, postProcessCommands, setup);
                        break;
                }

                XSound.play(player, "BLOCK_NOTE_BLOCK_PLING");
            }
        }
    }


}
