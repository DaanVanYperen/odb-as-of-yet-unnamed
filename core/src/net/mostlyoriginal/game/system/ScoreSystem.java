package net.mostlyoriginal.game.system;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.ui.Label;
import net.mostlyoriginal.api.utils.Duration;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.screen.GameScreen;
import net.mostlyoriginal.game.system.logic.TransitionSystem;

import static net.mostlyoriginal.api.operation.JamOperationFactory.tintBetween;
import static net.mostlyoriginal.api.operation.OperationFactory.delay;
import static net.mostlyoriginal.api.operation.OperationFactory.remove;
import static net.mostlyoriginal.api.operation.OperationFactory.sequence;

/**
 * @author Daan van Yperen
 */
public class ScoreSystem extends BaseSystem {

    private static final int TEXT = 200;
    private static final Tint YELLOW = new Tint("FFFF00");
    public int distance = 0;
    public int rockets = -1;
    public int choppers = 0;
    public boolean gameOver = false;
    public boolean scoreDisplayed = false;
    public float keyCooldown = 0;
    private ParticleSystem particleSystem;

    private float cooldown = 0;


    @Override
    protected void processSystem() {
        keyCooldown -= world.delta;
        if (gameOver && !scoreDisplayed) {
            scoreDisplayed = true;
            keyCooldown = 4f;


            E.E()
                    .anim("score_background")
                    .renderLayer(9000)
                    .pos(GameRules.SCREEN_WIDTH / 4 - 160, 0);

            int stars =
                    MathUtils.clamp(distance / 100, 0, 2) +
                    MathUtils.clamp(rockets / 10, 0, 1) +
                    MathUtils.clamp(choppers / 2, 0, 2);

            for (int i = 0; i < 5; i++) {
                E.E()
                        .anim("score_star_empty")
                        .renderLayer(9010)
                        .pos(GameRules.SCREEN_WIDTH / 4 - 85 + i * 34, TEXT - 80);
                if ( stars > i ) {
                    E.E()
                            .anim("score_star_filled")
                            .invisible()
                            .renderLayer(9012)
                            .script(
                                    sequence(
                                            delay(Duration.seconds(i*0.5f)),
                                            remove(Invisible.class)
                                    )
                            )
                            .pos(GameRules.SCREEN_WIDTH / 4 - 85 + i * 34, TEXT - 80);
                }
            }

            String s;
            switch (stars) {
                case 0 : s = "Rank: Sad!"; break;
                case 1 : s = "Rank: Newbie!"; break;
                case 2 : s = "Rank: Agent!"; break;
                case 3 : s = "Rank: Special Agent!"; break;
                case 4 : s = "Rank: Bodyguard!"; break;
                default: s = "Rank: Bald Eagle!"; break;
            }

            showInfo(s, TEXT + 20, 1.5f, GameRules.SCREEN_WIDTH / 4, YELLOW);
            showInfo(distance + "0 feet travelled.", TEXT, 1.5f, GameRules.SCREEN_WIDTH / 4, new Tint("AAAAAA"));
            showInfo(rockets + " rockets blocked.", TEXT - 16, 1.5f, GameRules.SCREEN_WIDTH / 4, new Tint("AAAAAA"));
            showInfo(choppers + " choppers downed.", TEXT - 32, 1.5f, GameRules.SCREEN_WIDTH / 4, new Tint("AAAAAA"));
            showInfo("Tap to play again!", TEXT - 100, 1.5f, GameRules.SCREEN_WIDTH / 4, Tint.WHITE);
        }

        if (gameOver) {
            cooldown -= world.delta;
            if (cooldown <= 0) {
                cooldown += 0.2f;
                particleSystem.confettiBomb(
                        MathUtils.random(50, GameRules.SCREEN_WIDTH / 2 - 50),
                        MathUtils.random(50, GameRules.SCREEN_HEIGHT / 2 - 50)
                );
            }
        }

        if (gameOver && keyCooldown <= 0) {
            if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                world.getSystem(TransitionSystem.class).transition(GameScreen.class, 1);
            }
        }
    }

    private void showInfo(String message, int y, float scale, int x, Tint color) {
        E.E()
                .pos(x, y)
                .labelText(message)
                .tint(0f, 0f, 0f, 0f)
                .fontFontName("5x5")
                .labelAlign(Label.Align.RIGHT)
                .slowTimeCooldown(99999)
                .script(
                        sequence(
                                tintBetween(Tint.TRANSPARENT, new Tint("000000ff"), 2f, Interpolation.pow2)
                        ))
                .fontScale(scale)
                .renderLayer(9200);

        E.E()
                .pos(x + 1, y + 1)
                .labelText(message)
                .tint(1f, 1f, 1f, 0f)
                .labelAlign(Label.Align.RIGHT)
                .script(
                        sequence(
                                tintBetween(Tint.TRANSPARENT, color, 2f, Interpolation.pow2)
                        ))
                .fontFontName("5x5")
                .fontScale(scale)
                .renderLayer(9200 + 1);
    }
}
