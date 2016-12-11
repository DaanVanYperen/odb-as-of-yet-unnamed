package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.game.component.BathroomLevel;
import net.mostlyoriginal.game.component.Effect;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public class SetupWorldSystem extends FluidSystem {

    public static final int Y_OFFSET = 25;
    public static final int TOILET_Y = 48;

    private BathroomLevel.Type[] level1 = {
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
    };

    private BathroomLevel.Type[] level2 = {
            BathroomLevel.Type.ENTRANCE,
            BathroomLevel.Type.TIPS,
            BathroomLevel.Type.TOILET,
            BathroomLevel.Type.TOILET,
            BathroomLevel.Type.TOILET,
            BathroomLevel.Type.SUPPLY_CLOSET
    };

    public SetupWorldSystem() {
        super(Aspect.all(BathroomLevel.class));
    }

    @Override
    protected void initialize() {
        super.initialize();

//        E().bathroomLevelModules(level1);
        E().bathroomLevelModules(level2);
    }

    private int x = 0;

    @Override
    protected void process(E e) {
        if (!e.bathroomLevelInitialized()) {
            e.bathroomLevelInitialized(true);
            for (BathroomLevel.Type type : e.bathroomLevelModules()) {
                e.bathroomLevelModuleEntityIds().add(initModule(type));
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
                x = x + GameScreenAssetSystem.TOILET_WIDTH;
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
                .bounds(0, 0,GameScreenAssetSystem.SUPPLY_CLOSET_WIDTH,GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                .anim("module_storage")
                .interactableDuration(0.01f)
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
                .pos(x, y+TOILET_Y)
                .render(GameScreenAssetSystem.LAYER_BEHIND_ACTORS)
                .anim("module_part_toilet");

        return E()
                .pos(x+4, y+TOILET_Y-11)
                .bounds(2, 0,GameScreenAssetSystem.TOILET_WIDTH,GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                .render(GameScreenAssetSystem.LAYER_TOILET_DOOR)
                .anim(MathUtils.randomBoolean() ? "module_part_door_closed" : "module_part_door_open")
                .interactable("module_part_door_closed", "module_part_door_open")
                .interactableUseOffsetY(38)
                .toiletBowlId(toiletBowl.id()).id();
    }

    private int spawnEntrance(int x, int y) {
        E()
                .pos(x, y)
                .render()
                .bounds(32, 0,GameScreenAssetSystem.ENTRANCE_WIDTH,GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                .anim("module_entrance")
                .id();


        return E()
                .pos(x + 32 +4, y+34)
                .bounds(16, 16,GameScreenAssetSystem.MAIN_DOOR_WIDTH,72)
                .render(GameScreenAssetSystem.LAYER_TOILET_DOOR)
                .anim("module_part_main_door_closed")
                .interactableDuration(0.25f)
                .entrance()
                .exit()
                .interactable("module_part_main_door_open", "module_part_main_door_closed")
                .id();

    }


    private int spawnTips(int x, int y) {
        E()
                .pos(x,y+TOILET_Y-20)
                .bounds(0, 0,GameScreenAssetSystem.PLAYER_WIDTH,GameScreenAssetSystem.PLAYER_HEIGHT)
                .render(GameScreenAssetSystem.LAYER_PLAYER)
                .player()
                .anim("player_plunger");

        return E()
                .pos(x, y)
                .render()
                .bounds(0, 0,GameScreenAssetSystem.TIPS_WIDTH,GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                .anim("module_tips")
                .interactable()
                .id();
    }


}
