package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.graphics.TintWhenSlowdown;
import net.mostlyoriginal.game.component.SlowTime;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class TintWhenSlowdownSystem extends FluidSystem {

    private SlowTimeSystem slowTimeSystem;

    public TintWhenSlowdownSystem() {
        super(Aspect.all(TintWhenSlowdown.class));
    }

    @Override
    protected void process(E e) {
        Color color = e.tintColor();

        TintWhenSlowdown t = e.getTintWhenSlowdown();
        color.r = Interpolation.pow2.apply(t.normal.r, t.slow.r, slowTimeSystem.slowdownAge);
        color.g = Interpolation.pow2.apply(t.normal.g, t.slow.g, slowTimeSystem.slowdownAge);
        color.b = Interpolation.pow2.apply(t.normal.b, t.slow.b, slowTimeSystem.slowdownAge);
        color.a = Interpolation.pow2.apply(t.normal.a, t.slow.a, slowTimeSystem.slowdownAge);
    }


}
