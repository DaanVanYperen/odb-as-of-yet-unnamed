package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.component.Laser;
import net.mostlyoriginal.game.component.RocketLauncher;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

/**
 * @author Daan van Yperen
 */
public class LaserPointingSystem extends FluidSystem {

    private static final int MAX_LASERS = 3;

    private static boolean DEBUG = false;
    private RocketLauncherSystem rocketLauncherSystem;

    private float laserChargingDuration = DEBUG ? 2f : 2f;
    private float laserBlinkingDuration = DEBUG ? 0.5f : 0.5f;
    private int maximumLasersAtOnce = 3;
    private int laserSpawnDelayMin = DEBUG ? 1 : 4;
    private int laserSpawnDelayMax = DEBUG ? 1 : 6;
    public int rocketVelocity = 10;


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
    private ScoreSystem scoreSystem;
    private StagepieceSystem stagePieceSystem;
    private LaserPointingSystem laserPointingSystem;

    public LaserPointingSystem() {
        super(Aspect.all(Laser.class));
    }

    @Override
    protected void initialize() {
        super.initialize();
        //stagePieceSystem.addHelicopter(-100, 300, 200);
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

        if (getEntityIds().size() < maximumLasersAtOnce && world.delta < 1f) {
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


    public boolean DEBUG2 = false;
    public int difficultyScore = DEBUG2 ? 59 : 1;

    private void scaleDifficulty() {
        difficultyCooldown -= world.delta * slowTimeSystem.slowdownFactor();
        if (difficultyCooldown <= 0) {
            difficultyCooldown += 1;
            difficultyScore++;
            scoreSystem.distance++;

            if (difficultyScore == 30) {
                maximumLasersAtOnce = 0;
                laserPointingSystem.cancelAllLasers();
                stagePieceSystem.addHelicopter(-100, 300, 200);
            }
            if (difficultyScore == 40) {
                maximumLasersAtOnce = 1;
            }

            if (difficultyScore == 50) {
                maximumLasersAtOnce = 4;
                laserSpawnDelayMin = 3;
                laserSpawnDelayMax = 5;
            }

            if (difficultyScore == 60) {


                int i = 2;
                stagePieceSystem.replaceAgent(GameScreenAssetSystem.LAYER_CAR - 51, 50 + 20 + 72 / 2 + i * 80 - 24, 0, -700 + i * 80);
                i = 0;
                stagePieceSystem.replaceAgent(GameScreenAssetSystem.LAYER_CAR - 51, 380 + 20 + 72 / 2 + i * 80 - 24, 0, -700 + i * 80);

                maximumLasersAtOnce = 0;
                laserPointingSystem.cancelAllLasers();
                stagePieceSystem.addHelicopter(-100, 300, 200);
                stagePieceSystem.addHelicopter(GameRules.SCREEN_WIDTH / 2 + 100, 250, 500);
            }

            if (difficultyScore == 80) {
                maximumLasersAtOnce = 1;
            }

            if (difficultyScore == 90) {
                rocketVelocity += 5;
                maximumLasersAtOnce = 5;
                laserSpawnDelayMin = 2;
                laserSpawnDelayMax = 3;
            }

            if (difficultyScore == 120) {
                maximumLasersAtOnce = 0;

                for (int i = 0; i < 2; i++) {
                    stagePieceSystem.replaceAgent(GameScreenAssetSystem.LAYER_CAR - 51, 50 + 20 + 72 / 2 + i * 80 - 24, 0, -700 + i * 80);
                }
                for (int i = 1; i < 3; i++) {
                    stagePieceSystem.replaceAgent(GameScreenAssetSystem.LAYER_CAR - 51, 380 + 20 + 72 / 2 + i * 80 - 24, 0, -700 + i * 80);
                }

                laserPointingSystem.cancelAllLasers();
                stagePieceSystem.addHelicopter(-100, 300, 200);
                stagePieceSystem.addHelicopter(GameRules.SCREEN_WIDTH / 2 + 100, 250, 500);
            }

            if (difficultyScore == 130) {
                maximumLasersAtOnce = 5;
            }

            if (difficultyScore >= 130 && difficultyScore % 10 == 9) {
                rocketVelocity += 5;
                maximumLasersAtOnce++;
                laserSpawnDelayMin = 1;
                laserSpawnDelayMax = 2;
                stagePieceSystem.addHelicopter(-100, 250, MathUtils.random(0, GameRules.SCREEN_WIDTH / 2));
            }
        }
    }

    private void cancelAllLasers() {
        Bag<Entity> entities = getEntities();
        Object[] array = entities.getData();
        for (int i = 0, s = entities.size(); s > i; i++) {
            E.E((Entity) array[i]).deleteFromWorld();
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
            e.tint(1f, 1f, 1f, Interpolation.fade.apply(laser.charging / laserChargingDuration) * 0.6f + 0.2f);
        } else {
            laser.firing += world.delta;
            if (laser.firing <= laserBlinkingDuration) {
                laser.blink += world.delta * 16f;
                e.tint(1f, 1f, 1f, laser.blink % 2 < 1f ? 0.8f : 0.6f);
                intercept(e, laser);
            } else {
                if (!laser.fired) {
                    rocketLauncherSystem.spawnRocket(laser.sourceX, laser.sourceY, laser.targetX, laser.targetY, RocketLauncher.RocketType.BIG, rocketVelocity);
                    laser.fired = true;
                }
                e.deleteFromWorld();
            }
        }

    }


    Vector2 v2 = new Vector2();

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
