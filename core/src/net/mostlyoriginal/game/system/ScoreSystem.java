package net.mostlyoriginal.game.system;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.ui.Label;
import net.mostlyoriginal.api.utils.Duration;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.screen.GameScreen;
import net.mostlyoriginal.game.system.logic.TransitionSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static net.mostlyoriginal.api.operation.JamOperationFactory.tintBetween;
import static net.mostlyoriginal.api.operation.OperationFactory.delay;
import static net.mostlyoriginal.api.operation.OperationFactory.sequence;

/**
 * @author Daan van Yperen
 */
public class ScoreSystem extends BaseSystem {

    public int distance = 0;
    public int rockets = 0;
    public boolean gameOver = false;
    public boolean scoreDisplayed = false;
    public float keyCooldown = 0;

    @Override
    protected void processSystem() {
        keyCooldown -= world.delta;
        if ( gameOver && !scoreDisplayed ) {
            scoreDisplayed=true;
            keyCooldown=4f;

            showInfo("Game over. " + distance + "0 feet travelled, " + rockets + " rockets blocked.", 50, 1.5f, GameRules.SCREEN_WIDTH/4);
            showInfo("Press space or click to try again.", 30, 1.5f, GameRules.SCREEN_WIDTH/4);
        }

        if ( gameOver && keyCooldown <= 0 ) {
            if ( Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                world.getSystem(TransitionSystem.class).transition(GameScreen.class, 1);
            }
        }
    }

    private void showInfo(String message, int y, float scale, int x) {
        E.E()
                .pos(x, y)
                .labelText(message)
                .tint(0f, 0f, 0f, 0f)
                .fontFontName("5x5")
                .labelAlign(Label.Align.RIGHT)
                .slowTimeCooldown(99999)
                .script(
                        sequence(
                                delay(Duration.seconds(2)),
                                tintBetween( Tint.TRANSPARENT, new Tint("000000ff"), 2f, Interpolation.pow2)
                        ))
                .fontScale(scale)
                .renderLayer(GameScreenAssetSystem.LAYER_ICONS);

        E.E()
                .pos(x + 2 , y + 2)
                .labelText(message)
                .tint(1f, 1f, 1f, 0f)
                .labelAlign(Label.Align.RIGHT)
                .script(
                        sequence(
                                delay(Duration.seconds(2)),
                                tintBetween(Tint.TRANSPARENT, Tint.WHITE, 2f, Interpolation.pow2)
                        ))
                .fontFontName("5x5")
                .fontScale(scale)
                .renderLayer(GameScreenAssetSystem.LAYER_ICONS + 1);
    }
}
