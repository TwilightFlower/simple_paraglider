package io.github.twilightflower.paraglider.client;

import io.github.twilightflower.paraglider.ParagliderEntity;
import io.github.twilightflower.paraglider.Util;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GliderSound extends MovingSound {
	private final ParagliderEntity entity;
	
	public GliderSound(ParagliderEntity glider) {
		super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS);
		repeat = true;
		repeatDelay = 0;
		volume = 0.01f;
		pitch = 1;
		entity = glider;
	}
	
	@Override
	public void update() {
		xPosF = (float) entity.posX;
		yPosF = (float) entity.posY;
		zPosF = (float) entity.posZ;
		
		volume = Util.towards(volume, MathHelper.sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ), 0.02f);
		volume = Math.min(0.5f, volume);
		volume = Math.max(0.01f, volume); // if volume = 0, sound stops
		
		donePlaying = entity.isDead;
	}
}
