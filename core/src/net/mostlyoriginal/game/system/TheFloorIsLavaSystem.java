package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.E;
import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointEdge;
import net.mostlyoriginal.game.component.TheFloorIsLava;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class TheFloorIsLavaSystem extends FluidSystem {

    public TheFloorIsLavaSystem() {
        super(Aspect.all(TheFloorIsLava.class));
    }

    @Override
    protected void process(E e) {
        if ( e.posY() < BoxPhysicsSystem.FLOOR_LEVEL_Y ) {

            //explodeAgents(e);

            e.struck();
        }
    }

    private void explodeAgents(E e) {
        if ( e.hasBoxed() ) {
            Body body = e.boxedBody();
            for (JointEdge jointEdge : body.getJointList()) {
                E other = (E)jointEdge.other.getUserData();
                if ( other != null) {
                    other.struck();
                }
            }
        }
    }
}
