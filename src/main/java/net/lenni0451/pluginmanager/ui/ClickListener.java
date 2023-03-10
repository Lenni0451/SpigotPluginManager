package net.lenni0451.pluginmanager.ui;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public interface ClickListener {

    void onClick(final ScreenHolder screenHolder, final ItemStack itemStack, final ClickType clickType);

}
