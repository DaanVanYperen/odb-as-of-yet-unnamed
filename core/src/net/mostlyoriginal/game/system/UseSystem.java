package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.game.component.Desire;
import net.mostlyoriginal.game.component.Interactable;
import net.mostlyoriginal.game.component.Player;
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

        float delta = world.getDelta();
        E actor = getActor(e);
        if ( actor.hasPlayer()) {
            delta = delta * actor.playerTool().multiplier;
        }

        e.inUseDuration(e.inUseDuration() + delta);

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
        if (e.inUseUserId() != -1) {
            applyEffects(e, getActor(e));
        }
        stopBeingUsed(e);
    }

    private E getActor(E e) {
        return E(e.inUseUserId());
    }

    private void applyEffects(E thing, E actor) {

        if (actor.hasPlayer()) {
            finishAsPlayer(thing, actor);
        } else {
            finishAsVisitor(thing, actor);
        }
    }

    private void finishAsPlayer(E thing, E actor) {
        if (thing.isDirty() && actor.playerTool() == Player.Tool.MOP) {
            thing.removeDirty();
        }
        if (thing.isClogged() && actor.playerTool() == Player.Tool.PLUNGER) {
            thing.removeClogged();
        }
        if (thing.isInventory()) {
            actor.playerNextTool();
        }
    }

    private void finishAsVisitor(E thing, E actor) {
        if (thing.hasToilet()) {
            worsenToiletState(thing);
            actor.desireType(Desire.Type.LEAVE);
        }
        if (thing.isExit()) {
            actor.deleteFromWorld();
        }
    }

    private void worsenToiletState(E thing) {
        if (thing.isDirty()) {
            // if dirty, become clogged as well.
            thing.clogged();
        } else if (thing.isClogged()) {
            // if clogged, become dirty as well.
            thing.dirty();
        } else {
            // become dirty or clogged randomly.
            if (MathUtils.randomBoolean()) {
                thing.dirty();
            } else {
                thing.clogged();
            }
        }
    }

    private void stopBeingUsed(E e) {
        if (e.inUseUserId() != -1) {
            getActor(e).removeUsing();
        }
        e.removeInUse();
    }

    public void startUsing(E actor, E item) {
        if ( item.hasInteractable() && !item.hasInUse() ) {

            actor.removeHunt();
            actor.using(item.id());
            item.inUse(actor.id());
        }
    }

}