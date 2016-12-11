package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.Interpolation;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.component.Clock;
import net.mostlyoriginal.game.component.module.TipBowl;
import net.mostlyoriginal.game.screen.GameScreen;
import net.mostlyoriginal.game.screen.LogoScreen;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.logic.TransitionSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public class ClockSystem extends FluidSystem {

    public static final int CLOCK_X = 100;
    public static final int CLOCK_Y = 110;
    public static final int VICTORY_HOUR = 18;
    private E largeHand;
    private E smallHand;

    private float age=0;
    private CoinSystem coinSystem;
    private E face;
    private GameScreenAssetSystem assetSystem;

    public ClockSystem() {
        super(Aspect.all(Clock.class));
    }

    @Override
    protected void initialize() {
        super.initialize();

        face = E()
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

        age += world.delta * 4f;
        e.clockAge(e.clockAge()+world.delta);

        int minutesPassed = (int)(e.clockAge()*e.clockSpeed());

        float hour = 8+(minutesPassed/60);
        float minute = minutesPassed%60;

        smallHand.angleRotation(hour * -(360f/12f));
        largeHand.angleRotation(minute * -(360f/60f));
        //largeHand.angleRotate(15);

        if ( hour >= VICTORY_HOUR-1 )
        {
            float scale = Interpolation.fade.apply(1.2f,1.8f,Math.abs(1f-(age%2)));
            float offsetX = (scale * 8f)-8f;
            smallHand.scale(scale).pos(CLOCK_X - offsetX, CLOCK_Y- offsetX);
            largeHand.scale(scale).pos(CLOCK_X- offsetX, CLOCK_Y- offsetX);
            face.scale(scale).pos(CLOCK_X- offsetX, CLOCK_Y- offsetX);
        }

        considerVictoryCondition((int)hour);
    }


    private void considerVictoryCondition(int hour ) {
        if ( !coinSystem.finishing && hour >= VICTORY_HOUR) {
            coinSystem.finishing = true;
            assetSystem.playVictorySfx();
            coinSystem.won = true;
            GameRules.level++;
            world.getSystem(TransitionSystem.class).transition(GameScreen.class, 2f);
        }
    }

}
