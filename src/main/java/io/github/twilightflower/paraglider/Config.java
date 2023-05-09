package io.github.twilightflower.paraglider;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

public class Config {
	public static float maxYawV;
	public static float yawAccel;
	public static float yawDecel;
	public static float yawSnapDist;
	public static float yawSnapSpeed;
	
	public static float tiltSpeed;
	public static float maxTilt;
	public static float maxHeadTurn;
	
	public static float airResistance;
	public static float gravity;
	public static float terminalVelocity;
	public static float horizontalAccel;
	
	public static float upGravity;
	public static float fallDecel;
	
	public static int cooldownTime;
	public static int durabilityRate;
	
	public static boolean elenaiDodgeCompat;
	public static float featherRate;
	
	public static float tiltFac;
	
	public static boolean attemptCustomArmorArmPose;
	
	public static void load(File file) {
		Configuration cfg = new Configuration(file);
		
		maxYawV = cfg.getFloat("maxYawV", "physics", 5, 0, 10000, "Maximum speed the glider can rotate at, in degrees per tick");
		yawAccel = cfg.getFloat("yawAccel", "physics", 0.3f, 0, 10000, "Speed the glider's rotation accelerates at, in degrees per tick per tick");
		yawDecel = cfg.getFloat("yawDecel", "physics", 2, 0, 10000, "Speed the glider's rotation decelerates at, in degrees per tick per tick.\n"
				+ "Should be greater than yawAccel.");
		yawSnapDist = cfg.getFloat("yawSnapDist", "physics", 5, 0, 180, "Maximum difference between the player's look yaw and current glider yaw where the "
				+ "glider will \"snap'\" to the exact player yaw, in degrees.");
		yawSnapSpeed = cfg.getFloat("yawSnapSpeed", "physics", 1, 0, 10000, "Maximum turning speed where the glider will \"snap\" to the exact player yaw, in degrees per tick.");
		
		
		airResistance = 1 - cfg.getFloat("airResistance", "physics", 0.3f, 0, 1, "Fraction of the glider's speed lost to air resistance per tick.");
		gravity = cfg.getFloat("gravity", "physics", 0.005f, 0, 10000, "Acceleration of gravity while gliding, in blocks per tick per tick.");
		terminalVelocity = -cfg.getFloat("terminalVelocity", "physics", 0.05f, 0, 10000, "Terminal downwards velocity while gliding, in blocks per tick.");
		horizontalAccel = cfg.getFloat("horizontalAccel", "physics", 0.3f, 0, 10000, "Horizontal acceleration while gliding, in blocks per tick per tick.");
		
		upGravity = cfg.getFloat("upGravity", "physics", 0.04f, 0, 10000, "Gravity that applies when a glider has upwards velocity, in blocks per tick per tick.\n"
				+ "This can happen due to a player being moving upwards when they deploy the glider.");
		fallDecel = cfg.getFloat("fallDecel", "physics", 0.03f, 0, 10000, "Rate the glider decelerates when falling faster than terminal velocity, in blocks per tick per tick.\n"
				+ "This can happen when a player is already falling when they deploy the glider.");
		
		tiltSpeed = cfg.getFloat("tiltSpeed", "visual", 0.4f, 0, 10000, "Speed the glider tilts at, in degrees per tick per tick. Visual only.");
		maxTilt = cfg.getFloat("maxTilt", "visual", 20, 0, 180, "Maximum amount the glider can tilt to the side, in degrees. Visual only.");
		maxHeadTurn = cfg.getFloat("maxHeadTurn", "visual", 100, 0, 180, "Maximum amount the player's head can appear to turn away from forward in third person.\n"
				+ "Visual only, does not affect first-person.");
		attemptCustomArmorArmPose = cfg.getBoolean("attemptCustomArmorArmPose", "visual", true, "Attempt to make other mods' custom armor models use the correct arm pose.\n"
				+ "This code is somewhat fragile -- if you're getting crashes when rendering players in third person, try disabling this.");
		
		cooldownTime = cfg.getInt("cooldownTime", "item", 20, 0, 10000, "Cooldown time between closing and being able to re-open the glider, in ticks.");
		durabilityRate = cfg.getInt("durabilityRate", "item", 20, 0, Integer.MAX_VALUE, "Time between durability being removed from the item while gliding, in ticks.");
		
		elenaiDodgeCompat = cfg.getBoolean("elenaiDodgeCompat", "elenaiDodge", SimpleParagliderMod.isClient || Loader.isModLoaded("universaltweaks"), "Enable Elenai Dodge 2 compatibility.\n"
				+ "This disables gliding while out of feathers, and makes gliding consume feathers.\n"
				+ "Note: As of the release of this mod, Elenai Dodge 2 has an issue in its API on dedicated servers, causing a crash.\n"
				+ "This issue is fixed in the mod Universal Tweaks, starting at version 1.6.0."
				+ "Therefore, this defaults to false on dedicated servers without Universal Tweaks.\n"
				+ "See https://github.com/ElenaiDev/ElenaiDodge2.0/issues/83 for more information.");
		featherRate = cfg.getFloat("featherRate", "elenaiDodge", 0.05f, 0, 10000, "Rate to consume feathers while gliding, in half-feathers per tick.");
		
		tiltFac = maxTilt / maxYawV;
		if(Float.isInfinite(tiltFac)) {
			tiltFac = 0;
		}
		
		cfg.save();
	}
}
