package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.game.component.Clock;
import net.mostlyoriginal.game.component.module.TipBowl;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public class ClockSystem extends FluidSystem {

    public static final int CLOCK_X = 100;
    public static final int CLOCK_Y = 110;
    private E largeHand;
    private E smallHand;

    public ClockSystem() {
        super(Aspect.all(Clock.class));
    }

    @Override
    protected void initialize() {
        super.initialize();

        E()
                .pos(CLOCK_X, CLOCK_Y)
                .anim("clock_face")
                .clock()
                .renderLayer(GameScreenAssetSystem.LAYER_CLOCK);

        smallHand = E()
                .pos(CLOCK_X, CLOCK_Y)
                .anim("clock_small_hand")
                .renderLayer(GameScreenAssetSystem.LAYER_CLOCK + 1)
                .angleRotate(0);
        largeHand = E()
                .pos(CLOCK_X, CLOCK_Y)
                .anim("clock_large_hand")
                .renderLayer(GameScreenAssetSystem.LAYER_CLOCK + 2)
                .angleRotate(0);
    }

    @Override
    protected void process(E e) {
        e.clockAge(e.clockAge()+world.delta);

        int minutesPassed = (int)(e.clockAge()*10f);

        float hour = 8+(minutesPassed/60);
        float minute = minutesPassed%60;

        smallHand.angleRotation(hour * -(360f/12f));
        largeHand.angleRotation(minute * -(360f/60f));
        //largeHand.angleRotate(15);
    }
}
