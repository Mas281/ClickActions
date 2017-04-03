package me.itsmas.clickactions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class ExampleClass {

    public void onEnable()
    {
        // Init with your own plugin instance in your onEnable
        ClickActions.init(null);
    }

    public void example()
    {
        Player player = null; // Obviously pass a non-null valid player value

        // Example: Traps the player in a bedrock structure when they click the message
        ClickActions.getInstance().sendActionMessage(player, "Click here for magical things to happen!", aPlayer ->
        {
            Location location = aPlayer.getLocation();

            location.getBlock().getRelative(BlockFace.DOWN).setType(Material.BEDROCK);

            location.getBlock().getRelative(BlockFace.NORTH).setType(Material.BEDROCK);
            location.getBlock().getRelative(BlockFace.EAST).setType(Material.BEDROCK);
            location.getBlock().getRelative(BlockFace.SOUTH).setType(Material.BEDROCK);
            location.getBlock().getRelative(BlockFace.WEST).setType(Material.BEDROCK);

            location.clone().add(0, 2, 0).getBlock().setType(Material.BEDROCK);
        });
    }
}
