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
    public static final int ENTRANCE_WIDTH = UNIT * 3;
    public static final int TOILET_WIDTH = UNIT;
    public static final int TIPS_WIDTH = UNIT;
    public static final int SUPPLY_CLOSET_WIDTH = UNIT * 2;
    public static final int VISITOR_WIDTH = 24;
    public static final int VISITOR_HEIGHT = 38;

    public static final int LAYER_BACKGROUND = 1;
    public static final int LAYER_BEHIND_ACTORS = 5;
    public static final int LAYER_TOILET_DOOR = 10;
    public static final int LAYER_ACTORS = 2000;
    public static final int LAYER_PLAYER = 1000;

    public static final int DEFAULT_MODULE_HEIGHT = UNIT * 5 + 16;
    public static final int PLAYER_WIDTH = 24;
    public static final int PLAYER_HEIGHT = 36;
    public static final int MAIN_DOOR_WIDTH = 24;

    public GameScreenAssetSystem() {
        super("tileset.png");
    }

    @Override
    protected void initialize() {
        super.initialize();

        add("module_entrance", 0, 0, ENTRANCE_WIDTH, DEFAULT_MODULE_HEIGHT, 1);
        add("module_tips", ENTRANCE_WIDTH, 0, TIPS_WIDTH, DEFAULT_MODULE_HEIGHT, 1);
        add("module_part_backgroundW", UNIT * 4, 0, TOILET_WIDTH, DEFAULT_MODULE_HEIGHT, 1);
        add("module_part_background", UNIT * 5, 0, TOILET_WIDTH, DEFAULT_MODULE_HEIGHT, 1);
        add("module_part_backgroundE", UNIT * 6, 0, TOILET_WIDTH, DEFAULT_MODULE_HEIGHT, 1);
        add("module_storage", UNIT * 7, 0, SUPPLY_CLOSET_WIDTH, DEFAULT_MODULE_HEIGHT, 1);

        add("module_part_toilet", 288, 56, 32, 72, 1);
        add("module_part_toilet_dirty_clogged", 288, 152, 32, 72, 1);
        add("module_part_toilet_dirty", 320, 152, 32, 72, 1);
        add("module_part_toilet_clogged", 352, 152, 32, 72, 2);

        add("module_part_main_door_closed", 36, 217, MAIN_DOOR_WIDTH, 54, 1);
        add("module_part_main_door_open",68, 217,18,54, 1);

        add("module_part_door_closed", 324, 96, 23, 43, 1);
        add("module_part_door_open", 356, 96, 12, 43, 1);
        add("module_part_handicap_door_closed", 388, 96, 23, 43, 1);
        add("module_part_handicap_door_open", 420, 96, 12, 43, 1);

        
        add("player_toiletpaper", 32, 288, PLAYER_WIDTH, PLAYER_HEIGHT, 6);
        add("player_plunger", 176, 288, PLAYER_WIDTH, PLAYER_HEIGHT, 6);
        add("player_mop", 320, 288, PLAYER_WIDTH, PLAYER_HEIGHT, 6);

        //Player, normal walk:
        //x 32, y 288 - w 24, h 36 - six frames
        //Player, plunger walk:
        //x 176, y 288 - w 24, h 36 - six frames
        //Player, mop walk:
        //x 320, y 288 - w 24, h 36 - six frames

        add("visitor", 32, 362, 24, 38, 6);
        add("visitor_busy", 176, 362, 24, 38, 2);


//        Guest happy walk animation:
//        32, 324 24, 38 
//        Guest neutral walk animation:
//        32, 362 24, 38
//        Guest sad walk animation:
//        32, 400 24, 38 
//        Guest RAGE walk animation:
//        32, 438 24, 38 
//
//        Guest happpeeing animation:
//        176, 324 24, 38 
//        Guest neutral peeing animation:
//        176, 362 24, 38
//        Guest sad peeing animation:
//        176, 400 24, 38 
//        Guest RAGE peeing animation:
//        176, 438 24, 38 
    }
}
