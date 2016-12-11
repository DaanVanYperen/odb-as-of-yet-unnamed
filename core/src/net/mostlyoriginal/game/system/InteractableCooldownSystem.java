package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.game.component.Interactable;
import net.mostlyoriginal.game.system.common.FluidSystem;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public class InteractableCooldownSystem extends FluidSystem {

    public InteractableCooldownSystem() {
        super(Aspect.all(Interactable.class));
    }

    @Override
    protected void process(E e) {
        if ( e.interactableCooldownBefore() > 0 ) {
            e.interactableCooldownBefore(e.interactableCooldownBefore() - world.getDelta());
        }
    }

}
