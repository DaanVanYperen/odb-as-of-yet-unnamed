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
    public static final int URINAL_WIDTH = UNIT;
    public static final int TIPS_WIDTH = UNIT;
    public static final int SUPPLY_CLOSET_WIDTH = UNIT * 2;
    public static final int VISITOR_WIDTH = 24;
    public static final int VISITOR_HEIGHT = 38;

    public static final int LAYER_BACKGROUND = 1;
    public static final int LAYER_BEHIND_ACTORS = 5;
    public static final int LAYER_CLOCK = 20;
    public static final int LAYER_TOILET_DOOR = 100;
    public static final int LAYER_ACTORS = 2000;
    public static final int LAYER_PLAYER = 1000;
    public static final int LAYER_ICONS = 2100;
    public static final int LAYER_ACTORS_BUSY = 90;

    public static final int DEFAULT_MODULE_HEIGHT = UNIT * 5 + 16;
    public static final int PLAYER_WIDTH = 24;
    public static final int PLAYER_HEIGHT = 36;
    public static final int MAIN_DOOR_WIDTH = 24;
    public static final float WALK_FRAME_DURATION = 0.06f;
    public static final float PLAYER_IDLE_FRAME_DURATION = 0.2f;
    public static final float PLAYER_WALK_FRAME_DURATION = 0.06f;

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

        add("module_part_urinal", 416,64,32,32, 1);
        add("module_part_urinal_dirty", 416,32,32,32, 1);

        add("module_part_main_door_closed", 36, 217, MAIN_DOOR_WIDTH, 54, 1);
        add("module_part_main_door_open", 68, 217, 18, 54, 1);

        add("module_part_door_closed", 324, 96, 23, 43, 1);
        add("module_part_door_open", 356, 96, 12, 43, 1);
        add("module_part_handicap_door_closed", 388, 96, 23, 43, 1);
        add("module_part_handicap_door_open", 420, 96, 12, 43, 1);


        add("player_walking_toiletpaper", 32, 288, PLAYER_WIDTH, PLAYER_HEIGHT, 6).setFrameDuration(PLAYER_WALK_FRAME_DURATION);
        add("player_walking_plunger", 176, 288, PLAYER_WIDTH, PLAYER_HEIGHT, 6).setFrameDuration(PLAYER_WALK_FRAME_DURATION);
        add("player_walking_mop", 320, 288, PLAYER_WIDTH, PLAYER_HEIGHT, 6).setFrameDuration(PLAYER_WALK_FRAME_DURATION);

        add("player_toiletpaper", 464, 288, PLAYER_WIDTH, PLAYER_HEIGHT, 5).setFrameDuration(PLAYER_IDLE_FRAME_DURATION);
        add("player_plunger", 584, 288, PLAYER_WIDTH, PLAYER_HEIGHT, 5).setFrameDuration(PLAYER_IDLE_FRAME_DURATION);
        add("player_mop", 704, 288, PLAYER_WIDTH, PLAYER_HEIGHT, 5).setFrameDuration(PLAYER_IDLE_FRAME_DURATION);

        //Player, normal walk:
        //x 32, y 288 - w 24, h 36 - six frames
        //Player, plunger walk:
        //x 176, y 288 - w 24, h 36 - six frames
        //Player, mop walk:
        //x 320, y 288 - w 24, h 36 - six frames

        // VISITOR 1

        add("visitor_happy1", 32, 324, 24, 38, 6).setFrameDuration(WALK_FRAME_DURATION);
        add("visitor_neutral1", 32, 362, 24, 38, 6).setFrameDuration(WALK_FRAME_DURATION);
        add("visitor_angry1", 32, 400, 24, 38, 6).setFrameDuration(WALK_FRAME_DURATION);
        add("visitor_enraged1", 32, 438, 24, 38, 6).setFrameDuration(WALK_FRAME_DURATION);

        add("visitor_pee_happy1", 176, 362, 24, 38, 2);
        add("visitor_pee_neutral1", 176, 362, 24, 38, 2);
        add("visitor_pee_angry1", 176, 400, 24, 38, 2);
        add("visitor_pee_enraged1", 176, 438, 24, 38, 2);

        add("visitor_poop_happy1", 224, 324, 24, 38, 1);
        add("visitor_poop_neutral1", 224, 362, 24, 38, 1);
        add("visitor_poop_angry1", 224, 400, 24, 38, 1);
        add("visitor_poop_enraged1", 224, 438, 24, 38, 1);


        // VISITOR 2

        add("visitor_happy2", 32, 476, 24, 38, 6).setFrameDuration(WALK_FRAME_DURATION);
        add("visitor_neutral2", 32, 514, 24, 38, 6).setFrameDuration(WALK_FRAME_DURATION);
        add("visitor_angry2", 32, 552, 24, 38, 6).setFrameDuration(WALK_FRAME_DURATION);
        add("visitor_enraged2", 32, 590, 24, 38, 6).setFrameDuration(WALK_FRAME_DURATION);

        add("visitor_pee_happy2", 176, 476, 24, 38, 2);
        add("visitor_pee_neutral2", 176, 514, 24, 38, 2);
        add("visitor_pee_angry2", 176, 552, 24, 38, 2);
        add("visitor_pee_enraged2", 176, 590, 24, 38, 2);

        add("visitor_poop_happy2", 224, 476, 24, 38, 1);
        add("visitor_poop_neutral2", 224, 514, 24, 38, 1);
        add("visitor_poop_angry2", 224, 552, 24, 38, 1);
        add("visitor_poop_enraged2", 224, 590, 24, 38, 1);



