package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import net.mostlyoriginal.game.component.Guard;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class BoxPhysicsAgentSystem extends FluidSystem {

    private BoxPhysicsSystem boxPhysicsSystem;

    public BoxPhysicsAgentSystem() {
        super(Aspect.all(Guard.class));
    }

    private boolean within(float val, float deviation) {
        return val >= -deviation && val <= deviation;
    }

    Vector2 v3 = new Vector2();
    Vector2 v4 = new Vector2();

    @Override
    protected void process(E e) {

        Guard guard = e.getGuard();

        Body body = e.boxedBody();

        e.pos(body.getPosition().x * BoxPhysicsSystem.SCALING - e.boundsCx(), body.getPosition().y * BoxPhysicsSystem.SCALING - e.boundsCy());
        e.angleRotation((float) Math.toDegrees(body.getAngle()));

        if (e.posY() < -50) {
            e.deleteFromWorld();
            return;
        }

        if (boxPhysicsSystem.updating) {

            if (e.guardState() == Guard.State.WALKING) {
                runRight(e, body);
            }

            body.setLinearDamping(0f);
            if ( e.isTouchingFloor() ) {
                if ( e.guardState() != Guard.State.WALKING) {
                    body.setLinearDamping(5f);
                    body.setTransform(body.getPosition(), 0);
                    guard.slideCooldown = 1f;
                }
            } else {
                guard.slideCooldown = 1f;
            }

            if (guard.slideCooldown >= 0 && e.isTouchingFloor()) {
                guard.slideCooldown -= world.delta;
                if (guard.slideCooldown <= 0) {
                    e.guardState(Guard.State.WALKING);
                }
            }
        }

        switch (e.guardState()) {
            case WALKING:
                e.anim("bodyguard_01");
                break;
            case CROUCHING:
                e.anim("bodyguard_01_crouch");
                break;
            case JUMPING:
                e.anim("bodyguard_01_jump");
                break;
        }

        if (e.isTouchingFloor()) {
            e.anim("bodyguard_01");
        } else {
            e.anim("bodyguard_01_jump");
        }

    }

    private void runRight(E e, Body body) {
        final Vector2 vel = body.getLinearVelocity();
        v3.x = e.posX() / BoxPhysicsSystem.SCALING;
        v3.y = e.posY() / BoxPhysicsSystem.SCALING;
        v4.x = (8f - vel.x) * body.getMass();
        v4.y = 0;
        body.applyLinearImpulse(v4, v3, true);
    }
}
