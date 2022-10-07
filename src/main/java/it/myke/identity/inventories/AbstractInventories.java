package it.myke.identity.inventories;

import de.themoep.inventorygui.InventoryGui;
import it.myke.identity.Identity;
import it.myke.identity.utils.PersonUtil;
import it.myke.identity.utils.config.ConfigLoader;
import it.myke.identity.utils.config.CustomConfigsInit;
import it.myke.identity.utils.furnace.FurnaceGui;
import it.myke.identity.utils.postprocess.PostProcessCommands;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;

public abstract class AbstractInventories {

    public abstract InventoryGui getNameChestGUI(Identity plugin, CustomConfigsInit customConfigsInit, boolean setup);

    public abstract InventoryGui getGenderChestGUI(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup);

    public abstract InventoryGui getAgeChestGUI(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup);

    public abstract FurnaceGui getAgeFurnaceGUI(Player player, Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup);

    public abstract AnvilGUI.Builder getNameAnvilGUI(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup);

    public abstract void openGUI(InventoryType inventory, ConfigLoader.InventoryTypes type, Identity plugin, Player player, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup);



}
