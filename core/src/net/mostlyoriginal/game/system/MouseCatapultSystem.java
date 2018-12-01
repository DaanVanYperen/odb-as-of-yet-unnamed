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
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class MouseCatapultSystem extends FluidSystem {

    Fixture focusFixture;

    private BoxPhysicsSystem boxPhysicsSystem;
    private QueryCallback callback = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            focusFixture = fixture;
            return true;
        }
    };
    private float posX;
    private float posY;
    private MouseJoint mouseJoint;

    public MouseCatapultSystem() {
        super(Aspect.all(MouseCursor.class, Pos.class));
    }

    @Override
    protected void begin() {
        super.begin();
    }

    private Vector2 origin = new Vector2();
    private Vector2 v2 = new Vector2();
    private E dragging;

    @Override
    protected void end() {
        super.end();
    }

    @Override
    protected void process(E e) {
        Vector3 pos = e.getPos().xy;
        posX = pos.x / boxPhysicsSystem.scaling;
        posY = pos.y / boxPhysicsSystem.scaling;

        focusFixture = null;
        scanFor(pos, 5);
        if (focusFixture == null) scanFor(pos, 10);
        if (focusFixture == null) scanFor(pos, 15);


        if (Gdx.input.isTouched()) {
            if (focusFixture != null && focusFixture.getBody() != boxPhysicsSystem.groundBody && focusFixture.getBody().getUserData() != null && dragging == null) {
                dragging = (E) focusFixture.getBody().getUserData();
                origin.set(e.posX(), e.posY());
                if (!dragging.isGuard()) {
                    dragging = null;
                }
            }
        } else {
            if (dragging != null) {
                if (dragging.hasBoxed()) {
                    Body body = dragging.boxedBody();
                    dragging.slowTimeCooldown(3f);
                    v2.set(dragging.posX(), dragging.posY()).sub(e.posX(), e.posY()).scl(body.getMass());
                    body.applyLinearImpulse(v2.x, v2.y,
                            (dragging.posX() + dragging.boundsCx()) / boxPhysicsSystem.scaling,
                            (dragging.posY() + dragging.boundsCy()) / boxPhysicsSystem.scaling, true);
                }
                dragging = null;
            }
        }
    }

    private void scanFor(Vector3 pos, int pixeldistance) {
        boxPhysicsSystem.box2d.QueryAABB(callback, (pos.x - pixeldistance) / boxPhysicsSystem.scaling, (pos.y - pixeldistance) / boxPhysicsSystem.scaling, (pos.x + pixeldistance) / boxPhysicsSystem.scaling, (pos.y + pixeldistance) / boxPhysicsSystem.scaling);
    }

    public void forgetJoint(JointEdge jointEdge) {
        if (mouseJoint == jointEdge.joint) {
            mouseJoint = null;
        }
    }
}
