package io.github.twilightflower.paraglider;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {
	public static float maxYawV;
	public static float yawAccel;
	public static float yawDecel;
	
	public static float tiltSpeed;
	public static float maxTilt;
	
	public static float airResistance;
	public static float gravity;
	public static float terminalVelocity;
	public static float horizontalAccel;
	
	public static int cooldownTime;
	public static int durabilityRate;
	
	public static boolean elenaiDodgeCompat;
	public static float featherRate;
	
	public static float tiltFac;
	
	public static void load(File file) {
		Configuration cfg = new Configuration(file);
		
		maxYawV = cfg.getFloat("maxYawV", "physics", 5, 0, 10000, "Maximum speed the glider can rotate at, in degrees per tick");
		yawAccel = cfg.getFloat("yawAccel", "physics", 0.3f, 0, 10000, "Speed the glider's rotation accelerates at, in degrees per tick per tick");
		yawDecel = cfg.getFloat("yawDecel", "physics", 2, 0, 10000, "Speed the glider's rotation decelerates at, in degrees per tick per tick.\n"
				+ "Should be greater than yawAccel.");
		
		airResistance = 1 - cfg.getFloat("airResistance", "physics", 0.3f, 0, 1, "Fraction of the glider's speed lost to air resistance per tick.");
		gravity = cfg.getFloat("gravity", "physics", 0.005f, 0, 10000, "Acceleration of gravity while gliding, in blocks per tick per tick.");
		terminalVelocity = cfg.getFloat("terminalVelocity", "physics", 0.05f, 0, 10000, "Terminal downwards velocity while gliding, in blocks per tick.");
		horizontalAccel = cfg.getFloat("horizontalAccel", "physics", 0.3f, 0, 10000, "Horizontal acceleration while gliding, in blocks per tick per tick.");
		
		tiltSpeed = cfg.getFloat("tiltSpeed", "visual", 0.4f, 0, 10000, "Speed the glider tilts at, in degrees per tick per tick. Visual only.");
		maxTilt = cfg.getFloat("maxTilt", "visual", 20, 0, 180, "Maximum amount the glider can tilt to the side, in degrees. Visual only.");
		
		cooldownTime = cfg.getInt("cooldownTime", "item", 20, 0, 10000, "Cooldown time between closing and being able to re-open the glider, in ticks.");
		durabilityRate = cfg.getInt("durabilityRate", "item", 20, 0, Integer.MAX_VALUE, "Time between durability being removed from the item while gliding, in ticks.");
		
		elenaiDodgeCompat = cfg.getBoolean("elenaiDodgeCompat", "elenaiDodge", SimpleParagliderMod.isClient, "Enable Elenai Dodge 2 compatibility.\n"
				+ "This disables gliding while out of feathers, and makes gliding consume feathers.\n"
				+ "Note: As of the release of this mod, Elenai Dodge 2 has an issue in its API on dedicated servers, causing a crash.\n"
				+ "Therefore, this defaults to false on dedicated servers for now.\n"
				+ "See https://github.com/ElenaiDev/ElenaiDodge2.0/issues/83 for more information.");
		featherRate = cfg.getFloat("featherRate", "elenaiDodge", 0.05f, 0, 10000, "Rate to consume feathers while gliding, in half-feathers per tick.");
		
		tiltFac = maxTilt / maxYawV;
		if(Float.isInfinite(tiltFac)) {
			tiltFac = 0;
		}
		
		cfg.save();
	}
}
