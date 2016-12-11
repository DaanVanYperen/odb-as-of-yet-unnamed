package net.mostlyoriginal.game;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.component.Desire;
import net.mostlyoriginal.game.system.common.FluidSystem;

import static com.artemis.E.*;

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
            E device = E(e.usingUsingId());
            applyAnim(e, device.hasToilet() ? "visitor_poop" : "visitor_busy");
        } else {
            applyAnim(e, "visitor");
        }
    }

    private void applyAnim(E e, String animId) {
        if (!e.animId().equals(animId)) {
            e.animAge(0);
            e.anim(animId);
        }
    }
}
