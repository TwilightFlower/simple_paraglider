package io.github.twilightflower.paraglider;

public class Util {
	public static float lerp(float start, float end, float frac) {
		if(frac > 1) {
			frac = 1;
		} else if(frac < 0) {
			frac = 0;
		}
		
		float diff = end - start;
		return start + (diff * frac);
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
