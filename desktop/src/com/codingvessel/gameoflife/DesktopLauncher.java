package com.codingvessel.gameoflife;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    private DesktopLauncher() {
    }

    public static void main(String... arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1920, 1200);
        config.setForegroundFPS(0);
        config.useVsync(false);
        config.setTitle("GameOfLife");
        new Lwjgl3Application(new GameOfLife(), config);
    }
}
