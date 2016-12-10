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

/**
 * @author Daan van Yperen
 */
public class HuntSystem extends FluidSystem {

    public HuntSystem() {
        super(Aspect.all(Hunt.class, Pos.class, Bounds.class));
    }

    @Override
    protected void process(E e) {

        if (e.huntEntityId() != DesireSystem.MISSING_ENTITY_ID) {
            E huntTarget = E(e.huntEntityId());

            if ( walkTowards(e, huntTarget) )
            {
                interact(e, huntTarget);
            }
        }
    }

    private void interact(E actor, E huntTarget) {
        actor.removeHunt();
        if ( huntTarget.hasToilet() ) {
            if ( actor.desireType() == Desire.Type.POOP ) {
                actor.removeDesire();
                actor.desireType(Desire.Type.LEAVE);
            }
        }
        if ( huntTarget.hasExit() ) {
            if ( actor.desireType() == Desire.Type.LEAVE ) {
                actor.deleteFromWorld();
            }
        }
    }

    private boolean walkTowards(E actor, E huntTarget) {
        float movementSpeed = world.delta * 150;
        if (actor.posX() + movementSpeed < huntTarget.posX()) {
            actor.posX(actor.posX() + movementSpeed);
            return false;
        } else if (actor.posX() - movementSpeed > huntTarget.posX()) {
            actor.posX(actor.posX() - movementSpeed);
            return false;
        }
        return true;
    }
}
