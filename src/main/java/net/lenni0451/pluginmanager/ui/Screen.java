package net.lenni0451.pluginmanager.ui;

import net.lenni0451.pluginmanager.utils.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Screen {

    private final String title;
    private final int rows;
    private final int columns;
    private final ItemList itemList;
    private int ticks;

    public Screen(final String title, final int rows) {
        this(title, rows, 9);
    }

    public Screen(final String title, final int rows, final int columns) {
        this.rows = rows;
        this.columns = columns;
        this.title = title;
        this.itemList = new ItemList(rows, columns);

        this.init(this.itemList);
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public void show(final ScreenHolder screenHolder) {
        Inventory inventory = Bukkit.createInventory(screenHolder.getPlayer(), this.rows * this.columns, this.title);
        this.setItems(inventory);
        screenHolder.getPlayer().openInventory(inventory);
    }

    public void callTick(final ScreenHolder screenHolder) {
        this.tick(screenHolder, this.ticks++);
        this.setItems(screenHolder.getPlayer().getOpenInventory().getTopInventory());
    }

    public void inventoryClick(final ScreenHolder screenHolder, final int row, final int column, final ClickType clickType) {
        Tuple<ItemStack, ClickListener> slot = this.itemList.get(row, column);
        if (slot.getB() != null) slot.getB().onClick(screenHolder, slot.getA(), clickType);
    }


    protected abstract void init(final ItemList itemList);

    protected abstract void tick(final ScreenHolder screenHolder, final int ticks);

    protected abstract void close();


    private void setItems(final Inventory inventory) {
        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.columns; c++) {
                inventory.setItem(r * this.columns + c, this.itemList.get(r, c).getA());
            }
        }
    }

}
