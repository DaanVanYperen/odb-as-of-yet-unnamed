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

    private BathroomLevel.Type[] level1 = {
            BathroomLevel.Type.ENTRANCE,
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

    public SetupWorldSystem() {
        super(Aspect.all(BathroomLevel.class));
    }

    @Override
    protected void initialize() {
        super.initialize();

        E().bathroomLevelModules(level1);
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
                moduleId = spawnEntrance(x, 0);
                x = x + GameScreenAssetSystem.ENTRANCE_WIDTH;
                break;
            case TOILET:
                moduleId = spawnToilet(x, 0);
                x = x + GameScreenAssetSystem.TOILET_WIDTH;
                break;
            case SUPPLY_CLOSET:
                moduleId = spawnCloset(x, 0);
                x = x + GameScreenAssetSystem.SUPPLY_CLOSET_WIDTH;
                break;
        }

        return moduleId;
    }

    private int spawnCloset(int x, int y) {
        E closet = E()
                .pos(x, y)
                .render()
                .bounds(0,0,GameScreenAssetSystem.SUPPLY_CLOSET_WIDTH,GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                .anim("module_storage")
                .interactable()
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
        E()
                .pos(x, y)
                .render(GameScreenAssetSystem.LAYER_BEHIND_ACTORS)
                .anim("module_part_toilet");

        return E()
                .pos(x, y)
                .bounds(0,0,GameScreenAssetSystem.TOILET_WIDTH,GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                .render(GameScreenAssetSystem.LAYER_TOILET_DOOR)
                .anim(MathUtils.randomBoolean() ? "module_part_door_closed" : "module_part_door_open")
                .interactable("module_part_door_closed", "module_part_door_open")
                .toilet().id();
    }

    private int spawnEntrance(int x, int y) {
        E()
                .pos(x,y+10)
                .bounds(0,0,GameScreenAssetSystem.PLAYER_WIDTH,GameScreenAssetSystem.PLAYER_HEIGHT)
                .render(GameScreenAssetSystem.LAYER_PLAYER)
                .player()
                .anim("player_plunger");

        return E()
                .pos(x, y)
                .render()
                .bounds(0,0,GameScreenAssetSystem.ENTRANCE_WIDTH,GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                .anim("module_entrance")
                .interactable()
                .entrance()
                .exit()
                .id();
    }


}
