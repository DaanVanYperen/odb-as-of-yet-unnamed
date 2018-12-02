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

import static net.mostlyoriginal.game.system.StagepieceSystem.CAT_AGENT;
import static net.mostlyoriginal.game.system.StagepieceSystem.CAT_BULLET;
import static net.mostlyoriginal.game.system.StagepieceSystem.CAT_CAR;

/**
 * @author Daan van Yperen
 */
public class LaserPointingSystem extends FluidSystem {

    private static final int MAX_LASERS = 3;

    private static boolean DEBUG = false;

    private float laserChargingDuration = DEBUG ? 2f : 2f;
    private float laserBlinkingDuration = DEBUG ? 0.5f : 0.5f;
    private int maximumLasersAtOnce = DEBUG ? 100 : 1;
    private int laserSpawnDelayMin = DEBUG ? 1 : 6;
    private int laserSpawnDelayMax = DEBUG ? 1 : 8;
    private int rocketVelocity = 20;


    private E head;
    private BoxPhysicsSystem boxPhysicsSystem;
    private RayCastCallback callback = new RayCastCallback() {

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            if (fixture.getBody() != null
                    && fixture.getBody().getUserData() != null
                    && ((E) fixture.getBody().getUserData()).hasGuard()) {
                hitFixture = ((E) fixture.getBody().getUserData());
                return 0;
            }

            return -1;
        }
    };
    private SlowTimeSystem slowTimeSystem;

    public LaserPointingSystem() {
        super(Aspect.all(Laser.class));
    }

    @Override
    protected void initialize() {
        super.initialize();

        spawnLaser(MathUtils.random(-500, GameRules.SCREEN_WIDTH / 2 + 500));
    }

    private void spawnLaser(int i) {
        E e = E.E()
                .laser(i, GameRules.SCREEN_HEIGHT / 2 + 200)
                .renderLayer(GameScreenAssetSystem.LAYER_ACTORS + 5000);

        process(e);
    }

    private float cooldown = 5;
    private float difficultyCooldown = 0;

    @Override
    protected void begin() {
        super.begin();

        scaleDifficulty();

        if (getEntityIds().size() < maximumLasersAtOnce) {
            cooldown -= world.delta;
            if (cooldown <= 0) {
                cooldown = MathUtils.random(laserSpawnDelayMin, laserSpawnDelayMax);
                head = entityWithTag("presidenthead");

                if (head != null) {
                    // aim a little bit ahead where the president will be in a couple of seconds.
                    spawnLaser((int) (head.posX() + 300 + MathUtils.random(-GameRules.SCREEN_WIDTH / 1.5f, GameRules.SCREEN_WIDTH / 1.5f)));
                }
            }
        }
    }


    public int difficultyScore = 1;

    private void scaleDifficulty() {
        difficultyCooldown -= world.delta * slowTimeSystem.slowdownFactor();
        if ( difficultyCooldown <= 0 ) {
            difficultyCooldown += 1;
            difficultyScore++;



            if ( difficultyScore == 30 ) {
                maximumLasersAtOnce= 2;
                laserSpawnDelayMin = 4;
                laserSpawnDelayMax = 6;
            }

            if ( difficultyScore == 60 ) {
                rocketVelocity += 10;
            }

            if ( difficultyScore == 90 ) {
                maximumLasersAtOnce=3;
                laserSpawnDelayMin = 4;
                laserSpawnDelayMax = 5;
            }
            if ( difficultyScore == 120 ) {
                rocketVelocity += 10;
            }

            if ( difficultyScore == 160 ) {
                maximumLasersAtOnce=5;
                laserSpawnDelayMin = 2;
                laserSpawnDelayMax = 4;
            }

            System.out.println(difficultyScore + " " + rocketVelocity);
        }
    }

    @Override
    protected void process(E e) {
        head = entityWithTag("presidenthead");

        if (head == null) {
            e.deleteFromWorld();
            return;
        }

        Laser laser = e.getLaser();
        laser.targetX = head.posX() + 4;
        laser.targetY = head.posY() + 4;

        if (laser.charging <= laserChargingDuration) {
            laser.charging += world.delta;
            e.tint(1f, 1f, 1f, Interpolation.fade.apply(laser.charging / laserChargingDuration) * 0.8f + 0.2f);
        } else {
            laser.firing += world.delta;
            if (laser.firing <= laserBlinkingDuration) {
                laser.blink += world.delta * 16f;
                e.tint(1f, 1f, 1f, laser.blink % 2 < 1f ? 1f : 0);
                intercept(e, laser);
            } else {
                if (!laser.fired) {
                    killPresident(laser);
                    laser.fired = true;
                }
                e.deleteFromWorld();
            }
        }

    }


    Vector2 v2 = new Vector2();

    private void killPresident(Laser laser) {

        E e = E.E()
                .pos(laser.sourceX, laser.sourceY)
                .renderLayer(GameScreenAssetSystem.LAYER_ACTORS + 10)
                .bounds(0, 0, 31, 24)
                //.slowTimeCooldown(5f)
                .bullet()
                .anim("bullet");

        v2.set(laser.targetX, laser.targetY).sub(laser.sourceX, laser.sourceY).nor().scl(rocketVelocity);

        Body body = boxPhysicsSystem.addAsBox(e, 16, 12, 5f, CAT_BULLET, (short) (CAT_CAR | CAT_AGENT), v2.angleRad());
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setSensor(true);
        }

        body.setGravityScale(0f);
        body.setLinearVelocity(v2.x, v2.y);
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
