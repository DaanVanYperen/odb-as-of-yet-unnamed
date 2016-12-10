package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.component.Desire;
import net.mostlyoriginal.game.component.module.Entrance;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public class VisitorSpawnSystem extends FluidSystem {

    public VisitorSpawnSystem() {
        super(Aspect.all(Entrance.class, Pos.class));
    }

    @Override
    protected void process(E e) {

        e.entranceCooldown(e.entranceCooldown()-world.getDelta());

        if ( e.entranceCooldown() <= 0 )
        {
            e.entranceCooldown(e.entranceTimeBetweenSpawns());
            e.anim(e.interactableStartAnimId());
            spawnVisitor((int)(e.posX() + e.boundsMinx()), (int)(e.posY() - e.boundsMiny()));
        }
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
