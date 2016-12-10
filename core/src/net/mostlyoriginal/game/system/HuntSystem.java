package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.component.Desire;
import net.mostlyoriginal.game.component.Hunt;
import net.mostlyoriginal.game.component.module.Exit;
import net.mostlyoriginal.game.component.module.Toilet;
import net.mostlyoriginal.game.system.common.FluidSystem;

import static com.artemis.E.E;
import static com.badlogic.gdx.scenes.scene2d.ui.Table.Debug.actor;

/**
 * @author Daan van Yperen
 */
public class HuntSystem extends FluidSystem {

    public HuntSystem() {
        super(Aspect.all(Hunt.class, Pos.class, Bounds.class));
    }

    protected UseSystem useSystem;


    @Override
    protected void process(E e) {

        if (e.huntEntityId() != DesireSystem.MISSING_ENTITY_ID) {
            E huntTarget = E(e.huntEntityId());

            if ( walkTowards(e, huntTarget) )
            {
                if ( !huntTarget.hasInUse() ) {
                    useSystem.startUsing(e, huntTarget);
                } else {
                    // hunt no longer valid. hunt something else!
                    e.removeHunt();
                }
            }
        }
    }

    private boolean walkTowards(E actor, E huntTarget) {
        float movementSpeed = world.delta * 150;
        if (actor.posX() + movementSpeed < huntTarget.posX() + huntTarget.boundsMinx()) {
            actor.posX(actor.posX() + movementSpeed);
            actor.animFlippedX(false);
            return false;
        } else if (actor.posX() - movementSpeed > huntTarget.posX() + huntTarget.boundsMinx()) {
            actor.posX(actor.posX() - movementSpeed);
            actor.animFlippedX(true);
            return false;
        }
        return true;
    }
}
