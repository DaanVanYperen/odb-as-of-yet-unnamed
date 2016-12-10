package net.mostlyoriginal.game;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import net.mostlyoriginal.game.component.BathroomLevel;
import net.mostlyoriginal.game.component.Player;
import net.mostlyoriginal.game.system.common.FluidSystem;

import static com.artemis.E.*;

/**
 * @author Daan van Yperen
 */
public class PlayerControlSystem extends FluidSystem {
    public PlayerControlSystem() {
        super(Aspect.all(Player.class));
    }

    @Override
    protected void process(E player) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            shiftPosition(player, -1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            shiftPosition(player, 1);
        }
        moveToModule(player);
    }

    private void moveToModule(E player) {
        if (player.playerActiveModuleId() != -1) {
            E module = E(player.playerActiveModuleId());
            player.posX(module.posX());
        }
    }

    private void shiftPosition(E player, int offset) {
        E bathroomLevel = getBathroomLevel();

        int moduleIndex = player.playerModuleIndex() + offset;
        int moduleCount = bathroomLevel.bathroomLevelModuleEntityIds().size();

        if (moduleIndex >= 0 && moduleIndex < moduleCount) {
            player.playerModuleIndex(moduleIndex);
            player.playerActiveModuleId(bathroomLevel.bathroomLevelModuleEntityIds().get(moduleIndex));
        }
    }

    public E getBathroomLevel() {
        return E(world.getAspectSubscriptionManager().get(Aspect.all(BathroomLevel.class)).getEntities().get(0));
    }
}
