package io.github.twilightflower.paraglider.client;

import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;

public class CallbackLayerRenderer<T extends EntityLivingBase> implements LayerRenderer<T> {
	private final Callback<? super T> callback;
	
	public CallbackLayerRenderer(Callback<? super T> callback) {
		this.callback = callback;
	}
	
	@Override
	public void doRenderLayer(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		callback.callback(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
	
	@FunctionalInterface
	public interface Callback<T extends EntityLivingBase> {
		void callback(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch, float scale);
	}
}
