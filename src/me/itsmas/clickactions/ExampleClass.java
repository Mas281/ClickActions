package me.itsmas.clickactions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ExampleClass extends JavaPlugin {

    public void onEnable()
    {
        // Init with your own plugin instance in your onEnable
        ClickActions.init(this);
    }

    public void example()
    {
        Player player = Bukkit.getPlayer("Notch");

        // Example: Traps the player in a bedrock structure when they click the message
        // Will expire after being used once
        ClickActions.getInstance().sendActionMessage(player, "Click here for magical things to happen!", true, aPlayer ->
        {
            Location location = aPlayer.getLocation();

            location.getBlock().getRelative(BlockFace.DOWN).setType(Material.BEDROCK);

            location.getBlock().getRelative(BlockFace.NORTH).setType(Material.BEDROCK);
            location.getBlock().getRelative(BlockFace.EAST).setType(Material.BEDROCK);
            location.getBlock().getRelative(BlockFace.SOUTH).setType(Material.BEDROCK);
            location.getBlock().getRelative(BlockFace.WEST).setType(Material.BEDROCK);

            location.clone().add(0, 2, 0).getBlock().setType(Material.BEDROCK);
        });

        // Removes all the player's click actions
        ClickActions.getInstance().removeActionMessages(player);
    }
}
