package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.component.Hunt;
import net.mostlyoriginal.game.system.common.FluidSystem;

import static com.artemis.E.E;

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
        float movementSpeed = world.delta * GameRules.WALKING_SPEED_VISITORS;
        if (actor.posX() + movementSpeed < huntTarget.posX() + huntTarget.boundsMinx()) {
            actor.posX(actor.posX() + movementSpeed);
            actor.animFlippedX(false);
            return false;
        } else if (actor.posX() - movementSpeed > huntTarget.posX() + huntTarget.boundsMinx()) {
            actor.posX(actor.posX() - movementSpeed);
            actor.animFlippedX(true);
            return false;
        } else if ( !actor.hasUsing() ) { actor.animAge(0); }
        return true;
    }
}
