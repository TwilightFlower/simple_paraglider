package io.github.twilightflower.paraglider.client;

import java.util.Map;
import java.util.WeakHashMap;

import io.github.twilightflower.paraglider.ParagliderEntity;
import io.github.twilightflower.paraglider.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents {
	private static final ModelBiped.ArmPose GLIDING = EnumHelper.addEnum(ModelBiped.ArmPose.class, "GLIDING", new Class[0]);
	private static final Map<RenderPlayer, LayerBipedArmor> ARMOR_MAP = new WeakHashMap<>();
	private static final Map<ModelBase, Void> ARMOR_MODEL_SET = new WeakHashMap<>();
	private static final EntityEquipmentSlot[] ARMOR_SLOTS = {EntityEquipmentSlot.FEET, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.HEAD};
	
	@SubscribeEvent
	public void cancelHandRender(RenderHandEvent event) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if(player.getRidingEntity() instanceof ParagliderEntity) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void renderHud(RenderGameOverlayEvent.Pre event) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if(event.getType() == ElementType.ALL && player.getRidingEntity() instanceof ParagliderEntity) {
			GuiIngameForge.renderFood = true;
		}
	}
	
	private static ItemStack heldMainHand;
	private static ItemStack heldOffHand;
	private static boolean cancelSound = false;
	
	@SubscribeEvent
	public void preRenderPlayer(RenderPlayerEvent.Pre event) {
		EntityPlayer player = event.getEntityPlayer();
		
		if(player.getRidingEntity() instanceof ParagliderEntity) {
			ParagliderEntity paraglider = (ParagliderEntity) player.getRidingEntity();
			
			cancelSound = true;
			
			// we don't want the items to render
			// the simplest solution is to get rid of them
			heldMainHand = player.getHeldItemMainhand();
			player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
			
			heldOffHand = player.getHeldItemOffhand();
			player.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
			
			cancelSound = false;
			
			// make body only rotate with glider
			player.renderYawOffset = paraglider.rotationYaw;
			player.prevRenderYawOffset = paraglider.prevRotationYaw;
			
			float roll = Util.lerp(paraglider.prevRotationRoll, paraglider.rotationRoll, event.getPartialRenderTick());
			float yaw = Util.lerp(paraglider.prevRotationYaw, paraglider.rotationYaw, event.getPartialRenderTick());
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(event.getX(), event.getY() + player.height, event.getZ());
			GlStateManager.rotate(-yaw, 0, 1, 0);
			GlStateManager.rotate(roll, 0, 0, 1);
			GlStateManager.rotate(yaw, 0, 1, 0);
			GlStateManager.translate(-event.getX(), -event.getY() - player.height, -event.getZ());
		}
	}
	
	@SubscribeEvent
	public void preRenderLiving(RenderLivingEvent.Pre<?> event) {
		if(event.getRenderer() instanceof RenderPlayer) {
			RenderPlayer render = (RenderPlayer) event.getRenderer();
			ModelPlayer model = render.getMainModel();
			AbstractClientPlayer player = (AbstractClientPlayer) event.getEntity();
			
			if(!ARMOR_MAP.containsKey(render)) {
				hackArms(model);
				model.bipedLeftArmwear = new ArmRendererHack(model, model.bipedLeftArmwear, () -> model.leftArmPose == GLIDING, false);
				model.bipedRightArmwear = new ArmRendererHack(model, model.bipedRightArmwear, () -> model.rightArmPose == GLIDING, true);
				
				for(LayerRenderer<?> layer : ClientAccessHelper.getLayers(render)) {
					if(layer instanceof LayerBipedArmor) {
						ARMOR_MAP.put(render, (LayerBipedArmor) layer);
						break;
					}
				}
			}
			
			LayerBipedArmor armorLayer = ARMOR_MAP.get(render);
			if(armorLayer != null) {
				
				for(EntityEquipmentSlot slot : ARMOR_SLOTS) {
					ModelBase armorModel = ClientAccessHelper.getArmorModelHook(armorLayer, player, player.getItemStackFromSlot(slot), slot, armorLayer.getModelFromSlot(slot));
					if(!ARMOR_MODEL_SET.containsKey(armorModel)) {
						ARMOR_MODEL_SET.put(armorModel, null);
						
						if(armorModel instanceof ModelBiped) {
							hackArms((ModelBiped) armorModel);
						}
					}
				}
			}
			
			if(event.getEntity().getRidingEntity() instanceof ParagliderEntity) {
				model.leftArmPose = GLIDING;
				model.rightArmPose = GLIDING;
			}
		}
	}
	
	private void hackArms(ModelBiped model) {
		model.bipedLeftArm = new ArmRendererHack(model, model.bipedLeftArm, () -> model.leftArmPose == GLIDING, false);
		model.bipedRightArm = new ArmRendererHack(model, model.bipedRightArm, () -> model.rightArmPose == GLIDING, true);
	}
	
	@SubscribeEvent
	public void postRenderPlayer(RenderPlayerEvent.Post event) {
		EntityPlayer player = event.getEntityPlayer();
		
		if(player.getRidingEntity() instanceof ParagliderEntity) {
			GlStateManager.popMatrix();
			
			cancelSound = true;
			
			player.setHeldItem(EnumHand.MAIN_HAND, heldMainHand);
			heldMainHand = ItemStack.EMPTY;
			
			player.setHeldItem(EnumHand.OFF_HAND, heldOffHand);
			heldOffHand = ItemStack.EMPTY;
			
			cancelSound = false;
		}
	}
	
	@SubscribeEvent
	public void noEquipSounds(PlaySoundAtEntityEvent event) {
		if(cancelSound) {
			event.setCanceled(true);
		}
	}
}
