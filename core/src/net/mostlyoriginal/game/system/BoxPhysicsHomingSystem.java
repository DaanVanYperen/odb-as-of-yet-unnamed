package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import net.mostlyoriginal.api.component.physics.Homing;
import net.mostlyoriginal.game.component.Boxed;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class BoxPhysicsHomingSystem extends FluidSystem {

    private BoxPhysicsSystem boxPhysicsSystem;
    private SlowTimeSystem slowTimeSystem;

    public BoxPhysicsHomingSystem() {
        super(Aspect.all(Homing.class, Boxed.class));
    }

    Vector2 targetVector = new Vector2();
    Vector2 reference = new Vector2();

    @Override
    protected void process(E e) {
        if (boxPhysicsSystem.updating) {

            if (e.homingTarget() == -1) {
                e.removeHoming();
                return;
            }

            Body body = e.boxedBody();

            E t = E.E(e.homingTarget());

            float sourceAngle = body.getAngle();
            reference.set(1, 0).rotateRad(sourceAngle);

            targetVector.set(e.posX() + e.boundsCx(), e.posY() + e.boundsCy())
                    .sub(t.posX() + t.boundsCx(), t.posY() + t.boundsCy()).angleRad();
            float aDiff = targetVector.angleRad(reference);

            float adjustedAngle = sourceAngle + MathUtils.clamp(aDiff, -0.01f * slowTimeSystem.slowdownFactor(), 0.01f* slowTimeSystem.slowdownFactor());

            body.setTransform(body.getPosition(), adjustedAngle);

            reference.set(1, 0).rotateRad(adjustedAngle).scl(body.getMass());
            body.setLinearVelocity(reference.x,reference.y);
        }
    }

}
