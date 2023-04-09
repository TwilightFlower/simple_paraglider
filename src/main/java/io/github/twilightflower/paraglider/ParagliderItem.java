package io.github.twilightflower.paraglider;

import io.github.twilightflower.paraglider.compat.ElenaiDodgeCompat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ParagliderItem extends Item {
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		
		if(ElenaiDodgeCompat.INSTANCE.canGlide(player)) {
			if(!world.isRemote) {
				ParagliderEntity paraglider = new ParagliderEntity(world);
				
				paraglider.setPosition(player.posX, player.posY, player.posZ);
				paraglider.rotationYaw = player.rotationYaw;
				paraglider.usedHand = hand;
				
				world.spawnEntity(paraglider);
				player.startRiding(paraglider, true);
				if(player.getRidingEntity() == null) {
					System.out.println("Mount failed");
				}
				
				paraglider.playSound(SoundEvents.ITEM_ARMOR_EQIIP_ELYTRA, 1, 1);
			}
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}
}
