package net.mostlyoriginal.game.system;

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

    public static final float IDLE_TIME_BEFORE_STOP_USING_MODULES = 0.7f;

    public PlayerControlSystem() {
        super(Aspect.all(Player.class));
    }

    protected UseSystem useSystem;

    public float lastUse = 0;
    public float autoClickCooldown = 0;

    @Override
    protected void process(E player) {
        if (autoClickCooldown > 0 ) autoClickCooldown -= world.delta;
        lastUse += world.delta;
        boolean justPressedAction = (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE));
        boolean pressedAction = (Gdx.input.isKeyPressed(Input.Keys.E) || Gdx.input.isKeyPressed(Input.Keys.SPACE));
        if (!player.isMoving()
                && (justPressedAction || pressedAction) ) {
            if (justPressedAction || autoClickCooldown <= 0) {
                autoClickCooldown = 0.25f;
                startUsingModule(player);
            }
            lastUse=0;
        }

        if (player.hasUsing()) {
            if ( lastUse >= IDLE_TIME_BEFORE_STOP_USING_MODULES) {
                stopUsingModule(player);
            }
        } else {
            if (!player.isMoving()) {
                if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    shiftPosition(player, -1);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    shiftPosition(player, 1);
                }
            }
            moveToModule(player);
        }
    }

    private void stopUsingModule(E player) {
        useSystem.stopBeingUsed(getModule(player),player);
    }

    private void startUsingModule(E player) {
        useSystem.startUsing(player, getModule(player));
    }

    private void moveToModule(E player) {
        player.moving(false);
        if (player.playerActiveModuleId() != -1) {
            E module = getModule(player);

            float targetX = module.posX() + module.boundsMinx();
            float movementSpeed = 200 * world.delta;

            if (player.posX() + movementSpeed < targetX) {
                player.posX(player.posX() + movementSpeed);
                player.animFlippedX(false);
                player.moving(true);
            } else if (player.posX() - movementSpeed > targetX) {
                player.posX(player.posX() - movementSpeed);
                player.animFlippedX(true);
                player.moving(true);
            }
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
