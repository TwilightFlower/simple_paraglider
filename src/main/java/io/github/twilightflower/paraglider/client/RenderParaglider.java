package io.github.twilightflower.paraglider.client;

import java.util.function.Supplier;

import io.github.twilightflower.paraglider.ParagliderEntity;
import io.github.twilightflower.paraglider.SimpleParagliderMod;
import io.github.twilightflower.paraglider.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderParaglider extends Render<ParagliderEntity> {
	private static final ItemStack DUMMY_STACK = new ItemStack(Blocks.STONE);
	private static final Supplier<IBakedModel> MODEL = BakedModelHelper.itemModel(SimpleParagliderMod.id("entity/paraglider"));
	
	public static void init() {}
	
	public RenderParaglider(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public ResourceLocation getEntityTexture(ParagliderEntity entity) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
	
	float lyaw;
	
	@Override
	public void doRender(ParagliderEntity entity, double ex, double ey, double ez, float yaw, float partialTicks) {
		bindEntityTexture(entity);
		
		Entity controller = entity.getControllingPassenger();
		if(controller != null) {
			float roll = Util.lerp(entity.prevRotationRoll, entity.rotationRoll, partialTicks);
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(ex, ey, ez);
			GlStateManager.rotate(-yaw, 0, 1, 0);
			GlStateManager.translate(0, controller.height, 0);
			GlStateManager.rotate(roll, 0, 0, 1);
			
			RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
			itemRenderer.renderItem(DUMMY_STACK, MODEL.get());
			
			GlStateManager.popMatrix();
		}
	}
}
