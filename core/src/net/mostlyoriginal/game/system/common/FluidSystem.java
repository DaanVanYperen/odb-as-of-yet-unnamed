package net.mostlyoriginal.game.system.common;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.game.component.BathroomLevel;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public abstract class FluidSystem extends EntityProcessingSystem {

    public FluidSystem() {
        super(Aspect.all());
    }

    public FluidSystem(Aspect.Builder aspect) {
        super(aspect);
    }

    @Override
    protected void process(Entity e) {
        process(E(e));
    }

    protected abstract void process(E e);
}
