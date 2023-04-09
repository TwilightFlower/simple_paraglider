package io.github.twilightflower.paraglider.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BakedModelHelper {
	private static final Map<ResourceLocation, ModelRef> SUPPLIERS = new HashMap<>();
	private static final Function<ResourceLocation, TextureAtlasSprite> TEX_GETTER = loc -> {
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(loc.toString());
	};
	private static boolean frozen = false;
	
	public static Supplier<IBakedModel> itemModel(ResourceLocation id) {
		if(frozen) {
			throw new IllegalStateException("Attempt to add model " + id + " after freeze");
		}
		
		return SUPPLIERS.computeIfAbsent(id, k -> new ModelRef());
	}
	
	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Pre event) {
		if(event.getMap() == Minecraft.getMinecraft().getTextureMapBlocks()) {
			frozen = true;
			
			for(Map.Entry<ResourceLocation, ModelRef> entry : SUPPLIERS.entrySet()) {
				IModel unbaked = ModelLoaderRegistry.getModelOrMissing(entry.getKey());
				entry.getValue().unbaked = unbaked;
				
				for(ResourceLocation tex : unbaked.getTextures()) {
					event.getMap().registerSprite(tex);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void modelBake(ModelBakeEvent event) {
		for(ModelRef ref : SUPPLIERS.values()) {
			ref.baked = ref.unbaked.bake(ref.unbaked.getDefaultState(), DefaultVertexFormats.ITEM, TEX_GETTER);
			ref.unbaked = null;
		}
	}
	
	private static class ModelRef implements Supplier<IBakedModel> {
		IBakedModel baked;
		IModel unbaked;
		
		public IBakedModel get() {
			return baked;
		}
	}
}
