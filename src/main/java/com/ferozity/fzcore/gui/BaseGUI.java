package com.ferozity.fzcore.gui;

import com.ferozity.fzcore.FZcore;
import com.ferozity.fzcore.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class BaseGUI implements Listener {

    protected final FZcore plugin;
    protected final Map<UUID, Inventory> openInventories;
    protected final Map<UUID, Map<Integer, Consumer<Player>>> clickActions;

    public BaseGUI(FZcore plugin) {
        this.plugin = plugin;
        this.openInventories = new HashMap<>();
        this.clickActions = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public abstract String getTitle();
    public abstract int getSize();
    public abstract void setItems(Inventory inv, Player player);

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, getSize(), ColorUtil.colorize(getTitle()));
        setItems(inv, player);
        openInventories.put(player.getUniqueId(), inv);
        clickActions.put(player.getUniqueId(), new HashMap<>());
        player.openInventory(inv);
    }

    protected void setItem(Inventory inv, int slot, ItemStack item, Consumer<Player> action) {
        inv.setItem(slot, item);
        UUID playerId = null;
        for (Map.Entry<UUID, Inventory> entry : openInventories.entrySet()) {
            if (entry.getValue().equals(inv)) {
                playerId = entry.getKey();
                break;
            }
        }
        if (playerId != null && action != null) {
            clickActions.get(playerId).put(slot, action);
        }
    }

    protected ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.colorize(name));
            if (lore.length > 0) {
                java.util.List<String> loreList = new java.util.ArrayList<>();
                for (String line : lore) {
                    loreList.add(ColorUtil.colorize(line));
                }
                meta.setLore(loreList);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    protected ItemStack createItem(Material material, String name, java.util.List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.colorize(name));
            if (lore != null && !lore.isEmpty()) {
                java.util.List<String> coloredLore = new java.util.ArrayList<>();
                for (String line : lore) {
                    coloredLore.add(ColorUtil.colorize(line));
                }
                meta.setLore(coloredLore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    protected void fillBorder(Inventory inv, ItemStack borderItem) {
        int size = getSize();
        int rows = size / 9;
        for (int i = 0; i < size; i++) {
            if (i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, borderItem);
            }
        }
    }

    protected void fillRow(Inventory inv, int row, ItemStack item) {
        int start = row * 9;
        for (int i = 0; i < 9; i++) {
            inv.setItem(start + i, item);
        }
    }

    protected void fillColumn(Inventory inv, int column, ItemStack item) {
        int rows = getSize() / 9;
        for (int row = 0; row < rows; row++) {
            inv.setItem(row * 9 + column, item);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        UUID playerId = player.getUniqueId();
        
        if (!openInventories.containsKey(playerId)) return;
        if (!event.getInventory().equals(openInventories.get(playerId))) return;
        
        event.setCancelled(true);
        
        int slot = event.getRawSlot();
        Map<Integer, Consumer<Player>> actions = clickActions.get(playerId);
        if (actions != null && actions.containsKey(slot)) {
            actions.get(slot).accept(player);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        if (openInventories.containsKey(playerId)) {
            openInventories.remove(playerId);
            clickActions.remove(playerId);
        }
    }

    public void closeAll() {
        for (UUID playerId : openInventories.keySet()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.closeInventory();
            }
        }
        openInventories.clear();
        clickActions.clear();
    }
}