package com.dtp5.model;

import java.awt.Color;

/**
 * Manages the day/night cycle with smooth transitions.
 * Controls lighting, colors, and affects creature behavior.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public class DayNightCycle {

    /** Duration of a full day cycle in simulation ticks */
    private final int cycleDuration;

    /** Current position in the cycle (0 to cycleDuration) */
    private double cyclePosition;

    /** Time multiplier for speed control */
    private double timeMultiplier = 1.0;

    /** Whether the cycle is paused */
    private boolean paused = false;

    // Day colors
    private static final Color DAY_SKY_TOP = new Color(0, 105, 148);
    private static final Color DAY_SKY_BOTTOM = new Color(13, 27, 42);

    // Sunset colors
    private static final Color SUNSET_SKY_TOP = new Color(255, 94, 77);
    private static final Color SUNSET_SKY_BOTTOM = new Color(41, 20, 80);

    // Night colors
    private static final Color NIGHT_SKY_TOP = new Color(10, 15, 35);
    private static final Color NIGHT_SKY_BOTTOM = new Color(5, 8, 18);

    // Sunrise colors
    private static final Color SUNRISE_SKY_TOP = new Color(255, 154, 102);
    private static final Color SUNRISE_SKY_BOTTOM = new Color(48, 25, 52);

    /**
     * Creates a new day/night cycle.
     * 
     * @param cycleDurationTicks Number of ticks for a complete day/night cycle
     */
    public DayNightCycle(int cycleDurationTicks) {
        this.cycleDuration = cycleDurationTicks;
        this.cyclePosition = cycleDuration / 4.0; // Start at noon
    }

    /**
     * Creates a day/night cycle with default duration (10 minutes at 60fps).
     */
    public DayNightCycle() {
        this(36000); // 10 minutes at 60fps
    }

    /**
     * Updates the cycle by one tick.
     */
    public void tick() {
        if (!paused) {
            cyclePosition = (cyclePosition + timeMultiplier) % cycleDuration;
        }
    }

    /**
     * Gets the current phase of the day (0.0 to 1.0).
     * 0.0 = midnight, 0.25 = sunrise, 0.5 = noon, 0.75 = sunset
     * 
     * @return Phase value
     */
    public double getPhase() {
        return cyclePosition / cycleDuration;
    }

    /**
     * Checks if it's currently daytime (between sunrise and sunset).
     * 
     * @return true if day
     */
    public boolean isDaytime() {
        double phase = getPhase();
        return phase >= 0.2 && phase < 0.8;
    }

    /**
     * Gets the current light level (0.0 = dark, 1.0 = bright).
     * 
     * @return Light level
     */
    public float getLightLevel() {
        double phase = getPhase();

        if (phase >= 0.25 && phase < 0.75) {
            // Daytime - full brightness
            if (phase < 0.35) {
                // Sunrise transition
                return (float) ((phase - 0.25) / 0.1);
            } else if (phase >= 0.65) {
                // Sunset transition
                return (float) ((0.75 - phase) / 0.1);
            }
            return 1.0f;
        } else {
            // Nighttime
            if (phase >= 0.75 && phase < 0.85) {
                // Dusk
                return (float) (1.0 - (phase - 0.75) / 0.1);
            } else if (phase >= 0.15 && phase < 0.25) {
                // Dawn
                return (float) ((phase - 0.15) / 0.1);
            }
            return 0.15f; // Moonlight
        }
    }

    /**
     * Gets the current sky color for the top of the gradient.
     * 
     * @return Sky top color
     */
    public Color getSkyTopColor() {
        return interpolateSkyColor(true);
    }

    /**
     * Gets the current sky color for the bottom of the gradient.
     * 
     * @return Sky bottom color
     */
    public Color getSkyBottomColor() {
        return interpolateSkyColor(false);
    }

    private Color interpolateSkyColor(boolean top) {
        double phase = getPhase();

        Color dayColor = top ? DAY_SKY_TOP : DAY_SKY_BOTTOM;
        Color nightColor = top ? NIGHT_SKY_TOP : NIGHT_SKY_BOTTOM;
        Color sunriseColor = top ? SUNRISE_SKY_TOP : SUNRISE_SKY_BOTTOM;
        Color sunsetColor = top ? SUNSET_SKY_TOP : SUNSET_SKY_BOTTOM;

        // Determine which colors to blend
        if (phase >= 0.15 && phase < 0.25) {
            // Dawn: night -> sunrise
            double t = (phase - 0.15) / 0.1;
            return blendColors(nightColor, sunriseColor, t);
        } else if (phase >= 0.25 && phase < 0.35) {
            // Morning: sunrise -> day
            double t = (phase - 0.25) / 0.1;
            return blendColors(sunriseColor, dayColor, t);
        } else if (phase >= 0.35 && phase < 0.65) {
            // Day
            return dayColor;
        } else if (phase >= 0.65 && phase < 0.75) {
            // Afternoon: day -> sunset
            double t = (phase - 0.65) / 0.1;
            return blendColors(dayColor, sunsetColor, t);
        } else if (phase >= 0.75 && phase < 0.85) {
            // Dusk: sunset -> night
            double t = (phase - 0.75) / 0.1;
            return blendColors(sunsetColor, nightColor, t);
        } else {
            // Night
            return nightColor;
        }
    }

    private Color blendColors(Color c1, Color c2, double t) {
        t = Math.max(0, Math.min(1, t));
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * t);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * t);
        return new Color(r, g, b);
    }

    /**
     * Gets the moon visibility (0.0 = invisible, 1.0 = full visibility).
     * 
     * @return Moon visibility
     */
    public float getMoonVisibility() {
        double phase = getPhase();
        if (phase >= 0.8 || phase < 0.2) {
            if (phase >= 0.8 && phase < 0.9) {
                return (float) ((phase - 0.8) / 0.1);
            } else if (phase >= 0.1 && phase < 0.2) {
                return (float) ((0.2 - phase) / 0.1);
            }
            return 1.0f;
        }
        return 0.0f;
    }

    /**
     * Gets the sun visibility (0.0 = invisible, 1.0 = full visibility).
     * 
     * @return Sun visibility
     */
    public float getSunVisibility() {
        return isDaytime() ? 1.0f - getMoonVisibility() : 0.0f;
    }

    /**
     * Sets the time multiplier (1.0 = normal, 2.0 = double speed, etc.).
     * 
     * @param multiplier Time multiplier
     */
    public void setTimeMultiplier(double multiplier) {
        this.timeMultiplier = Math.max(0.0, multiplier);
    }

    /**
     * Gets the current time multiplier.
     * 
     * @return Time multiplier
     */
    public double getTimeMultiplier() {
        return timeMultiplier;
    }

    /**
     * Pauses or resumes the cycle.
     * 
     * @param paused true to pause
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * Checks if the cycle is paused.
     * 
     * @return true if paused
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Sets the time of day directly.
     * 
     * @param phase 0.0 = midnight, 0.5 = noon
     */
    public void setPhase(double phase) {
        this.cyclePosition = (phase % 1.0) * cycleDuration;
    }

    /**
     * Gets a human-readable time string.
     * 
     * @return Time like "12:00" or "Night"
     */
    public String getTimeString() {
        double phase = getPhase();
        int hours = (int) ((phase * 24 + 6) % 24); // 0.0 = midnight = 0:00
        int minutes = (int) ((phase * 24 * 60) % 60);
        return String.format("%02d:%02d", hours, minutes);
    }

    /**
     * Gets the current period name.
     * 
     * @return "Day", "Night", "Dawn", or "Dusk"
     */
    public String getPeriodName() {
        double phase = getPhase();
        if (phase >= 0.15 && phase < 0.25)
            return "Dawn";
        if (phase >= 0.25 && phase < 0.75)
            return "Day";
        if (phase >= 0.75 && phase < 0.85)
            return "Dusk";
        return "Night";
    }
}
