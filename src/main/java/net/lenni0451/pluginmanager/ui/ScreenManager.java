package net.lenni0451.pluginmanager.ui;

import net.lenni0451.pluginmanager.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScreenManager implements Listener {

    private static final List<InventoryAction> FORBIDDEN_ACTIONS = Arrays.asList(InventoryAction.NOTHING, InventoryAction.MOVE_TO_OTHER_INVENTORY, InventoryAction.COLLECT_TO_CURSOR, InventoryAction.UNKNOWN);

    private final Map<HumanEntity, ScreenHolder> screenHolders = new HashMap<>();

    public ScreenManager() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this::tickScreens, 1, 1);
    }

    public ScreenHolder getScreenHolder(final Player player) {
        return this.screenHolders.get(player);
    }

    public void close() {
        for (ScreenHolder screenHolder : this.screenHolders.values()) screenHolder.closeScreen();
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.screenHolders.put(event.getPlayer(), new ScreenHolder(event.getPlayer()));
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        this.screenHolders.get(event.getPlayer()).closeCurrentScreen();
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        ScreenHolder screenHolder = this.screenHolders.get(event.getWhoClicked());
        if (screenHolder.getCurrentScreen() == null) return;

        Screen screen = screenHolder.getCurrentScreen();
        if (event.getClickedInventory() == screenHolder.getPlayer().getOpenInventory().getBottomInventory()) {
            if (FORBIDDEN_ACTIONS.contains(event.getAction())) {
                event.setCancelled(true);
                return;
            }
        }
        if (event.getClickedInventory() == screenHolder.getPlayer().getOpenInventory().getTopInventory()) {
            event.setCancelled(true);

            int row = event.getSlot() / screen.getColumns();
            int column = event.getSlot() % screen.getColumns();
            if (row < 0 || row >= screen.getRows() || column < 0 || column >= screen.getColumns()) return;

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> screen.inventoryClick(screenHolder, row, column, event.getClick()));
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        ScreenHolder screenHolder = this.screenHolders.get(event.getWhoClicked());
        if (screenHolder.getCurrentScreen() == null) return;

        Screen screen = screenHolder.getCurrentScreen();
        for (Integer slot : event.getRawSlots()) {
            if (slot < screen.getRows() * screen.getColumns()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.screenHolders.remove(event.getPlayer());
    }

    private void tickScreens() {
        for (ScreenHolder screenHolder : this.screenHolders.values()) {
            if (screenHolder.getCurrentScreen() != null) screenHolder.getCurrentScreen().callTick(screenHolder);
        }
    }

}
