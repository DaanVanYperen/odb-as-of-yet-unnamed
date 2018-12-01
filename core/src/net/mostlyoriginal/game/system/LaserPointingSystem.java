package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
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

    private E head;
    private BoxPhysicsSystem boxPhysicsSystem;

    public LaserPointingSystem() {
        super(Aspect.all(Laser.class));
    }

    @Override
    protected void initialize() {
        super.initialize();

        E.E()
                .laser(200, GameRules.SCREEN_HEIGHT / 2)
                .renderLayer(GameScreenAssetSystem.LAYER_ACTORS + 5000);
        E.E()
                .laser(400, GameRules.SCREEN_HEIGHT / 2)
                .renderLayer(GameScreenAssetSystem.LAYER_ACTORS + 5000);
        E.E()
                .laser(600, GameRules.SCREEN_HEIGHT / 2)
                .renderLayer(GameScreenAssetSystem.LAYER_ACTORS + 5000);
    }

    @Override
    protected void process(E e) {
        head = entityWithTag("presidenthead");

        Laser laser = e.getLaser();
        laser.targetX = head.posX() + 4;
        laser.targetY = head.posY() + 4;

        final E e2 = e;
        boxPhysicsSystem.box2d.rayCast(new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getBody() != null
                        && fixture.getBody().getUserData() != null
                        && ((E) fixture.getBody().getUserData()).isGuard()) {
                    ((E) fixture.getBody().getUserData()).deleteFromWorld();
                    e2.deleteFromWorld();
                    return 0;
                }

                return -1;
            }
        }, laser.sourceX / boxPhysicsSystem.scaling, laser.sourceY / boxPhysicsSystem.scaling, laser.targetX / boxPhysicsSystem.scaling,laser.targetY / boxPhysicsSystem.scaling);
    }
}
