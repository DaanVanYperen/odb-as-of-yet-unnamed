package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.component.Desire;
import net.mostlyoriginal.game.component.module.Entrance;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public class EntranceSystem extends FluidSystem {

    public EntranceSystem() {
        super(Aspect.all(Entrance.class, Pos.class));
    }

    @Override
    protected void process(E e) {
        scaleDifficultyWithTime(e);
        considerSpawningVisitor(e);
    }

    private void considerSpawningVisitor(E e) {
        e.entranceCooldown(e.entranceCooldown()-world.getDelta());
        if ( e.entranceCooldown() <= 0 )
        {
            e.entranceCooldown(e.entranceTimeBetweenSpawns());
            e.anim(e.interactableStartAnimId());
            spawnVisitor((int)(e.posX() + e.boundsMinx()), (int)(e.posY() - e.boundsMiny()));
        }
    }

    private void scaleDifficultyWithTime(E e) {
        e.entranceAge(e.entranceAge()+world.getDelta());
        float timeBetweenSpawns = Interpolation.linear.apply(
                e.entranceTimeBetweenSpawnsEasiest(),
                e.entranceTimeBetweenSpawnsHardest(),
                MathUtils.clamp(e.entranceAge() / e.entranceMaxAge(),0,1f));
        e.entranceTimeBetweenSpawns(
                timeBetweenSpawns);
    }

    private void spawnVisitor(int x, int y) {
        E()
                .pos(x, y)
                .bounds(0,0,GameScreenAssetSystem.VISITOR_WIDTH,GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                .render(GameScreenAssetSystem.LAYER_ACTORS)
                .desire(Desire.Type.POOP)
                .anim("visitor");
    }
}
