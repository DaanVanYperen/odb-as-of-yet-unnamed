package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.component.Laser;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

/**
 * @author Daan van Yperen
 */
public class LaserPointingSystem extends FluidSystem {

    private static final int MAX_LASERS = 5;
    private static final float CHARGEUP_DURATION = 5f;
    private static final float FIRING_DURATION = 5f;
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

        if (getEntityIds().size() < MAX_LASERS) {
            cooldown -= world.delta;
            if (cooldown <= 0) {
                cooldown += MathUtils.random(2,4);
                head = entityWithTag("presidenthead");
                spawnLaser((int) (head.posX() + MathUtils.random(-GameRules.SCREEN_WIDTH / 3, GameRules.SCREEN_WIDTH / 3)));
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
            e.tint(1f, 1f, 1f, Interpolation.fade.apply(laser.charging / CHARGEUP_DURATION) * 0.6f + 0.4f);
        } else {
            laser.firing += world.delta;
            if (laser.firing <= FIRING_DURATION) {
                laser.blink += world.delta * 16f;
                e.tint(1f, 1f, 1f, laser.blink % 2 < 1f ? 1f : 0);
                intercept(e, laser);
            } else {
                killPresident();
            }
        }

    }

    private void killPresident() {
        E presidenthead = entityWithTag("president");
        presidenthead.tint(1f,0f,0f,1f);

    }

    private E hitFixture;

    private void intercept(E e, Laser laser) {
        hitFixture = null;
        boxPhysicsSystem.box2d.rayCast(callback, laser.sourceX / boxPhysicsSystem.scaling, laser.sourceY / boxPhysicsSystem.scaling, laser.targetX / boxPhysicsSystem.scaling, laser.targetY / boxPhysicsSystem.scaling);
        if ( hitFixture != null)
        {
            hitFixture.deleteFromWorld();
            e.deleteFromWorld();
        }
    }
}
