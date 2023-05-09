package io.github.twilightflower.paraglider.client;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import io.github.twilightflower.paraglider.Config;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ClientAccessHelper {
	private static final Field LAYERS = ObfuscationReflectionHelper.findField(RenderLivingBase.class, "field_177097_h");
	private static final GetArmorModelHook GET_ARMOR_MODEL_HOOK;
	
	static {
		LAYERS.setAccessible(true);
		
		if(Config.attemptCustomArmorArmPose) {
			try {
				Method hookMethod = LayerArmorBase.class.getDeclaredMethod("getArmorModelHook", EntityLivingBase.class, ItemStack.class, EntityEquipmentSlot.class, ModelBase.class);
				hookMethod.setAccessible(true);
				MethodHandle handle = MethodHandles.publicLookup().unreflect(hookMethod);
				MethodHandle metaHandle = MethodHandles.publicLookup().findVirtual(MethodHandle.class, "invoke", handle.type());
				
				GET_ARMOR_MODEL_HOOK = (GetArmorModelHook) LambdaMetafactory.metafactory(MethodHandles.lookup(),
						"getArmorModelHook",
						MethodType.methodType(GetArmorModelHook.class, MethodHandle.class),
						handle.type(),
						metaHandle,
						handle.type()).getTarget().invoke(handle);
			} catch(Throwable e) {
				throw new RuntimeException();
			}
		} else {
			GET_ARMOR_MODEL_HOOK = (layer, entity, stack, slot, model) -> model;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<LayerRenderer<?>> getLayers(RenderLivingBase<?> render) {
		try {
			return (List<LayerRenderer<?>>) LAYERS.get(render);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static ModelBase getArmorModelHook(LayerArmorBase<?> armorLayer, EntityLivingBase entity, ItemStack itemStack, EntityEquipmentSlot slot, ModelBase model) {
		return GET_ARMOR_MODEL_HOOK.getArmorModelHook(armorLayer, entity, itemStack, slot, model);
	}
	
	private interface GetArmorModelHook {
		ModelBase getArmorModelHook(LayerArmorBase<?> armorLayer, EntityLivingBase entity, ItemStack itemStack, EntityEquipmentSlot slot, ModelBase model);
	}
}
