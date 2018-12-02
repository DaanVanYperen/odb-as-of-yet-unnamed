package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class MouseThrowSystem extends FluidSystem {

    Fixture focusFixture;

    private BoxPhysicsSystem boxPhysicsSystem;
    private QueryCallback callback = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            focusFixture=fixture;
            return true;
        }
    };
    private float posX;
    private float posY;
    private MouseJoint mouseJoint;

    public MouseThrowSystem() {
        super(Aspect.all(MouseCursor.class, Pos.class));
    }

    @Override
    protected void begin() {
        super.begin();
        focusFixture =null;
    }


    @Override
    protected void end() {
        super.end();
        if ( Gdx.input.isTouched()) {
            if (focusFixture != null && focusFixture.getBody() != boxPhysicsSystem.groundBody && mouseJoint == null && focusFixture.getBody() != null) {
                MouseJointDef def = new MouseJointDef();
                def.bodyA = boxPhysicsSystem.groundBody;
                Body targetBody = focusFixture.getBody();
                def.bodyB = targetBody;
                def.collideConnected = true;
                def.target.set(posX, posY);
                def.maxForce = 100000.0f * targetBody.getMass();

                mouseJoint = (MouseJoint)boxPhysicsSystem.box2d.createJoint(def);
                targetBody.setAwake(true);
            }
        } else {
            if (mouseJoint != null) {
                boxPhysicsSystem.box2d.destroyJoint(mouseJoint);
                mouseJoint = null;
            }
        }

        if ( mouseJoint != null ) {
            mouseJoint.setTarget(new Vector2(posX,posY));
        }
    }

    @Override
    protected void process(E e) {
        Vector3 pos = e.getPos().xy;
        posX = pos.x / boxPhysicsSystem.SCALING;
        posY = pos.y / boxPhysicsSystem.SCALING;
        boxPhysicsSystem.box2d.QueryAABB(callback, (pos.x - 5) / boxPhysicsSystem.SCALING, (pos.y - 5) / boxPhysicsSystem.SCALING, (pos.x + 5) / boxPhysicsSystem.SCALING, (pos.y + 5) / boxPhysicsSystem.SCALING);
    }

    public void forgetJoint(JointEdge jointEdge) {
        if ( mouseJoint == jointEdge.joint) {
            mouseJoint = null;
        }
    }
}
