package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.game.CoinSystem;
import net.mostlyoriginal.game.EmotionService;
import net.mostlyoriginal.game.component.Desire;
import net.mostlyoriginal.game.component.Emotion;
import net.mostlyoriginal.game.component.Interactable;
import net.mostlyoriginal.game.component.Player;
import net.mostlyoriginal.game.component.state.InUse;
import net.mostlyoriginal.game.system.common.FluidSystem;

import static com.artemis.E.E;
import static net.mostlyoriginal.game.system.view.GameScreenAssetSystem.*;

/**
 * @author Daan van Yperen
 */
public class InteractableCooldownSystem extends FluidSystem {

    public InteractableCooldownSystem() {
        super(Aspect.all(Interactable.class));
    }

    @Override
    protected void process(E e) {
        if ( e.interactableCooldownBefore() > 0 ) {
            e.interactableCooldownBefore(e.interactableCooldownBefore() - world.getDelta());
        }
    }

}
