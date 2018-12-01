package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.physics.box2d.*;
import net.mostlyoriginal.game.component.Struck;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class StruckSystem extends FluidSystem {
    private BoxPhysicsSystem boxPhysicsSystem;

    public StruckSystem() {
        super(Aspect.all(Struck.class));
    }

    @Override
    protected void process(E e) {
        e.removeStruck();
        if (e.isGuard() || e.isBullet()) {
            e.deleteFromWorld();
        }

        if ( e.id() == entityWithTag("president").id()) {
            if ( e.hasBoxed() ) {
                e.slowTime();
                Body body = e.boxedBody();
                for (JointEdge jointEdge : body.getJointList()) {
                    boxPhysicsSystem.box2d.destroyJoint(jointEdge.joint);
                    break;
                }
                for (Fixture fixture : body.getFixtureList()) {
                    fixture.getFilterData().maskBits=0;
                    fixture.refilter();
                }
                body.applyLinearImpulse(0,500f,e.posX() / boxPhysicsSystem.scaling,e.posY() / boxPhysicsSystem.scaling, true);
            }
        }
    }
}
