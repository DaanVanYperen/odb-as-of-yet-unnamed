package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.component.BathroomLevel;
import net.mostlyoriginal.game.component.Effect;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public class LevelSetupSystem extends FluidSystem {

    public static final int Y_OFFSET = 25;
    public static final int TOILET_Y = 48;
    public Level activeLevel;

    public static class Level {
        public String name;
        public BathroomLevel.Type[] level;
        public int lossCount = 5;
        public float timeBetweenSpawnsEasiest=12;
        public float timeBetweenSpawnsHardest=2;
        private int minCount=1;
        private int maxCount=1;
        public float clockSpeed=10;

        public Level(String name, BathroomLevel.Type[] level) {
            this.name = name;
            this.level = level;
        }

        public Level lossCount(int count ) {
            this.lossCount = count;
            return this;
        }

        public Level spawnDelay(float easy, float hard)
        {
            this.timeBetweenSpawnsEasiest =easy;
            this.timeBetweenSpawnsHardest = hard;
            return this;
        }

        public Level spawnCount(int minCount, int maxCount) {
            this.minCount = minCount;
            this.maxCount = maxCount;
            return this;

        }

        public Level clockSpeed(int clockSpeed) {
            this.clockSpeed=clockSpeed;
            return this;
        }
    }

    ;

    private Level level1 = new Level(
            "Stage 2: Toilets are us",
            new BathroomLevel.Type[]{
                    BathroomLevel.Type.ENTRANCE,
                    BathroomLevel.Type.TIPS,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.SUPPLY_CLOSET
            });

    private Level level2 = new Level(
            "Stage 2: Toilets are us",
            new BathroomLevel.Type[]{
                    BathroomLevel.Type.ENTRANCE,
                    BathroomLevel.Type.TIPS,
                    BathroomLevel.Type.SINK,
                    BathroomLevel.Type.URINAL,
                    BathroomLevel.Type.URINAL,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.SUPPLY_CLOSET
            });

    private Level level4 = new Level(
            "Stage X: Zero Tolerance",
            new BathroomLevel.Type[]{
                    BathroomLevel.Type.ENTRANCE,
                    BathroomLevel.Type.TIPS,
                    BathroomLevel.Type.SINK,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.SUPPLY_CLOSET
            }).lossCount(1);

    private Level level3 = new Level(
            "Stage X: Chili Con Carne Convention",
            new BathroomLevel.Type[]{
                    BathroomLevel.Type.ENTRANCE,
                    BathroomLevel.Type.TIPS,
                    BathroomLevel.Type.SINK,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.SINK,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.SUPPLY_CLOSET
            });

    private Level urinalLevel = new Level(
            "Stage 2: Toilets are us",
            new BathroomLevel.Type[]{
                    BathroomLevel.Type.ENTRANCE,
                    BathroomLevel.Type.TIPS,
                    BathroomLevel.Type.URINAL,
                    BathroomLevel.Type.URINAL,
                    BathroomLevel.Type.URINAL,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.SUPPLY_CLOSET
            });


    private Level panicLevel = new Level(
            "Stage X: AHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH!!!",
            new BathroomLevel.Type[]{
                    BathroomLevel.Type.ENTRANCE,
                    BathroomLevel.Type.TIPS,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.URINAL,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.URINAL,
                    BathroomLevel.Type.TOILET,
                    BathroomLevel.Type.SINK,
                    BathroomLevel.Type.SINK,
                    BathroomLevel.Type.SUPPLY_CLOSET
            })
            .clockSpeed(10)
            .spawnCount(1,3)
            .spawnDelay(8,4);


    private Level[] levels = new Level[] {
            panicLevel
    };

    public LevelSetupSystem() {
        super(Aspect.all(BathroomLevel.class));
    }

    @Override
    protected void initialize() {
        super.initialize();

//        E().bathroomLevelModules(level1);
        loadLevel(levels[MathUtils.clamp(GameRules.level-1,0,levels.length-1)]);
    }

    private void loadLevel(Level level) {
        this.activeLevel = level;
        E()
                .bathroomLevelModules(level.level)
                .bathroomLevelName(level.name);

        E()
                .pos(32,20)
                .labelText(level.name)
                .tint(0.3f,0.3f,0.3f,1f)
                .fontFontName("5x5")
                .fontScale(1.5f)
                .renderLayer(GameScreenAssetSystem.LAYER_ICONS);
    }

    private int x = 0;

    @Override
    protected void process(E e) {
        if (!e.bathroomLevelInitialized()) {
            e.bathroomLevelInitialized(true);
            if ( e.bathroomLevelModules() != null) {
                for (BathroomLevel.Type type : e.bathroomLevelModules()) {
                    e.bathroomLevelModuleEntityIds().add(initModule(type));
                }
            }
        }
    }

    private int initModule(BathroomLevel.Type type) {

        int moduleId = -1;

        switch (type) {
            case ENTRANCE:
                moduleId = spawnEntrance(x, Y_OFFSET);
                x = x + GameScreenAssetSystem.ENTRANCE_WIDTH;
                break;
            case TIPS:
                moduleId = spawnTips(x, Y_OFFSET);
                x = x + GameScreenAssetSystem.TIPS_WIDTH;
                break;
            case TOILET:
                moduleId = spawnToilet(x, Y_OFFSET);
                x = x + GameScreenAssetSystem.URINAL_WIDTH;
                break;
            case URINAL:
                moduleId = spawnUrinal(x, Y_OFFSET);
                x = x + GameScreenAssetSystem.TOILET_WIDTH;
                break;
            case SINK:
                moduleId = spawnSink(x, Y_OFFSET);
                x = x + GameScreenAssetSystem.SINK_WIDTH;
                break;
            case SUPPLY_CLOSET:
                moduleId = spawnCloset(x, Y_OFFSET);
                x = x + GameScreenAssetSystem.SUPPLY_CLOSET_WIDTH;
                break;
        }

        return moduleId;
    }

    private int spawnCloset(int x, int y) {
        E closet = E()
                .pos(x, y)
                .render()
                .bounds(0, 0, GameScreenAssetSystem.SUPPLY_CLOSET_WIDTH, GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                .anim("module_storage")
                .interactableDuration(0)
                .inventory();

        E mopandbucket =
                E()
                        .pos(x, y)
                        .render()
                        .effect(Effect.Type.CLEAN);

        E plunger =
                E()
                        .pos(x, y)
                        .render()
                        .effect(Effect.Type.UNCLOG);

        return closet.id();

    }

    private int spawnToilet(int x, int y) {
        E()
                .pos(x, y)
                .render(GameScreenAssetSystem.LAYER_BACKGROUND)
                .anim("module_part_background");

        E toiletBowl = E()
                .pos(x, y + TOILET_Y)
                .render(GameScreenAssetSystem.LAYER_BEHIND_ACTORS)
                .anim("module_part_toilet");

        String doorClosed = "module_part_door_closed";
        String doorOpen = "module_part_door_open";

        if (MathUtils.random(1, 100) < 20) {
            doorOpen = "module_part_handicap_door_open";
            doorClosed = "module_part_handicap_door_closed";
        }

        return E()
                .pos(x + 4, y + TOILET_Y - 11)
                .bounds(2, 0, GameScreenAssetSystem.TOILET_WIDTH, GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                .render(GameScreenAssetSystem.LAYER_TOILET_DOOR)
                .anim(MathUtils.randomBoolean() ? doorClosed : doorOpen)
                .interactable(doorClosed, doorOpen)
                .interactableUseOffsetY(38)
                .toiletBowlId(toiletBowl.id()).id();
    }

    private int spawnUrinal(int x, int y) {
        E()
                .pos(x, y)
                .render(GameScreenAssetSystem.LAYER_BACKGROUND)
                .anim("module_part_background");


        return E()
                .pos(x, y + TOILET_Y + 30)
                .bounds(2, 0, GameScreenAssetSystem.URINAL_WIDTH, GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                .render(GameScreenAssetSystem.LAYER_BEHIND_ACTORS)
                .anim()
                .interactable()
                .interactableDuration(1.5f)
                .interactableUseOffsetY(44)
                .urinal()
                .id();
    }

    private int spawnSink(int x, int y) {
        E()
                .pos(x, y)
                .render(GameScreenAssetSystem.LAYER_BACKGROUND)
                .anim("module_part_background");


        return E()
                .pos(x, y + TOILET_Y + 32)
                .bounds(2, 0, GameScreenAssetSystem.URINAL_WIDTH, GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                .render(GameScreenAssetSystem.LAYER_BEHIND_ACTORS)
                .anim()
                .interactable()
                .interactableDuration(1f)
                .interactableUseOffsetY(44)
                .sink()
                .id();
    }

    private int spawnEntrance(int x, int y) {
        E()
                .pos(x, y)
                .render()
                .bounds(32, 0, GameScreenAssetSystem.ENTRANCE_WIDTH, GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                .anim("module_entrance")
                .id();

        if (MathUtils.random(1, 4) <= 3) {
            E()
                    .pos(x + 64 + 3, y + 59)
                    .render()
                    .bounds(0, 0, GameScreenAssetSystem.ENTRANCE_WIDTH, GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                    .anim("poster_" + MathUtils.random(1, 3))
                    .id();
        }

        return E()
                .pos(x + 32 + 4, y + 34)
                .bounds(16, 16, GameScreenAssetSystem.MAIN_DOOR_WIDTH, 72)
                .render(GameScreenAssetSystem.LAYER_TOILET_DOOR)
                .anim("module_part_main_door_closed")
                .interactableDuration(0.25f)
                .entrance()
                .entranceTimeBetweenSpawnsEasiest(activeLevel.timeBetweenSpawnsEasiest)
                .entranceTimeBetweenSpawnsHardest(activeLevel.timeBetweenSpawnsHardest)
                .entranceMinCount(activeLevel.minCount)
                .entranceMaxCount(activeLevel.maxCount)
                .exit()
                .interactable("module_part_main_door_open", "module_part_main_door_closed")
                .id();

    }


    private int spawnTips(int x, int y) {
        E()
                .pos(x, y + TOILET_Y - 20)
                .bounds(0, 0, GameScreenAssetSystem.PLAYER_WIDTH, GameScreenAssetSystem.PLAYER_HEIGHT)
                .render(GameScreenAssetSystem.LAYER_PLAYER)
                .player()
                .anim("player_plunger");

        E tipbowl = E()
                .pos(x + 2, y + TOILET_Y + 2)
                .render(GameScreenAssetSystem.LAYER_BEHIND_ACTORS)
                .anim("coin_0");

        return E()
                .pos(x, y)
                .render()
                .bounds(0, 0, GameScreenAssetSystem.TIPS_WIDTH, GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                .anim("module_tips")
                .tipBowlBowlId(tipbowl.id())
                .tipBowlMaxAnger(activeLevel.lossCount)
                .interactableDuration(0.0f)
                .id();
    }


}
