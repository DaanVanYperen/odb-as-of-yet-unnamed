package net.mostlyoriginal.game.system;

import com.artemis.BaseEntitySystem;
import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.Manager;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.game.component.Emotion;

/**
 * @author Daan van Yperen
 */
public class EmotionService extends PassiveSystem {

    public void worsenanger(E e)
    {
        switch (e.emotionState())
        {
            case NEUTRAL:
                e.emotionState(Emotion.State.ANGRY);
                break;
            case HAPPY:
                e.emotionState(Emotion.State.NEUTRAL);
                break;
            case ANGRY:
                e.emotionState(Emotion.State.ENRAGED);
                break;
        }
    }
}
