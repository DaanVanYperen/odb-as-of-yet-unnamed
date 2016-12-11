package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.game.component.Player;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class PlayerSystem extends FluidSystem {
    public PlayerSystem() {
        super(Aspect.all(Player.class, Anim.class));
    }

    CoinSystem coinSystem;

    @Override
    protected void process(E e) {
        switch (e.playerTool()) {
            case PLUNGER:
                e.anim(
                        e.hasUsing() ? "player_using_plunger" :
                                e.isMoving() ? "player_walking_plunger" : "player_plunger");
                break;
            case MOP:
                e.anim(e.hasUsing() ? "player_using_mop" :
                        e.isMoving() ? "player_walking_mop" : "player_mop");
                break;
        }
        if ( coinSystem.finishing ) {
            if ( coinSystem.won ) {
                e.anim("player_winning");
            } else {
                e.anim("player_losing");
            }
        }
    }

}
