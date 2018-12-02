package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.component.Desire;
import net.mostlyoriginal.game.screen.LogoScreen;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.logic.TransitionSystem;

import static com.artemis.E.*;

/**
 * @author Daan van Yperen
 */
public class VisitorSystem extends FluidSystem {
    public VisitorSystem() {
        super(Aspect.all(Desire.class, Pos.class));
    }

    @Override
    protected void process(E e) {
        if (e.hasUsing()) {
            E device = E(e.usingUsingId());
            applyAnim(e,
                    (device.hasSink() ? getAnimWash(e) :
                    (device.hasToilet() ? getAnimPoop(e) : getAnimPee(e))) + e.desireIndex());
        } else {
            applyAnim(e, getAnimNormal(e)+ e.desireIndex());
            world.getSystem(TransitionSystem.class).transition(LogoScreen.class, 5);
        }
    }

    private String getAnimPee(E e) {
        switch (e.emotionState()) {
            case HAPPY:
                return "visitor_pee_happy";
            case ANGRY:
                return "visitor_pee_angry";
            case ENRAGED:
                return "visitor_pee_enraged";
            default:
                return "visitor_pee_neutral";
        }
    }

    private String getAnimPoop(E e ) {
        switch (e.emotionState()) {
            case HAPPY:
                return "visitor_poop_happy";
            case ANGRY:
                return "visitor_poop_angry";
            case ENRAGED:
                return "visitor_poop_enraged";
            default:
                return "visitor_poop_neutral";
        }
    }


    private String getAnimWash(E e ) {
        switch (e.emotionState()) {
            case HAPPY:
                return "visitor_wash_happy";
            case ANGRY:
                return "visitor_wash_angry";
            case ENRAGED:
                return "visitor_wash_enraged";
            default:
                return "visitor_wash_neutral";
        }
    }

    private String getAnimNormal(E e) {
        switch (e.emotionState()) {
            case HAPPY:
                return "visitor_happy";
            case ANGRY:
                return "visitor_angry";
            case ENRAGED:
                return "visitor_enraged";
            default:
                return "visitor_neutral";
        }
    }

    private void applyAnim(E e, String animId) {
        if (!e.animId().equals(animId)) {
            e.animAge(0);
            e.anim(animId);
        }
    }
}
