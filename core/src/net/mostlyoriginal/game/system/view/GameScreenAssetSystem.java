package net.mostlyoriginal.game.system.view;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.game.GameRules;

/**
 * @author Daan van Yperen
 */
@Wire
public class GameScreenAssetSystem extends AbstractAssetSystem {
    public static final int DANCING_MAN_HEIGHT = 36;
    public static final int UNIT = 32;
    public static final int ENTRANCE_WIDTH = UNIT * 3;
    public static final int TOILET_WIDTH = UNIT;
    public static final int SINK_WIDTH = UNIT;
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
    public static final float WALK_FRAME_DURATION = 0.03f * (150f / GameRules.WALKING_SPEED_VISITORS);
    public static final float PLAYER_IDLE_FRAME_DURATION = 0.2f;
    public static final float PLAYER_USE_FRAME_DURATION = 0.2f;
    public static final float PLAYER_WALK_FRAME_DURATION = 0.06f;
    private static final float WASH_FRAME_DURATION = 0.6f;
    public static final float LOW_VOLUME = 0.01f;
    private Music music;

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
        add("module_part_toilet_dirty_clogged_1", 416,152,32,72, 1);
        add("module_part_toilet_dirty_clogged_2", 448,152,32,72, 1);

        add("module_part_toilet_dirty", 320, 152, 32, 72, 1);
        add("module_part_toilet_clogged", 352, 152, 32, 72, 2);

        add("module_part_urinal", 416, 64, 32, 32, 1);
        add("module_part_urinal_dirty", 416, 32, 32, 32, 1);

        add("module_part_sink", 448, 32, 32, 64, 1);
        add("module_part_sink_dirty", 512, 32, 32, 64, 1);
        add("module_part_sink_gross", 544, 32, 32, 64, 1);

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

//        add("player_use_toiletpaper", 464, 288, PLAYER_WIDTH, PLAYER_HEIGHT, 5).setFrameDuration(PLAYER_IDLE_FRAME_DURATION);
        add("player_using_plunger", 824, 288, 24, 36, 2).setFrameDuration(PLAYER_USE_FRAME_DURATION);
        add("player_using_mop", 872, 288, 24, 36, 2).setFrameDuration(PLAYER_USE_FRAME_DURATION);

        add("player_winning", 920,288,27,36,2).setFrameDuration(PLAYER_USE_FRAME_DURATION);
        add("player_losing", 974,288,20,36,2).setFrameDuration(PLAYER_USE_FRAME_DURATION);

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


        // VISITOR 3

        add("visitor_happy3", 32, 628, 24, 38, 6).setFrameDuration(WALK_FRAME_DURATION);
        add("visitor_neutral3", 32, 666, 24, 38, 6).setFrameDuration(WALK_FRAME_DURATION);
        add("visitor_angry3", 32, 704, 24, 38, 6).setFrameDuration(WALK_FRAME_DURATION);
        add("visitor_enraged3", 32, 742, 24, 38, 6).setFrameDuration(WALK_FRAME_DURATION);

        add("visitor_pee_happy3", 176, 628, 24, 38, 2);
        add("visitor_pee_neutral3", 176, 666, 24, 38, 2);
        add("visitor_pee_angry3", 176, 704, 24, 38, 2);
        add("visitor_pee_enraged3", 176, 742, 24, 38, 2);

        add("visitor_poop_happy3", 224, 628, 24, 38, 1);
        add("visitor_poop_neutral3", 224, 666, 24, 38, 1);
        add("visitor_poop_angry3", 224, 704, 24, 38, 1);
        add("visitor_poop_enraged3", 224, 742, 24, 38, 1);

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

        add("visitor_wash_happy1", 248, 324, 24, 38, 2).setFrameDuration(WASH_FRAME_DURATION);
        add("visitor_wash_neutral1", 248, 362, 24, 38, 2).setFrameDuration(WASH_FRAME_DURATION);
        add("visitor_wash_angry1", 248, 400, 24, 38, 2).setFrameDuration(WASH_FRAME_DURATION);
        add("visitor_wash_enraged1", 248, 438, 24, 38, 2).setFrameDuration(WASH_FRAME_DURATION);

        add("visitor_wash_happy2", 248, 476, 24, 38, 2).setFrameDuration(WASH_FRAME_DURATION);
        add("visitor_wash_neutral2", 248, 514, 24, 38, 2).setFrameDuration(WASH_FRAME_DURATION);
        add("visitor_wash_angry2", 248, 552, 24, 38, 2).setFrameDuration(WASH_FRAME_DURATION);
        add("visitor_wash_enraged2", 248, 590, 24, 38, 2).setFrameDuration(WASH_FRAME_DURATION);

        add("visitor_wash_happy3", 248, 628, 24, 38, 2).setFrameDuration(WASH_FRAME_DURATION);
        add("visitor_wash_neutral3", 248, 666, 24, 38, 2).setFrameDuration(WASH_FRAME_DURATION);
        add("visitor_wash_angry3", 248, 704, 24, 38, 2).setFrameDuration(WASH_FRAME_DURATION);
        add("visitor_wash_enraged3", 248, 704, 24, 38, 2).setFrameDuration(WASH_FRAME_DURATION);

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

        sfxVolume = 0.3f;


