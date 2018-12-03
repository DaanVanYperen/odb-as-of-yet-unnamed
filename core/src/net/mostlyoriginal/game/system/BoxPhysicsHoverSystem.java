package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointEdge;
import net.mostlyoriginal.game.component.Hovering;
import net.mostlyoriginal.game.component.TheFloorIsLava;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class BoxPhysicsHoverSystem extends FluidSystem {
    private BoxPhysicsSystem boxPhysicsSystem;

    public BoxPhysicsHoverSystem() {
        super(Aspect.all(Hovering.class));
    }

    @Override
    protected void process(E e) {
        if ( boxPhysicsSystem.updating && e.hasBoxed()) {
            Body body = e.boxedBody();
            Vector2 vel = body.getLinearVelocity();
            if ( e.hoveringTargetY() > e.posY() ) {
                body.applyForceToCenter(0,5f*body.getMass(), true);
            }
        }
    }

}
