package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.game.component.Desire;
import net.mostlyoriginal.game.component.Interactable;
import net.mostlyoriginal.game.component.state.InUse;
import net.mostlyoriginal.game.system.common.FluidSystem;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public class UseSystem extends FluidSystem {

    public UseSystem() {
        super(Aspect.all(InUse.class, Interactable.class));
    }

    @Override
    protected void process(E e) {

        e.inUseDuration(e.inUseDuration() + world.getDelta());

        if (e.inUseDuration() >= e.interactableDuration()) {
            finishUsing(e);
        } else {
            continueUsing(e);
        }
    }

    private void continueUsing(E e) {
        if (e.interactableEndAnimId() != null) {
            e.anim(e.interactableStartAnimId());
        }
    }

    private void finishUsing(E e) {
        if (e.interactableEndAnimId() != null) {
            e.anim(e.interactableEndAnimId());
        }
        if ( e.inUseUserId() != -1 ) {
            applyEffects(e, E(e.inUseUserId()));
        }
        stopBeingUsed(e);
    }

    private void applyEffects(E thing, E actor) {
        if ( thing.isToilet()) {
            actor.desireType(Desire.Type.LEAVE);
        }
        if (thing.isExit()) {
            actor.deleteFromWorld();
        }
    }

    private void stopBeingUsed(E e) {
        if ( e.inUseUserId() != -1 ) {
            E(e.inUseUserId()).removeUsing();
        }
        e.removeInUse();
    }
}
