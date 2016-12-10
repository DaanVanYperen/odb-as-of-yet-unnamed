package net.mostlyoriginal.game.system.view;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.Animation;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class GameScreenAssetSystem extends AbstractAssetSystem {
    public static final int DANCING_MAN_HEIGHT = 36;
    public static final int UNIT = 32;
    public static final int ENTRANCE_WIDTH = UNIT * 4;
    public static final int TOILET_WIDTH = UNIT;
    public static final int SUPPLY_CLOSET_WIDTH = UNIT * 2;

    public static final int LAYER_BACKGROUND = 1;
    public static final int LAYER_BEHIND_ACTORS = 5;
    public static final int LAYER_TOILET_DOOR = 10;


    public GameScreenAssetSystem() {
        super("tileset.png");
    }

    @Override
    protected void initialize() {
        super.initialize();

        int DEFAULT_MODULE_HEIGHT = UNIT * 5;

        add("module_entrance", 0, 0, ENTRANCE_WIDTH, DEFAULT_MODULE_HEIGHT, 1);
        add("module_part_backgroundW", UNIT * 4, 0, TOILET_WIDTH, DEFAULT_MODULE_HEIGHT, 1);
        add("module_part_background", UNIT * 5, 0, TOILET_WIDTH, DEFAULT_MODULE_HEIGHT, 1);
        add("module_part_backgroundE", UNIT * 6, 0, TOILET_WIDTH, DEFAULT_MODULE_HEIGHT, 1);
        add("module_storage", UNIT * 7, 0, SUPPLY_CLOSET_WIDTH, DEFAULT_MODULE_HEIGHT, 1);
        add("module_part_toilet", UNIT * 9, 0, UNIT, DEFAULT_MODULE_HEIGHT, 1);
        add("module_part_door_closed", UNIT * 10, 0, UNIT, DEFAULT_MODULE_HEIGHT, 1);
        add("module_part_door_open", UNIT * 11, 0, UNIT, DEFAULT_MODULE_HEIGHT, 1);
    }
}
