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
import net.mostlyoriginal.game.component.Guard;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

/**
 * @author Daan van Yperen
 */
public class MouseCatapultSystem extends FluidSystem {

    Fixture focusFixture;

    private BoxPhysicsSystem boxPhysicsSystem;
    private QueryCallback callback = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            if ( fixture.getFilterData().categoryBits == StagepieceSystem.CAT_AGENT) {
                focusFixture = fixture;
                return true;
            } else return false;
        }
    };
    private float posX;
    private float posY;
    private MouseJoint mouseJoint;
    private E arrow;

    public MouseCatapultSystem() {
        super(Aspect.all(MouseCursor.class, Pos.class));
    }

    @Override
    protected void initialize() {
        super.initialize();
        arrow = E.E()
                .pos(0, 0)
                .invisible()
                .tint(1f,1f,1f,0.6f)
                .bounds(0,0,14,28)
                .anim("arrow")
                .renderLayer(GameScreenAssetSystem.LAYER_ACTORS + 1000);
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
        posX = pos.x / boxPhysicsSystem.SCALING;
        posY = pos.y / boxPhysicsSystem.SCALING;

        focusFixture = null;
        scanFor(pos, 5);
        if (focusFixture == null) scanFor(pos, 10);
        if (focusFixture == null) scanFor(pos, 15);


        if (Gdx.input.isTouched()) {
            if (focusFixture != null && focusFixture.getBody() != boxPhysicsSystem.groundBody && focusFixture.getBody().getUserData() != null && dragging == null) {
                dragging = (E) focusFixture.getBody().getUserData();
                origin.set(e.posX(), e.posY());
                if (!dragging.hasGuard()) {
                    dragging = null;
                } else {
                    dragging.guardState(Guard.State.CROUCHING);
                }
            }

            if ( dragging != null ) {
                dragging.slowTimeCooldown(0.2f);
            }
        } else {
            if (dragging != null) {
                if (dragging.hasBoxed()) {
                    Body body = dragging.boxedBody();
                    dragging.guardState(Guard.State.JUMPING);
                    dragging.slowTimeCooldown(3f);
                    v2.set(dragging.posX(), dragging.posY()).sub(e.posX() - 12, e.posY() - 12).clamp(20f,60f).scl(1.2f).scl(body.getMass());
                    body.applyLinearImpulse(v2.x, v2.y,
                            (dragging.posX() + dragging.boundsCx()) / boxPhysicsSystem.SCALING,
                            (dragging.posY() + dragging.boundsCy()) / boxPhysicsSystem.SCALING, true);
                }
                dragging = null;
            }
        }

        if  ( dragging != null && dragging.hasBoxed() ) {

            Body body = dragging.boxedBody();
            v2.set(dragging.posX(), dragging.posY()).sub(e.posX() - 12, e.posY() - 12).clamp(20f,60f).scl(body.getMass()).scl(0.2f);

            arrow.removeInvisible();
            arrow.posX(dragging.posX() + dragging.boundsCx()+ v2.x - arrow.boundsCx());
            arrow.posY(dragging.posY() + dragging.boundsCy()+ v2.y - arrow.boundsCy());
            arrow.angleRotation(v2.angle()-90);

        } else {
            arrow.invisible();
        }
    }

    private void scanFor(Vector3 pos, int pixeldistance) {
        boxPhysicsSystem.box2d.QueryAABB(callback, (pos.x - pixeldistance) / boxPhysicsSystem.SCALING, (pos.y - pixeldistance) / boxPhysicsSystem.SCALING, (pos.x + pixeldistance) / boxPhysicsSystem.SCALING, (pos.y + pixeldistance) / boxPhysicsSystem.SCALING);
    }

    public void forgetJoint(JointEdge jointEdge) {
        if (mouseJoint == jointEdge.joint) {
            mouseJoint = null;
        }
    }
}
