package net.lenni0451.pluginmanager.ui;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScreenHolder {

    private final Player player;
    private Screen currentScreen;
    private final List<Screen> previousScreens = new ArrayList<>();

    public ScreenHolder(final Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Screen getCurrentScreen() {
        return this.currentScreen;
    }

    public List<Screen> getPreviousScreens() {
        return Collections.unmodifiableList(this.previousScreens);
    }

    public void openScreen(final Screen screen) {
        if (this.currentScreen != null) this.previousScreens.add(this.currentScreen);
        this.currentScreen = screen;
        this.currentScreen.show(this);
    }

    public void closeScreen() {
        if (this.currentScreen != null) {
            this.currentScreen.close();
            this.currentScreen = null;
        }
        this.previousScreens.clear();
    }

    public void closeCurrentScreen() {
        if (this.currentScreen != null) {
            this.currentScreen.close();
            if (this.previousScreens.isEmpty()) {
                this.currentScreen = null;
            } else {
                this.currentScreen = this.previousScreens.remove(this.previousScreens.size() - 1);
                this.currentScreen.show(this);
            }
        }
    }

}
