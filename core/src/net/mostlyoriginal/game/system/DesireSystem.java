package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.game.component.Desire;
import net.mostlyoriginal.game.component.Using;
import net.mostlyoriginal.game.component.module.Exit;
import net.mostlyoriginal.game.component.module.TipBowl;
import net.mostlyoriginal.game.component.module.Toilet;
import net.mostlyoriginal.game.component.module.Urinal;
import net.mostlyoriginal.game.component.state.Dirty;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class DesireSystem extends FluidSystem {

    public static final int MISSING_ENTITY_ID = -1;

    public DesireSystem() {
        super(Aspect.all(Desire.class).exclude(Using.class));
    }

    @Override
    protected void process(E e) {
        if (!e.hasHunt()) {
            startHunt(e);
        }
    }

    private void startHunt(E e) {

        int entityId = MISSING_ENTITY_ID;

        switch (e.desireType()) {
            case LEAVE:
                entityId = randomOf(getExits());
                break;
            case TIP:
                entityId = randomOf(getTipBowls());
                break;
            case POOP:
                entityId = randomOf(getCleanToilet());
                if ( entityId == MISSING_ENTITY_ID ) {
                    entityId = randomOf(getDirtyToilet());
                }
                break;
            case PEE:
                entityId = randomOf(getCleanUrinal());
                if ( entityId == MISSING_ENTITY_ID ) {
                    entityId = randomOf(getDirtyUrinal());
                }
                if ( entityId == MISSING_ENTITY_ID ) {
                    entityId = randomOf(getCleanToilet());
                }
                if ( entityId == MISSING_ENTITY_ID ) {
                    entityId = randomOf(getDirtyToilet());
                }
                break;
        }

        if (entityId != MISSING_ENTITY_ID) {
            e.huntEntityId(entityId);
        }
    }

    private int randomOf(IntBag exits) {
        return !exits.isEmpty() ? exits.get(MathUtils.random(exits.size()-1)) : MISSING_ENTITY_ID;
    }

    private IntBag getCleanToilet() {
        return world
                .getAspectSubscriptionManager()
                .get(Aspect.all(Toilet.class).exclude(Dirty.class)).getEntities();
    }


    private IntBag getDirtyToilet() {
        return world
                .getAspectSubscriptionManager()
                .get(Aspect.all(Toilet.class)).getEntities();
    }


    private IntBag getCleanUrinal() {
        return world
                .getAspectSubscriptionManager()
                .get(Aspect.all(Urinal.class).exclude(Dirty.class)).getEntities();
    }


    private IntBag getDirtyUrinal() {
        return world
                .getAspectSubscriptionManager()
                .get(Aspect.all(Urinal.class)).getEntities();
    }


    private IntBag getExits() {
        return world
                .getAspectSubscriptionManager()
                .get(Aspect.all(Exit.class)).getEntities();
    }

    private IntBag getTipBowls() {
        return world
                .getAspectSubscriptionManager()
                .get(Aspect.all(TipBowl.class)).getEntities();
    }
}
