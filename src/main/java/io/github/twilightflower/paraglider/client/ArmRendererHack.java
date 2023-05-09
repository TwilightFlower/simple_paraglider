package io.github.twilightflower.paraglider.client;

import java.util.function.BooleanSupplier;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ArmRendererHack extends ModelRenderer {
	private static final float X_ROTATION = (float) Math.PI;
	private static final float Y_ROTATION = 0;
	private static final float Z_ROTATION = (float) Math.PI / 8;
	
	private final ModelRenderer base;
	private final BooleanSupplier condition;
	private final boolean invert;
	
	public ArmRendererHack(ModelBase model, ModelRenderer copyFrom, BooleanSupplier condition, boolean invert) {
		super(model, copyFrom.boxName);
		copy(copyFrom, this);
		cubeList = copyFrom.cubeList;
		childModels = copyFrom.childModels;
		base = copyFrom;
		this.condition = condition;
		this.invert = invert;
	}
	
	@Override
	public ModelRenderer setTextureOffset(int x, int y) {
		base.setTextureOffset(x, y);
		return super.setTextureOffset(x, y);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void render(float scale) {
		copy(this, base);
		apply();
		base.render(scale);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void renderWithRotation(float scale) {
		copy(this, base);
		apply();
		base.renderWithRotation(scale);
	}
	
	@Override
	public void addChild(ModelRenderer child) {
		base.addChild(child);
	}
	
	@Override
	public ModelRenderer addBox(String partName, float offX, float offY, float offZ, int width, int height, int depth) {
		base.addBox(partName, offX, offY, offZ, width, height, depth);
		return this;
	}
	
	@Override
	public ModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth) {
		base.addBox(offX, offY, offZ, width, height, depth);
		return this;
	}
	
	@Override
	public ModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth, boolean mirrored) {
		base.addBox(offX, offY, offZ, width, height, depth, mirrored);
		return this;
	}
	
	@Override
	public void addBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor) {
		base.addBox(offX, offY, offZ, width, height, depth, scaleFactor);
	}
	
	@Override
	public void postRender(float scale) {
		base.postRender(scale);
		copy(base, this);
	}
	
	private void apply() {
		if(condition.getAsBoolean()) {
			base.rotateAngleX = X_ROTATION;
			base.rotateAngleY = Y_ROTATION;
			
			if(invert) {
				base.rotateAngleZ = -Z_ROTATION;
			} else {
				base.rotateAngleZ = Z_ROTATION;
			}
		}
	}
	
	private static void copy(ModelRenderer from, ModelRenderer to) {
		to.textureWidth = from.textureWidth;
		to.textureHeight = from.textureHeight;
		to.rotationPointX = from.rotationPointX;
		to.rotationPointY = from.rotationPointY;
		to.rotationPointZ = from.rotationPointZ;
		to.rotateAngleX = from.rotateAngleX;
		to.rotateAngleY = from.rotateAngleY;
		to.rotateAngleZ = from.rotateAngleZ;
		to.mirror = from.mirror;
		to.showModel = from.showModel;
		to.isHidden = from.isHidden;
		to.offsetX = from.offsetX;
		to.offsetY = from.offsetY;
		to.offsetZ = from.offsetZ;
	}
}
