package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import net.mostlyoriginal.game.component.Boxed;
import net.mostlyoriginal.game.component.Haunted;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class BoxPhysicsHauntedSystem extends FluidSystem {

    SlowTimeSystem slowTimeSystem;

    public BoxPhysicsHauntedSystem() {
        super(Aspect.all(Haunted.class, Boxed.class));
    }

    Vector2 worldOrigin = new Vector2();
    Vector2 pushVec = new Vector2();

    @Override
    protected void process(E e) {

        Haunted haunted = e.getHaunted();

        haunted.cooldown -= world.delta * slowTimeSystem.slowdownFactor();
        if (haunted.cooldown2 <= 0) {
            haunted.cooldown2 += haunted.nextCooldown;
            haunted.nextCooldown = MathUtils.random(2f, 3f);
            Body body = e.boxedBody();
            bounce(e, body, e.boundsMinx() + 4, haunted.strength);
            haunted.strength = MathUtils.random(2f, 6f);
        }

        haunted.cooldown2 -= world.delta * slowTimeSystem.slowdownFactor();
        if (haunted.cooldown <= 0) {
            haunted.cooldown += haunted.nextCooldown;
            Body body = e.boxedBody();
            bounce(e, body, e.boundsMaxx() - 4, haunted.strength);
        }

    }

    private void bounce(E e, Body body, float xOffset, float strength) {
        final Vector2 vel = body.getLinearVelocity();
        worldOrigin.x = (e.posX() + xOffset) / BoxPhysicsSystem.SCALING;
        worldOrigin.y = (e.posY() + e.boundsCy()) / BoxPhysicsSystem.SCALING;
        pushVec.x = 0;
        pushVec.y = strength * body.getMass();
        body.applyLinearImpulse(pushVec, worldOrigin, true);
    }

}
