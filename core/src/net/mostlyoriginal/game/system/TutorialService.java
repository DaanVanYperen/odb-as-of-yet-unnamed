package net.mostlyoriginal.game.system;

import com.artemis.E;
import com.artemis.managers.TagManager;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.game.component.Tutorial;

/**
 * @author Daan van Yperen
 */
public class TutorialService extends PassiveSystem {
    TagManager tagManager;

    public Tutorial.Step step() {
        Tutorial tutorial = getTutorial();
        return tutorial != null ? tutorial.step : Tutorial.Step.DONE;
    }

    private Tutorial getTutorial() {
        int id = tagManager.getEntityId("tutorial");
        return id != -1 ? E.E(id).getTutorial() : null;
    }

    public void next() {
        Tutorial tutorial = getTutorial();
        if (tutorial != null) tutorial.next();
    }
}
