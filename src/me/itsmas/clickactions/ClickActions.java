package me.itsmas.clickactions;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class to send json messages to players
 * which can execute an action when clicked
 *
 * @author Mas281
 * @version 1.0
 * @since 3/04/2017
 */
public class ClickActions implements Listener {

    /**
     * Private constructor
     * No new instances of the class
     * are needed to be created outside
     */
    private ClickActions()
    {
        actionMap = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * The instance of the main class
     */
    private static Plugin plugin;

    /**
     * Sets the instance of the plugin
     * @param pluginInstance The plugin instance
     */
    public static void init(Plugin pluginInstance)
    {
        plugin = pluginInstance;
    }

    /**
     * The singleton instance of the class
     */
    private static final ClickActions instance = new ClickActions();

    /**
     * Gets the instance of the class
     * @return The class instance
     */
    public static ClickActions getInstance()
    {
        return instance;
    }

    ////////////////////////////////////////////////////

    /**
     * Functional interface for
     * executing actions with
     * a player
     */
    @FunctionalInterface
    public interface PlayerAction
    {
        /**
         * Executes the desired action
         * on a player based upon implementation
         * @param player The player to run the action for
         */
        void run(Player player);
    }

    /**
     * Class holding information about an action
     */
    private class ActionData
    {
        /**
         * The player to execute the action on
         */
        private UUID playerId;

        /**
         * The action to execute
         */
        private PlayerAction action;

        /**
         * ActionData constructor
         * @param playerId The {@link UUID} of the player to execute the action on
         * @param action The {@link PlayerAction} to execute
         */
        private ActionData(UUID playerId, PlayerAction action)
        {
            this.playerId = playerId;
            this.action = action;
        }

        /**
         * Gets the UUID of the player to execute the action upon
         * @return The stored {@link UUID}
         */
        private UUID getPlayerId()
        {
            return playerId;
        }

        /**
         * Gets the action associated with this ActionData object
         * @return The stored {@link PlayerAction}
         */
        private PlayerAction getAction()
        {
            return action;
        }
    }

    /**
     * Map linking an action UUID and the action
     */
    private Map<UUID, ActionData> actionMap;

    /**
     * Sends a clickable action message to a player
     * @param player The player to send the message to
     * @param msg The message to send to the player
     * @param action The action to execute when the player clicks the message
     */
    public void sendActionMessage(Player player, String msg, PlayerAction action)
    {
        sendActionMessage(player, new TextComponent(msg), action);
    }

    /**
     * Sends a clickable action message to a player
     * @param player The player to send the message to
     * @param component The text component to send to the player
     * @param action The action to execute when the player clicks the message
     */
    public void sendActionMessage(Player player, TextComponent component, PlayerAction action)
    {
        sendActionMessage(player, new TextComponent[] {component}, action);
    }

    /**
     * Sends clickable action messages to a player
     * @param player The player to send the message to
     * @param components The text components to send to the player
     * @param action The action to execute when the player clicks the message
     */
    public void sendActionMessage(Player player, TextComponent[] components, PlayerAction action)
    {
        Validate.notNull(player, "Player cannot be null");
        Validate.notNull(components, "Components cannot be null");
        Validate.notNull(action, "Action cannot be null");

        UUID id = UUID.randomUUID();

        while (actionMap.keySet().contains(id))
        {
            id = UUID.randomUUID();
        }

        actionMap.put(id, new ActionData(player.getUniqueId(), action));

        for (BaseComponent component : components)
        {
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + id.toString()));
        }

        player.spigot().sendMessage(components);
    }

    /* Listeners */
    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        // Remove all click actions for
        // a player when they log out
        for (Map.Entry<UUID, ActionData> entry : actionMap.entrySet())
        {
            if (entry.getValue().getPlayerId().equals(event.getPlayer().getUniqueId()))
            {
                actionMap.remove(entry.getKey());
                return;
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event)
    {
        // The command entered
        String command = event.getMessage().split(" ")[0].substring(1);

        UUID id;

        try
        {
            id = UUID.fromString(command);
        }
        catch (IllegalArgumentException expected)
        {
            // They didn't enter a valid UUID
            return;
        }

        // The data associated with the UUID they entered
        ActionData data = actionMap.get(id);

        if (data == null)
        {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();

        if (player.getUniqueId().equals(data.getPlayerId()))
        {
            // They entered a command linked with their data
            data.getAction().run(player);
        }
    }
}
