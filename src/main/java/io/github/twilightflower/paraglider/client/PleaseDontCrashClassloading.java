package io.github.twilightflower.paraglider.client;

import io.github.twilightflower.paraglider.ParagliderEntity;
import net.minecraft.client.Minecraft;

public class PleaseDontCrashClassloading {
	public static void startGliderSound(ParagliderEntity glider) {
		Minecraft.getMinecraft().getSoundHandler().playSound(new GliderSound(glider));
	}
}
