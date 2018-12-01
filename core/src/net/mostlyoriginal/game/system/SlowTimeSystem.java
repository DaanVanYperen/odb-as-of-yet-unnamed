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
import net.mostlyoriginal.game.component.SlowTime;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static net.mostlyoriginal.game.system.LevelSetupSystem.*;

/**
 * @author Daan van Yperen
 */
public class SlowTimeSystem extends FluidSystem {

    private BoxPhysicsSystem boxPhysicsSystem;

    public SlowTimeSystem() {
        super(Aspect.all(SlowTime.class));
    }

    @Override
    protected void begin() {
        super.begin();
        boxPhysicsSystem.slowmotion=false;
    }

    @Override
    protected void process(E e) {
        boxPhysicsSystem.slowmotion=true;
    }
}
