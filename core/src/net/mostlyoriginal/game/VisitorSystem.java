package net.mostlyoriginal.game;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.component.Desire;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class VisitorSystem extends FluidSystem {
    public VisitorSystem() {
        super(Aspect.all(Desire.class, Pos.class));
    }

    @Override
    protected void process(E e) {
        if ( e.hasUsing() )
        {
            if ( !e.animId().equals("visitor_busy") ) {
                e.animAge(0);
                e.anim("visitor_busy");
            }
        } else {
            if ( !e.animId().equals("visitor") ) {
                e.animAge(0);
                e.anim("visitor");
            }
        }
    }
}
