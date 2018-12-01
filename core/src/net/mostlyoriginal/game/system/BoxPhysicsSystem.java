package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.component.Boxed;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class BoxPhysicsSystem extends FluidSystem {

    public static final int FLOOR_LEVEL_Y = 50;
    public float scaling = 8f;
    public Body groundBody;
    private SlowTimeSystem slowdownSystem;

    //private MouseThrowSystem mouseThrowSystem;

    public BoxPhysicsSystem() {
        super(Aspect.all(Pos.class, Boxed.class));
        box2d = new World(new Vector2(0, -98f), true);
        box2d.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                bulletHit(contact.getFixtureA(), contact.getFixtureB());
                bulletHit(contact.getFixtureB(), contact.getFixtureA());
            }

            private void bulletHit(Fixture fixtureA, Fixture fixtureB) {
                if (fixtureA.getFilterData().categoryBits == LevelSetupSystem.CAT_BULLET) {
                    short cat = fixtureB.getFilterData().categoryBits;
                    if ( cat == LevelSetupSystem.CAT_CAR || cat == LevelSetupSystem.CAT_AGENT  || cat == LevelSetupSystem.CAT_PRESIDENT) {
                        ((E) fixtureB.getBody().getUserData()).struck();
                        ((E) fixtureA.getBody().getUserData()).struck();
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
        addGroundBody();
    }

    public World box2d;

    @Override
    protected void initialize() {
        super.initialize();
    }

    public Body addAsBox(E e, float cx, float cy, float density, short categoryBits, short maskBits) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.x = (e.getPos().xy.x - e.boundsCx()) / scaling;
        bodyDef.position.y = (e.getPos().xy.y - e.boundsCy()) / scaling;

        Body body = box2d.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(cx / scaling, cy / scaling);

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;

        body.createFixture(fixtureDef);

        e.boxedBody(body);
        body.setUserData(e);

        shape.dispose();

        return body;
    }

    protected void addGroundBody() {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.x = 0;
        bodyDef.position.y = 0 / scaling;

        groundBody = box2d.createBody(bodyDef);

        EdgeShape shape = new EdgeShape();
        shape.set(0, FLOOR_LEVEL_Y / scaling, 99999 / scaling, FLOOR_LEVEL_Y / scaling);
//        PolygonShape shape = new PolygonShape();
//        shape.setAsBox(20 / scaling, 1000 / scaling);

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        groundBody.createFixture(fixtureDef);
        shape.dispose();
    }

    float cooldown = 0;

    float timeStep = (1.0f / 60.0f);
    boolean stepping = false;

    @Override
    protected void begin() {
        super.begin();
        //if (Gdx.input.isKeyJustPressed(Input.Keys.F7)) slowmotion = !slowmotion;

        cooldown = cooldown -= world.delta * 1f;
        if (cooldown <= 0) {
            cooldown += timeStep;
            box2d.step(timeStep * slowdownSystem.slowdownFactor(), 6, 2);
            stepping = true;
        } else
            stepping = false;
    }

    @Override
    protected void end() {
        super.end();
        if (cooldown2 <= 0) {
            cooldown2 += 3f;
        }
        cooldown2 -= world.delta;
    }

    float cooldown2 = 0;

    @Override
    public void removed(Entity e) {
        Body body = E.E(e).boxedBody();
        if (body != null) {
            for (JointEdge jointEdge : body.getJointList()) {
                // bit hacky but it should suffice.
                //mouseThrowSystem.forgetJoint(jointEdge);
            }
            box2d.destroyBody(body);
        }
    }

    Vector2 v3 = new Vector2();
    Vector2 v4 = new Vector2();

    @Override
    protected void process(E e) {

        Body body = e.boxedBody();
        e.pos(body.getPosition().x * scaling - e.boundsCx(), body.getPosition().y * scaling - e.boundsCy());
        e.angleRotation((float) Math.toDegrees(body.getAngle()));


        if (cooldown2 <= 0) {
            body.setTransform(body.getPosition(), 0);
        }

        if ( e.hasLocomotion() ) {

            if (stepping && within(body.getAngle(), 0.1f) && within(body.getLinearVelocity().y, 0.1f) && !e.hasSlowTime()) {
                Vector2 vel = body.getLinearVelocity();
                v3.x = e.posX() / scaling;
                v3.y = e.posY() / scaling;
                v4.x = (8f - vel.x) * body.getMass();
                body.applyLinearImpulse(v4, v3, true);

                if ( e.isGuard() && !e.hasSlowTime() ) {
                    e.anim("bodyguard_01");
                }
            }

        }

        if ( e.posY() < -50 ) {
            e.deleteFromWorld();
        }
    }

    private boolean within(float val, float deviation) {
        return val >= -deviation && val <= deviation;
    }

    @Override
    protected void dispose() {
        super.dispose();
        if (box2d != null) {
            box2d.dispose();
        }
    }
}
