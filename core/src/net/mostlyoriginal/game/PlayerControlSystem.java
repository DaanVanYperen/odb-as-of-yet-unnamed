package net.mostlyoriginal.game;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import net.mostlyoriginal.game.component.BathroomLevel;
import net.mostlyoriginal.game.component.Player;
import net.mostlyoriginal.game.system.UseSystem;
import net.mostlyoriginal.game.system.common.FluidSystem;

import static com.artemis.E.*;

/**
 * @author Daan van Yperen
 */
public class PlayerControlSystem extends FluidSystem {
    public PlayerControlSystem() {
        super(Aspect.all(Player.class));
    }

    protected UseSystem useSystem;

    @Override
    protected void process(E player) {
        if (!player.hasUsing()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                shiftPosition(player, -1);
                player.animFlippedX(true);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                shiftPosition(player, 1);
                player.animFlippedX(false);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                startUsingModule(player);
            }
        }
        moveToModule(player);
    }

    private void startUsingModule(E player) {
        useSystem.startUsing(player, getModule(player));
    }

    private void moveToModule(E player) {
        if (player.playerActiveModuleId() != -1) {
            E module = getModule(player);
            player.posX(module.posX() + module.boundsMinx());
        }
    }

    private E getModule(E player) {
        return E(player.playerActiveModuleId());
    }

    private void shiftPosition(E player, int offset) {
        E bathroomLevel = getBathroomLevel();

        int moduleIndex = player.playerModuleIndex() + offset;
        int moduleCount = bathroomLevel.bathroomLevelModuleEntityIds().size();

        if (moduleIndex >= 1 && moduleIndex < moduleCount) {
            player.playerModuleIndex(moduleIndex);
            player.playerActiveModuleId(bathroomLevel.bathroomLevelModuleEntityIds().get(moduleIndex));
        }
    }

    public E getBathroomLevel() {
        return E(world.getAspectSubscriptionManager().get(Aspect.all(BathroomLevel.class)).getEntities().get(0));
    }
}
