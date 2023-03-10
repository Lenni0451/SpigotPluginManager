package net.lenni0451.pluginmanager.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

    public static ItemBuilder of(final Material material) {
        return new ItemBuilder(material);
    }


    private final ItemStack itemStack;

    public ItemBuilder(final Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder name(final String name) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(name);
            this.itemStack.setItemMeta(itemMeta);
        }
        return this;
    }

    public ItemBuilder amount(final int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder glow() {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            this.itemStack.setItemMeta(itemMeta);
        }
        return this;
    }

    public ItemStack get() {
        return this.itemStack;
    }

}
