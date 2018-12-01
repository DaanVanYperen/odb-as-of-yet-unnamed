package net.mostlyoriginal.game;

import com.badlogic.gdx.audio.Music;

/**
 * @author Daan van Yperen
 */
public abstract class GameRules {

    public static final int PERCENTAGE_CHANCE_OF_SINK_DIRTY_ESCALATION = 50;
    public static final int PERCENTAGE_CHANCE_OF_URINAL_DIRTY_ESCALATION = 50;
    public static final int PERCENTAGE_CHANCE_OF_TOILET_DIRTY_ESCALATION = 75;
    public static final int WALKING_SPEED_VISITORS = 50;

    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    public static final int CAMERA_ZOOM = 2;


    public static int lastScore = -1;
    public static int level = -1;
    public static Music music;
}
