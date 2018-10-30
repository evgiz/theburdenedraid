package net.evgiz.ld40.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.evgiz.ld40.LudumDare40;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "LibGDX 3D";
		config.width = 900;
		config.height = 500;
		config.vSyncEnabled = false;
		config.resizable = false;

		config.foregroundFPS = 0;
		config.backgroundFPS = 0;

		new LwjglApplication(new LudumDare40(), config);
	}
}
