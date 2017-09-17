package utility;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class SystemInformationUtility {
	
	//get width of currently focused monitor in pixels
	public static int getScreenWidth() {
		GraphicsDevice currentScreen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = currentScreen.getDisplayMode().getWidth();
		return width;
	}
		
	//get height of currently focused monitor in pixels
	public static int getScreenHeight() {
		GraphicsDevice currentScreen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int height = currentScreen.getDisplayMode().getHeight();
		return height;
	}
}
