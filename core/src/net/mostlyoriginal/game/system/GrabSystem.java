package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import net.mostlyoriginal.game.component.Grab;
import net.mostlyoriginal.game.system.common.FluidSystem;

import static com.artemis.E.*;

/**
 * @author Daan van Yperen
 */
public class GrabSystem extends FluidSystem {

    private BoxPhysicsSystem boxPhysicsSystem;
    private BoxPhysicsAgentSystem boxPhysicsAgentSystem;

    public GrabSystem() {
        super(Aspect.all(Grab.class));
    }

    @Override
    public void inserted(Entity e) {
        final E agent = E(e);
        final E heli = E(agent.grabTargetId());

        if (heli.hasBoxed() && agent.hasBoxed()) {

            boxPhysicsAgentSystem.disableCollisionAndInteraction(agent);

            if ( numberOfAgentsAttached(heli) >= 1 ) {
                weighDown(agent);
            }

            final WeldJointDef def = new WeldJointDef();
            def.bodyA = heli.boxedBody();
            def.bodyB = agent.boxedBody();
            def.collideConnected = false;
            def.type = JointDef.JointType.WeldJoint;

            // try to place the agent at the right X.
            def.localAnchorA.x = (((agent.posX()+agent.boundsCx())
                    -(heli.posX()+heli.boundsCx()))) / BoxPhysicsSystem.SCALING;
            def.localAnchorA.y = (-heli.boundsCy() + 4) / BoxPhysicsSystem.SCALING;

            // hands!
            def.localAnchorB.x = 0 / BoxPhysicsSystem.SCALING;
            def.localAnchorB.y = 10 / BoxPhysicsSystem.SCALING;
            boxPhysicsSystem.box2d.createJoint(def);

        }
    }

    private void weighDown(E agent) {
        if ( agent.hasBoxed() && agent.boxedBody().getFixtureList().size > 0 ) {
            agent.boxedBody().getFixtureList().get(0).setDensity(20);
        }
    }

    private int numberOfAgentsAttached(E heli) {
        return heli.boxedBody().getJointList().size;
    }

    @Override
    protected void process(E e) {

    }
}
