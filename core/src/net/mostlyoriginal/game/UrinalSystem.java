package net.mostlyoriginal.game;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.game.component.module.Urinal;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class UrinalSystem extends FluidSystem {
    public UrinalSystem() {
        super(Aspect.all(Urinal.class));
    }

    @Override
    protected void process(E e) {
        if ( e.hasDirty() ) {
            e.anim("module_part_urinal_dirty");
        } else {
            e.anim("module_part_urinal");
        }
    }
}
