package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.component.module.Entrance;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.MyClearScreenSystem;

import static net.mostlyoriginal.game.screen.GameScreen.BACKGROUND_COLOR_HEX;

/**
 * @author Daan van Yperen
 */
public class DiscoSystem extends FluidSystem {
    private float startX;
    private float startY;

    public DiscoSystem() {
        super(Aspect.all(Entrance.class));
    }

    private MyClearScreenSystem clearScreenSystem;
    private CameraSystem cameraSystem;

    private Color BASELINE_COLOR = Color.valueOf(BACKGROUND_COLOR_HEX).mul(0.8f);
    private Color MAX_COLOR = Color.valueOf(BACKGROUND_COLOR_HEX).mul(1.2f);

    private float age;
    private float cooldown;

    private Color a = new Color();
    private Color drift = new Color();
    float cameraShakeStrength=0;

    @Override
    protected void initialize() {
        super.initialize();
        startX = cameraSystem.camera.position.x;
        startY = cameraSystem.camera.position.y;
    }

    @Override
    protected void process(E e) {
        age += world.delta * 10f;
        float timeBetweenSpawns = e.entranceTimeBetweenSpawns();


        timeBetweenSpawns =14;

        cooldown -= world.delta;
        if (cooldown <= 0) {
            cooldown = timeBetweenSpawns / 4f;
            drift.r = MathUtils.random(-1f, 1f);
            drift.g = MathUtils.random(-1f, 1f);
            drift.b = MathUtils.random(-1f, 1f);
            cameraShakeStrength=Interpolation.fade.apply(1f,5f, (14f-timeBetweenSpawns)/14f);
        }

        cameraShakeStrength -= world.delta * 10f;
        if ( cameraShakeStrength < 0 ) cameraShakeStrength =0;
        cameraSystem.camera.position.x =
                startX +
                Interpolation.linear.apply(0,cameraShakeStrength, Math.abs(1f - (age % 2f)));
        cameraSystem.camera.position.y =
                startY +
                Interpolation.linear.apply(0,cameraShakeStrength, Math.abs(1f - (age % 2f)));
        cameraSystem.camera.update();


        a.r = MathUtils.clamp(a.r + drift.r * world.delta, 0, 1f);
        a.g = MathUtils.clamp(a.g + drift.g * world.delta, 0, 1f);
        a.b = MathUtils.clamp(a.b + drift.b * world.delta, 0, 1f);

        clearScreenSystem.color.r = Interpolation.linear.apply(BASELINE_COLOR.r, MAX_COLOR.r, a.r);
        clearScreenSystem.color.g = Interpolation.linear.apply(BASELINE_COLOR.g, MAX_COLOR.g, a.g);
        clearScreenSystem.color.b = Interpolation.linear.apply(BASELINE_COLOR.b, MAX_COLOR.b, a.b);
    }
}