//        Guest 3 happy walk animation:
//        32,628,24,38,6
//        Guest 3 neutral walk animation:
//        32,666,24,38,6
//        Guest 3 sad walk animation:
//        32,704,24,38,6
//        Guest 3 RAGE walk animation:
//        32,742,24,38,6
//
//        Guest 3 happy peeing animation:
//        176,628,24,38,2
//        Guest 3 neutral peeing animation:
//        176,666,24,38,2
//        Guest 3 sad peeing animation:
//        176,704,24,38,2
//        Guest 3 RAGE peeing animation:
//        176,742,24,38,2
//
//        Guest 3 happy poopface:
//        224,628,24,38
//        Guest 3 neutral poopface:
//        224,666,24,38
//        Guest 3 sad poopface:
//        224,704,24,38
//        Guest 3 RAGE poopface:
//        224,742,24,38
//
        add("coin_0", 96 - 16, 224 - 32, 16, 16, 1); // empty
        add("coin_1", 96, 224 - 32, 16, 16, 1);
        add("coin_2", 96 + 16, 224 - 32, 16, 16, 1);
        add("coin_3", 96 + 32, 224 - 32, 16, 16, 1);
        add("coin_4", 96 + 48, 224 - 32, 16, 16, 1);
        add("coin_5", 96 + 64, 224 - 32, 16, 16, 1);

        add("poster_1", 96, 224, 26, 42, 1);
        add("poster_2", 123, 224, 26, 42, 1);
        add("poster_3", 150, 224, 26, 42, 1);

        add("icon_mop", 150 + 26, 224 - 32, 16, 32, 1);
        add("icon_plunger", 150 + 26 + 16, 224 - 32, 16, 32, 1);
        add("icon_plunger_and_mop", 256, 192, 16, 32, 1);
        //
//        Mop + Plunger icon
//

        add("icon_forbidden", 150 + 26 + 32, 224 - 32, 16, 16, 1);
        add("icon_sad", 150 + 26 + 32, 224 - 16, 16, 16, 1);
        add("icon_sad2", 150 + 26 + 32 + 16, 224 - 16, 16, 16, 1);
        add("icon_coin", 150 + 26 + 48, 224 - 32, 16, 16, 1);
        add("icon_pointer", 150 + 26 + 64, 224 - 32, 16, 16, 1);

        add("icon_button", 256, 224, 16, 32, 3).setFrameDuration(1f / 15f);

        add("progress_0", 192, 242, 21, 4, 1);
        add("progress_25", 192, 247, 21, 4, 1);
        add("progress_50", 192, 252, 21, 4, 1);
        add("progress_75", 192, 257, 21, 4, 1);
        add("progress_100", 192, 262, 21, 4, 1);

        add("clock_face", 150 + 26 + 16, 224, 15, 15, 1);
        add("clock_large_hand", 150 + 26 + 32, 224, 15, 15, 1);
        add("clock_small_hand", 150 + 26 + 48, 224, 15, 15, 1);

        add("icon_press", 150 + 26 + 80, 224, 16, 32, 3);
    }
}
