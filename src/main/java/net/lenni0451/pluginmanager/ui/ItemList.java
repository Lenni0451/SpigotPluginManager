package net.lenni0451.pluginmanager.ui;

import net.lenni0451.pluginmanager.utils.Tuple;
import org.bukkit.inventory.ItemStack;

public class ItemList {

    private final int rows;
    private final int columns;
    private final ItemStack[][] items;
    private final ClickListener[][] clickListeners;

    public ItemList(final int rows, final int columns) {
        this.rows = rows;
        this.columns = columns;
        this.items = new ItemStack[rows][columns];
        this.clickListeners = new ClickListener[rows][columns];
    }

    public ItemList fill(final ItemStack itemStack) {
        return this.fill(itemStack, null);
    }

    public ItemList fill(final ItemStack itemStack, final ClickListener clickListener) {
        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.columns; c++) {
                this.items[r][c] = this.clone(itemStack);
                this.clickListeners[r][c] = clickListener;
            }
        }
        return this;
    }

    public ItemList fillRow(final int row, final ItemStack itemStack) {
        return this.fillRow(row, itemStack, null);
    }

    public ItemList fillRow(final int row, final ItemStack itemStack, final ClickListener clickListener) {
        this.checkRow(row);
        for (int c = 0; c < this.columns; c++) {
            this.items[row][c] = this.clone(itemStack);
            this.clickListeners[row][c] = clickListener;
        }
        return this;
    }

    public ItemList fillColumn(final int column, final ItemStack itemStack) {
        return this.fillColumn(column, itemStack, null);
    }

    public ItemList fillColumn(final int column, final ItemStack itemStack, final ClickListener clickListener) {
        this.checkColumn(column);
        for (int r = 0; r < this.rows; r++) {
            this.items[r][column] = this.clone(itemStack);
            this.clickListeners[r][column] = clickListener;
        }
        return this;
    }

    public ItemList set(final int row, final int column, final ItemStack itemStack) {
        return this.set(row, column, itemStack, null);
    }

    public ItemList set(final int row, final int column, final ItemStack itemStack, final ClickListener clickListener) {
        this.checkRow(row);
        this.checkColumn(column);
        if (column < 0 || column >= this.columns) throw new IllegalArgumentException("Column must be between 0 and " + (this.columns - 1) + " but was " + column);
        this.items[row][column] = itemStack.clone();
        this.clickListeners[row][column] = clickListener;
        return this;
    }

    public Tuple<ItemStack, ClickListener> get(final int row, final int column) {
        this.checkRow(row);
        this.checkColumn(column);
        return new Tuple<>(this.items[row][column], this.clickListeners[row][column]);
    }


    private ItemStack clone(final ItemStack itemStack) {
        if (itemStack == null) return null;
        return itemStack.clone();
    }

    private void checkRow(final int row) {
        if (row < 0 || row >= this.rows) throw new IllegalArgumentException("Row must be between 0 and " + (this.rows - 1) + " but was " + row);
    }

    private void checkColumn(final int column) {
        if (column < 0 || column >= this.columns) throw new IllegalArgumentException("Column must be between 0 and " + (this.columns - 1) + " but was " + column);
    }

}
