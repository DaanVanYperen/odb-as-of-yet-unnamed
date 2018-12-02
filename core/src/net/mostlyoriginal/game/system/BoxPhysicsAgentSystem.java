package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import net.mostlyoriginal.game.component.Guard;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class BoxPhysicsAgentSystem extends FluidSystem {

    private BoxPhysicsSystem boxPhysicsSystem;
    private StagepieceSystem stagePieceSystem;

    public BoxPhysicsAgentSystem() {
        super(Aspect.all(Guard.class));
    }

    private boolean within(float val, float deviation) {
        return val >= -deviation && val <= deviation;
    }

    Vector2 worldOrigin = new Vector2();
    Vector2 vel = new Vector2();

    @Override
    protected void process(E e) {

        Guard guard = e.getGuard();

        Body body = e.boxedBody();

        e.pos(body.getPosition().x * BoxPhysicsSystem.SCALING - e.boundsCx(), body.getPosition().y * BoxPhysicsSystem.SCALING - e.boundsCy());
        e.angleRotation((float) Math.toDegrees(body.getAngle()));

        if (e.posY() < -50) {
            stagePieceSystem.replaceAgent(e.renderLayer(), e.guardTargetX());
            e.deleteFromWorld();
            return;
        }

        if (boxPhysicsSystem.updating) {

            if ( e.isTouchingFloor() ) {
                boolean notMovingUp = body.getLinearVelocity().y <= 0;
                if ( e.guardState() == Guard.State.JUMPING && notMovingUp) {
                    body.setTransform(body.getPosition(), 0);
                    guard.slideCooldown = 1f;
                    e.guardState(Guard.State.SLIDING);
                    e.animAge(0);
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

        if ( e.guardState() == Guard.State.JUMPING ) {
            if ( e.hasSlowTime() && body.getLinearVelocity().y < -4 ) {
                e.removeSlowTime();
            }
        }

        if ( e.guardState() == Guard.State.WALKING ) {
            if ( !within(e.guardTargetX() - e.posX(), 4f)) {
                run(e,body, MathUtils.clamp(e.guardTargetX() - e.posX(), -16,16));
            }
        }

        boolean facingLeft = body.getLinearVelocity().x < 0;
        body.setLinearDamping(0f);
        switch (e.guardState()) {
            case WALKING:
                e.anim("bodyguard_01");
                break;
            case CROUCHING:
                e.anim("bodyguard_01_crouch");
                break;
            case JUMPING:
                e.anim(facingLeft ? "bodyguard_01_jump_left" : "bodyguard_01_jump");
                break;
            case SLIDING:
                e.anim(facingLeft ? "bodyguard_01_slide_left" : "bodyguard_01_slide");
                body.setLinearDamping(5f);
                break;
        }

    }

    private void run(E e, Body body, float xMove) {
        final Vector2 vel = body.getLinearVelocity();
        worldOrigin.x = (e.posX() + e.boundsCx()) / BoxPhysicsSystem.SCALING;
        worldOrigin.y = (e.posY() + e.boundsCy()) / BoxPhysicsSystem.SCALING;
        this.vel.x = (xMove - vel.x) * body.getMass();
        this.vel.y = 0;
        body.applyLinearImpulse(this.vel, worldOrigin, true);
    }
}
