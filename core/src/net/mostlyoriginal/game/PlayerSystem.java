package net.mostlyoriginal.game;

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

    @Override
    protected void process(E e) {
        switch (e.playerTool()) {
            case PLUNGER:
                e.anim(e.isMoving() ? "player_walking_plunger" : "player_plunger");
                break;
            case MOP:
                e.anim(e.isMoving() ? "player_walking_mop" : "player_mop");
                break;
        }
    }


    //add("player_toiletpaper", 32, 288, PLAYER_WIDTH, PLAYER_HEIGHT, 6);
//    add("player_plunger", 176, 288, PLAYER_WIDTH, PLAYER_HEIGHT, 6);
//    add("player_mop", 320, 288, PLAYER_WIDTH, PLAYER_HEIGHT, 6);

}
