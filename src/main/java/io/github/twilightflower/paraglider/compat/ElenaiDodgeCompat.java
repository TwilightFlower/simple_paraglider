package io.github.twilightflower.paraglider.compat;

import com.elenai.elenaidodge2.api.FeathersHelper;

import io.github.twilightflower.paraglider.Config;
import io.github.twilightflower.paraglider.ParagliderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Loader;

public class ElenaiDodgeCompat {
	public static final ElenaiDodgeCompat INSTANCE;
	
	static {
		if(Loader.isModLoaded("elenaidodge2") && Config.elenaiDodgeCompat) {
			INSTANCE = new Impl();
		} else {
			INSTANCE = new ElenaiDodgeCompat();
		}
	}
	
	private ElenaiDodgeCompat() { }
	
	public boolean tick(ParagliderEntity glider, Entity rider) {
		return true;
	}
	
	public boolean canGlide(EntityPlayer player) {
		return true;
	}
	
	public static class Impl extends ElenaiDodgeCompat {
		@Override
		public boolean canGlide(EntityPlayer player) {
			if(player instanceof EntityPlayerMP) {
				EntityPlayerMP playerMp = (EntityPlayerMP) player;
				int weight = FeathersHelper.getWeight(playerMp);
				int feathers = FeathersHelper.getFeatherLevel(playerMp);
				
				return feathers > weight;
			} else {
				return true;
			}
		}
		
		@Override
		public boolean tick(ParagliderEntity glider, Entity rider) {
			if(rider instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) rider;
				int weight = FeathersHelper.getWeight(player);
				int feathers = FeathersHelper.getFeatherLevel(player);
				
				glider.partialFeathers += Config.featherRate;
				
				// if we have more than 1 feather used, realize it and decrease the increment
				if(glider.partialFeathers >= 1) {
					int fullFeathers = (int) glider.partialFeathers;
					glider.partialFeathers -= fullFeathers;
					feathers -= fullFeathers;
					FeathersHelper.decreaseFeathers(player, fullFeathers);
				}
				
				// dismount if necessary
				if(feathers <= weight) {
					return false;
				}
				
				return true;
			} else {
				return true;
			}
		}
	}
}
