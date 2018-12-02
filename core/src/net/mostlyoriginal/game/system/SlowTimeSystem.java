package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.game.component.SlowTime;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class SlowTimeSystem extends FluidSystem {

    private BoxPhysicsSystem boxPhysicsSystem;
    private boolean slowmotion;

    public SlowTimeSystem() {
        super(Aspect.all(SlowTime.class));
    }

    public float slowdownAge = 0;

    @Override
    protected void begin() {
        super.begin();
        slowmotion=false;
    }

    protected float slowdownFactor() {
        float factor = Interpolation.pow2.apply(1f - slowdownAge) * 0.6f + 0.4f;
        return factor;
    }

    @Override
    protected void end() {
        super.end();
        if (slowmotion) {
            slowdownAge = MathUtils.clamp(slowdownAge + world.delta*2f, 0, 1f);
        } else {
            slowdownAge = MathUtils.clamp(slowdownAge - world.delta*2f, 0, 1f);
        }
    }

    @Override
    protected void process(E e) {
        e.slowTimeCooldown(e.slowTimeCooldown() - world.delta);
        if ( e.slowTimeCooldown() <= 0 ) {
            e.removeSlowTime();
        } else {
            slowmotion = true;
        }
    }
}
