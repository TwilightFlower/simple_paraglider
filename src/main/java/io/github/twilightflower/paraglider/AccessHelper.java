package io.github.twilightflower.paraglider;

import java.lang.reflect.Field;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class AccessHelper {
	private static final Field RIDING_ENTITY = ObfuscationReflectionHelper.findField(Entity.class, "field_184239_as");
	
	static {
		RIDING_ENTITY.setAccessible(true);
	}
	
	public static void setRidingEntity(Entity on, Entity to) {
		try {
			RIDING_ENTITY.set(on, to);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
