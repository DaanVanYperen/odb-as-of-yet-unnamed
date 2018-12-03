package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointEdge;
import net.mostlyoriginal.game.component.Hovering;
import net.mostlyoriginal.game.component.TheFloorIsLava;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

/**
 * @author Daan van Yperen
 */
public class BoxPhysicsHoverSystem extends FluidSystem {
    private BoxPhysicsSystem boxPhysicsSystem;
    private SlowTimeSystem slowTimeSystem;
    private GameScreenAssetSystem gameScreenAssetSystem;

    public BoxPhysicsHoverSystem() {
        super(Aspect.all(Hovering.class));
    }

    public float chopCooldown=0;

    @Override
    protected void begin() {
        super.begin();

        if ( getEntityIds().size() > 0 ) {
            chopCooldown -= world.delta;
            if (chopCooldown <= 0) {
                chopCooldown += 0.25f;
                gameScreenAssetSystem.playSfx("heli1",0.05f);
            }
        }
    }

    @Override
    protected void process(E e) {
        if (boxPhysicsSystem.updating && e.hasBoxed()) {
            Body body = e.boxedBody();
            Vector2 vel = body.getLinearVelocity();
            if (e.posY() < e.hoveringTargetY()) {
                body.applyForceToCenter(0, 5f * body.getMass()* slowTimeSystem.slowdownFactor(), true);
            }
            // move left.
            if (e.posX() + e.boundsCx() > e.hoveringTargetX() + 64 && vel.x > -5) {
                body.applyForceToCenter(-1f * body.getMass()* slowTimeSystem.slowdownFactor(), 0, true);
            }

            // move right.
            if (e.posX() + e.boundsCx() < e.hoveringTargetX() - 64 && vel.x <5) {
                body.applyForceToCenter(1f * body.getMass() * slowTimeSystem.slowdownFactor(), 0, true);
            }
        }
    }

}
