package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.component.Laser;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static net.mostlyoriginal.game.system.LevelSetupSystem.CAT_AGENT;
import static net.mostlyoriginal.game.system.LevelSetupSystem.CAT_BULLET;
import static net.mostlyoriginal.game.system.LevelSetupSystem.CAT_CAR;

/**
 * @author Daan van Yperen
 */
public class LaserPointingSystem extends FluidSystem {

    private static final int MAX_LASERS = 3;
    private static final float CHARGEUP_DURATION = 3f;
    private static final float FIRING_DURATION = 3f;
    private E head;
    private BoxPhysicsSystem boxPhysicsSystem;
    private RayCastCallback callback = new RayCastCallback() {

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            if (fixture.getBody() != null
                    && fixture.getBody().getUserData() != null
                    && ((E) fixture.getBody().getUserData()).isGuard()) {
                hitFixture = ((E) fixture.getBody().getUserData());
                return 0;
            }

            return -1;
        }
    };

    public LaserPointingSystem() {
        super(Aspect.all(Laser.class));
    }

    @Override
    protected void initialize() {
        super.initialize();

        spawnLaser(MathUtils.random(-500, GameRules.SCREEN_WIDTH / 2 + 500));
    }

    private void spawnLaser(int i) {
        E.E()
                .laser(i, GameRules.SCREEN_HEIGHT / 2)
                .renderLayer(GameScreenAssetSystem.LAYER_ACTORS + 5000);
    }

    private float cooldown = 0;

    @Override
    protected void begin() {
        super.begin();

        if (getEntityIds().size() < 3) {
            cooldown -= world.delta;
            if (cooldown <= 0) {
                cooldown = MathUtils.random(4, 8);
                head = entityWithTag("presidenthead");

                // aim a little bit ahead where the president will be in a couple of seconds.
                spawnLaser((int) (head.posX() + 300 + MathUtils.random(-GameRules.SCREEN_WIDTH / 2, GameRules.SCREEN_WIDTH / 2)));
            }
        }
    }

    @Override
    protected void process(E e) {
        head = entityWithTag("presidenthead");

        Laser laser = e.getLaser();
        laser.targetX = head.posX() + 4;
        laser.targetY = head.posY() + 4;

        if (laser.charging <= CHARGEUP_DURATION) {
            laser.charging += world.delta;
            e.tint(1f, 1f, 1f, Interpolation.fade.apply(laser.charging / CHARGEUP_DURATION) * 0.2f + 0.2f);
        } else {
            laser.firing += world.delta;
            if (laser.firing <= FIRING_DURATION) {
                laser.blink += world.delta * 16f;
                e.tint(1f, 1f, 1f, laser.blink % 2 < 1f ? 1f : 0);
                intercept(e, laser);
                if ( !laser.fired && laser.firing > FIRING_DURATION-1) {
                    killPresident(laser);
                    laser.fired=true;
                }
            } else {
                e.deleteFromWorld();
            }
        }

    }


    Vector2 v2 = new Vector2();

    private void killPresident(Laser laser) {

        E e = E.E()
                .pos(laser.sourceX, laser.sourceY)
                .renderLayer(GameScreenAssetSystem.LAYER_ACTORS + 10)
                .bounds(0,0,6,6)
                //.slowTimeCooldown(5f)
                .bullet()
                .anim("bullet");

        v2.set(laser.targetX + 60f *2f,laser.targetY).sub(laser.sourceX,laser.sourceY).scl(0.05f);

        Body body = boxPhysicsSystem.addAsBox(e, 3, 3, 5f, CAT_BULLET, (short) (CAT_CAR | CAT_AGENT));
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setSensor(true);
        }
        body.setGravityScale(0f);
        body.setLinearVelocity(v2.x,v2.y);
    }

    private E hitFixture;

    private void intercept(E e, Laser laser) {
        hitFixture = null;
        //boxPhysicsSystem.box2d.rayCast(callback, laser.sourceX / boxPhysicsSystem.scaling, laser.sourceY / boxPhysicsSystem.scaling, laser.targetX / boxPhysicsSystem.scaling, laser.targetY / boxPhysicsSystem.scaling);
        //if (hitFixture != null) {
//            hitFixture.deleteFromWorld();
//            e.deleteFromWorld();
//        }
    }
}
