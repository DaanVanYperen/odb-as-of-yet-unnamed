package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import net.mostlyoriginal.game.component.Guard;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class BoxPhysicsAgentSystem extends FluidSystem {

    private BoxPhysicsSystem boxPhysicsSystem;
    private StagepieceSystem stagePieceSystem;
    private E tutorial;
    private boolean removeTutorials;
    private MouseCatapultSystem mouseCatapultSystem;


    public BoxPhysicsAgentSystem() {
        super(Aspect.all(Guard.class));
    }

    private boolean within(float val, float deviation) {
        return val >= -deviation && val <= deviation;
    }

    Vector2 worldOrigin = new Vector2();
    Vector2 vel = new Vector2();

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void begin() {
        super.begin();
        mouseCatapultSystem.setTutorialFocus(null);
    }

    @Override
    protected void process(E e) {

        Guard guard = e.getGuard();

        Body body = e.boxedBody();

        e.pos(body.getPosition().x * BoxPhysicsSystem.SCALING - e.boundsCx(), body.getPosition().y * BoxPhysicsSystem.SCALING - e.boundsCy());
        e.angleRotation((float) Math.toDegrees(body.getAngle()));

        E tutorial = guard.tutorial != -1 ? E.E(guard.tutorial) : null;
        if (tutorial != null) {
            tutorial.posX(e.posX() + e.boundsCx() - tutorial.boundsCx());
            tutorial.posY(e.posY() + e.boundsMaxy() - 55);
            mouseCatapultSystem.setTutorialFocus(e);
            if (removeTutorials) {
                tutorial.deleteFromWorld();
                e.guardTutorial(-1);
                mouseCatapultSystem.setTutorialFocus(null);
            }
        }

        if (e.posY() < -50) {
            stagePieceSystem.replaceAgent(e.renderLayer(), e.guardTargetX(), e.guardBandaged() || MathUtils.random(0, 100) < 50);
            e.deleteFromWorld();
            return;
        }

        if (boxPhysicsSystem.updating) {

            if (e.isTouchingFloor()) {
                boolean notMovingUp = body.getLinearVelocity().y <= 0;
                if (e.guardState() == Guard.State.JUMPING && notMovingUp) {
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

        if (e.guardState() == Guard.State.JUMPING) {
            if (e.hasSlowTime() && body.getLinearVelocity().y < -4) {
                e.removeSlowTime();
            }
        }

        if (e.guardState() == Guard.State.WALKING) {
            if (!within(e.guardTargetX() - e.posX(), 4f)) {
                run(e, body, MathUtils.clamp(e.guardTargetX() - e.posX(), -16, 16));
            }
        }

        boolean facingLeft = body.getLinearVelocity().x < 0;
        boolean bandaged = e.guardBandaged();
        body.setLinearDamping(0f);
        switch (e.guardState()) {
            case WALKING:
                e.anim(bandaged ? "bodyguard_bandaged_01" : "bodyguard_01");
                break;
            case CROUCHING:
                e.anim(bandaged ? "bodyguard_bandaged_01_crouch" : "bodyguard_01_crouch");
                removeTutorials = true;
                break;
            case JUMPING:
                e.anim(
                        bandaged ?
                                (facingLeft ? "bodyguard_bandaged_01_jump_left" : "bodyguard_bandaged_01_jump") :
                                (facingLeft ? "bodyguard_01_jump_left" : "bodyguard_01_jump")
                );
                break;
            case SLIDING:
                e.anim(
                        bandaged ?
                                (facingLeft ? "bodyguard_bandaged_01_slide_left" : "bodyguard_bandaged_01_slide") :
                                (facingLeft ? "bodyguard_01_slide_left" : "bodyguard_01_slide")
                );
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

    public void disableCollisionAndInteraction(E e) {
        if (e.hasGuard()) {
            e.removeCatapultProjectile();
            Body body = e.boxedBody();
            body.setTransform(body.getPosition(), MathUtils.random(-10f, 10f));
            for (Fixture fixture : body.getFixtureList()) {
                Filter filterData = fixture.getFilterData();
                filterData.maskBits = 0;
                fixture.setFilterData(filterData);
            }
        }

    }
}