        loadSounds(new String[]{
                "coin_drop_01",
                "coin_drop_02",
                "coin_drop_03",
                "coin_drop_04",
                "coin_drop_05",
                "coin_drop_06",
                "coin_drop_07",
                "coin_drop_08",
                "coin_drop_09",
                "coin_drop_10",
                "coin_drop_11",
                "coin_drop_12",
                "door_close_01",
                "door_close_02",
                "door_close_03",
                "door_open_01",
                "door_open_02",
                "door_open_03",
                "mop_01",
                "mop_02",
                "mop_03",
                "mop_04",
                "mop_05",
                "plunger_01",
                "plunger_02",
                "plunger_03",
                "plunger_04",
                "plunger_05",
                "supplies_01",
                "supplies_02",
                "supplies_03",
                "poop_02",
                "poop_01",
                "poop_03",
                "poop_04",
                "poop_05",
                "poop_06",
                "pee_drips_01",
                "pee_long_01",
                "pee_long_02",
                "pee_long_03",
                "pee_short_01",
                "pee_short_02",
                "pee_short_03",
                "toilet_flush_01",
                "guest_angry_01",
                "guest_angry_02",
                "guest_angry_03",
                "guest_angry_04",
                "guest_angry_05",
                "guest_angry_06",
                "guest_angry_07",
                "guest_angry_08",
                "guest_angry_09",
                "handwash_01",
                "handwash_02",
                "handwash_03",

                "footstep_left_01",
                "footstep_left_02",
                "footstep_right_01",
                "footstep_right_02",

                "progress_bar_25",
                "progress_bar_50",
                "progress_bar_75",
                "progress_bar_100",
                "victory",
                "defeat"

        });

        if (GameRules.music == null) {
            GameRules.music = Gdx.audio.newMusic(Gdx.files.internal(
                    "sfx/i_clean_toilet_background_music_0"+MathUtils.random(1,3)+".mp3"));
        }
        GameRules.music.stop();
        GameRules.music.setLooping(true);
        GameRules.music.play();
        GameRules.music.setPan(0, 0.1f);


//        Toilet large poop:
//        416,152,32,72
//        Toilet HUGE poop:
//        448,152,32,72EEEE
//
//        Fly variant 1:
//        216,240,2,2
//        Fly variant 2:
//        219,240,2,2
//        Fly variant 3:
//        216,243,2,2
//        Fly variant 4:
//        219,243,2,2
//
//        Sink dirty:
//        512,32,32,64
//        Sink GROSS:
//        544,32,32,64
    }

    public void playSfx(String... names) {
        playSfx(names[MathUtils.random(0, names.length - 1)], sfxVolume);
    }

    public void playSfx(float volume, String... names) {
        playSfx(names[MathUtils.random(0, names.length - 1)]);
    }


    public void playSfx(String name, float volume) {
        if (volume > 0) {
            Sound sfx = getSfx(name);
            if (sfx != null) {
                sfx.stop();
                sfx.play(volume, MathUtils.random(1f, 1.04f), 0);
            }
        }
    }


    public void playDoorCloseSfx() {
        playSfx(LOW_VOLUME,
                "door_close_01",
                "door_close_02",
                "door_close_03");
    }


    public void playDoorOpenSfx() {
        playSfx(LOW_VOLUME,
                "door_open_01",
                "door_open_02",
                "door_open_03");
    }

    public void playCoinSfx() {
        playSfx(
                "coin_drop_01",
                "coin_drop_02",
                "coin_drop_03",
                "coin_drop_04",
                "coin_drop_05",
                "coin_drop_06",
                "coin_drop_07",
                "coin_drop_08",
                "coin_drop_09",
                "coin_drop_10",
                "coin_drop_11",
                "coin_drop_12"
        );
    }

    public void playMopSfx() {
        playSfx(
                "mop_01",
                "mop_02",
                "mop_03",
                "mop_04",
                "mop_05");
    }

    public void playPlungerSfx() {
        playSfx( "plunger_01",
                "plunger_02",
                "plunger_03",
                "plunger_04",
                "plunger_05");
    }

    public void playSuppliesSfx() {
        playSfx( "supplies_01",
                "supplies_02",
                "supplies_03");
    }

    public void playPoopSfx() {
        playSfx(
                "poop_01",
                "poop_02",
                "poop_03",
                "poop_04",
                "poop_05",
                "poop_06"
        );
    }

    public void playFlushSfx() {
        playSfx(
                "toilet_flush_01"
        );
    }

    public void playPeeSfx() {
        playSfx("pee_drips_01",
//                "pee_long_01",
//                "pee_long_02",
//                "pee_long_03",
                "pee_short_01",
                "pee_short_02",
                "pee_short_03"
                );
    }


    public void playGuestAngrySfx() {
        playSfx("guest_angry_01",
                "guest_angry_02",
                "guest_angry_03",
                "guest_angry_04",
                "guest_angry_05",
                "guest_angry_06",
                "guest_angry_07",
                "guest_angry_08",
                "guest_angry_09");
    }


    public void playSinkSfx() {
        playSfx("handwash_01",
                "handwash_02",
                "handwash_03");
    }

    public void playVictorySfx() {
        playSfx("victory");
    }

    public void playDefeatSfx() {
        playSfx("defeat");
    }

}
