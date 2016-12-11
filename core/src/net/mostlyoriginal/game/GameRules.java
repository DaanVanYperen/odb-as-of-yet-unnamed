package net.mostlyoriginal.game;

/**
 * @author Daan van Yperen
 */
public abstract class GameRules {

    public static final int PERCENTAGE_CHANCE_OF_SINK_DIRTY_ESCALATION = 50;
    public static final int PERCENTAGE_CHANCE_OF_URINAL_DIRTY_ESCALATION = 50;
    public static final int PERCENTAGE_CHANCE_OF_TOILET_DIRTY_ESCALATION = 75;
    public static final int WALKING_SPEED_VISITORS = 50;

    public static final int SCREEN_WIDTH = 1000;
    public static final int SCREEN_HEIGHT = 480;


    public static int lastScore = -1;
    public static int level = -1;
}
