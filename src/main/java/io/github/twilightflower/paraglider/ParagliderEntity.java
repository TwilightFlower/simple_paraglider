package io.github.twilightflower.paraglider;

import java.util.List;

import io.github.twilightflower.paraglider.client.PleaseDontCrashClassloading;
import io.github.twilightflower.paraglider.compat.ElenaiDodgeCompat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParagliderEntity extends Entity {
	private static final float DEGREES_TO_RADIANS = (float) Math.PI / 180;
	
	private float yawMotion = 0;
	private int durabilityTimer = Config.durabilityRate;
	public boolean shouldDismount = false;
	public EnumHand usedHand = EnumHand.MAIN_HAND;
	
	public float partialFeathers = 0;
	
	public float prevRotationRoll;
	public float rotationRoll;
	
	public ParagliderEntity(World worldIn) {
		super(worldIn);
		setSize(0.6f, 1.8f);
	}
	
	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		this.fallDistance = 0;
		
		Entity rider = getControllingPassenger();
		prevRotationRoll = rotationRoll;
		
		if(rider != null && !rider.isDead && !rider.onGround && !onGround && !shouldDismount &&
				isHoldingGlider(rider) && ElenaiDodgeCompat.INSTANCE.tick(this, rider) && !inWater) {
			float actualRiderYaw = actualMod(rider.rotationYaw, 360);
			float actualSelfYaw = actualMod(rotationYaw, 360);
			
			float yawDiff = actualRiderYaw - actualSelfYaw;
			if(yawDiff > 180) {
				yawDiff -= 360;
			} else if(yawDiff < -180) {
				yawDiff += 360;
			}
			
			float absDiff = Math.abs(yawDiff);
			float diffSign = Math.signum(yawDiff);
			float targetYawSpeed = diffSign * Config.maxYawV;
			
			if(absDiff < 5 && yawMotion < 1) {
				yawMotion = 0;
				rotationYaw = rider.rotationYaw;
			} else {
				if(diffSign == Math.signum(yawMotion)) {
					yawMotion = Util.towards(yawMotion, targetYawSpeed, Config.yawAccel);
				} else {
					yawMotion = Util.towards(yawMotion, targetYawSpeed, Config.yawDecel);
				}
			}
			
			rotationYaw += yawMotion;
			rotationRoll = Util.towards(rotationRoll, yawMotion * Config.tiltFac, Config.tiltSpeed);
			
			float fallSpeedFactor = (float) motionY / -Config.terminalVelocity;
			
			// minecraft's coordinate system makes no sense
			// why is sin x and why is it backwards
			float accel = fallSpeedFactor * Config.horizontalAccel;
			float radYaw = rotationYaw * DEGREES_TO_RADIANS;
			float accelX = -MathHelper.sin(radYaw) * accel;
			float accelZ = MathHelper.cos(radYaw) * accel;
			
			motionX += accelX;
			motionZ += accelZ;
			
			motionX *= Config.airResistance;
			motionZ *= Config.airResistance;
			
			if(motionY > -Config.terminalVelocity) {
				motionY -= Config.gravity;
			}
			
			move(MoverType.SELF, motionX, motionY, motionZ);
			
			if(durabilityTimer-- <= 0) {
				durabilityTimer = Config.durabilityRate;
				if(rider instanceof EntityLivingBase && !world.isRemote) {
					EntityLivingBase living = (EntityLivingBase) rider;
					living.getHeldItem(usedHand).damageItem(1, living);
				}
			}
		} else if(!world.isRemote) {
			// we don't want to run the destroy logic clientside
			// if the client destroys it but not the server, we get a desync
			manualDismount();
			world.removeEntity(this);
		}
	}
	
	@Override
	public void onAddedToWorld() {
		if(SimpleParagliderMod.isClient && world.isRemote) {
			// this is 100% necessary, if I copy-paste the code from that file into this one it crashes.
			// I don't know why.
			PleaseDontCrashClassloading.startGliderSound(this);
		}
	}
	
	private boolean isHoldingGlider(Entity passenger) {
		if(passenger instanceof EntityLivingBase) {
			EntityLivingBase e = (EntityLivingBase) passenger;
			return e.getHeldItem(usedHand).getItem() == SimpleParagliderMod.PARAGLIDER;
		}
		// for non-living entities, we don't care
		return true;
	}
	
	@Override
	public void setDead() {
		manualDismount();
		playSound(SoundEvents.ITEM_ARMOR_EQIIP_ELYTRA, 1, 1);
		super.setDead();
	}
	
	public void manualDismount() {
		Entity passenger = getControllingPassenger();
		
		if(passenger != null) {
			AccessHelper.setRidingEntity(passenger, null);
			removePassenger(passenger);
			passenger.setSneaking(false);
		}
	}
	
	@Override
	public void updatePassenger(Entity passenger) {
		passenger.setPosition(posX, posY, posZ);
		passenger.fallDistance = 0;
		
		if(passenger instanceof EntityPlayer) {
			((EntityPlayer) passenger).getCooldownTracker().setCooldown(SimpleParagliderMod.PARAGLIDER, Config.cooldownTime);
		}
	}
	
	@Override
	public Entity getControllingPassenger() {
		List<Entity> passengers = getPassengers(); // getPassengers clones, only call once
		return passengers.isEmpty() ? null : passengers.get(0);
	}
	
	@Override
	public boolean shouldRiderSit() {
		return false;
	}
	
	private static float actualMod(float num, float modBy) {
		float javaMod = num % modBy;
		if(javaMod < 0) {
			javaMod += modBy;
		}
		return javaMod;
	}

	@Override
	protected void entityInit() { }

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		yawMotion = compound.getFloat("yaw_motion");
		partialFeathers = compound.getFloat("partial_feathers");
		if(compound.hasKey("durability_timer")) {
			durabilityTimer = compound.getInteger("durability_timer");
		}
		
		// We might end up deserializing one with a gone player rider due to logouts.
		// In SP, this causes a desync, so just don't load them.
		// Check for a passenger because some other mod might be calling readFromNbt to modify data.
		if(!world.isRemote && getControllingPassenger() == null) {
			setDead();
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setFloat("yaw_motion", yawMotion);
		compound.setFloat("partial_feathers", partialFeathers);
		compound.setInteger("durability_timer", durabilityTimer);
	}
}
