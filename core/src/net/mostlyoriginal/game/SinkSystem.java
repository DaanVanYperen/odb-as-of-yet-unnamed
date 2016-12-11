package net.mostlyoriginal.game;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.game.component.module.Sink;
import net.mostlyoriginal.game.component.module.Urinal;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class SinkSystem extends FluidSystem {
    public SinkSystem() {
        super(Aspect.all(Sink.class));
    }

    @Override
    protected void process(E e) {
        if ( e.hasDirty() ) {
            if ( e.dirtyLevel() == 1 ) {
                e.anim("module_part_sink_gross");
            } else {
                e.anim("module_part_sink_dirty");
            }
        } else {
            e.anim("module_part_sink");
        }
    }
}
