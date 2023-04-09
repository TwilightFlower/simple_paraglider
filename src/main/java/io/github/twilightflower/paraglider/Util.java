package io.github.twilightflower.paraglider;

public class Util {
	/**
	 * angle1 - angle2
	 */
	public static float angleDiff(float angle1, float angle2) {
		angle1 = actualMod(angle1, 360);
		angle2 = actualMod(angle2, 360);
		
		float diff = angle1 - angle2;
		if(diff > 180) {
			return diff - 360;
		} else if(diff < -180) {
			return diff + 360;
		} else {
			return diff;
		}
	}
	
	public static float actualMod(float num, float modBy) {
		float javaMod = num % modBy;
		if(javaMod < 0) {
			javaMod += modBy;
		}
		return javaMod;
	}
	
	public static float lerp(float start, float end, float frac) {
		if(frac > 1) {
			frac = 1;
		} else if(frac < 0) {
			frac = 0;
		}
		
		float diff = end - start;
		return start + (diff * frac);
	}
	
	public static double towards(double start, double end, double by) {
		double dist = end - start;
		double absDist = Math.abs(dist);
		if(by > absDist) {
			by = absDist;
		}
		
		by *= Math.signum(dist);
		return start + by;
	}
	
	public static float towards(float start, float end, float by) {
		float dist = end - start;
		float absDist = Math.abs(dist);
		if(by > absDist) {
			by = absDist;
		}
		
		by *= Math.signum(dist);
		return start + by;
	}
}
