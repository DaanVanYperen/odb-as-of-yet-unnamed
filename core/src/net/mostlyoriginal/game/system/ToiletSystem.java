package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.game.component.module.Toilet;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class ToiletSystem extends FluidSystem {
    public ToiletSystem() {
        super(Aspect.all(Toilet.class));
    }

    @Override
    protected void process(E e) {
        E bowl = E.E(e.toiletBowlId());
        if (e.hasDirty() && e.isClogged()) {
            if (e.dirtyLevel() == 2) {
                bowl.anim("module_part_toilet_dirty_clogged_2");
            } else if (e.dirtyLevel() == 1) {
                bowl.anim("module_part_toilet_dirty_clogged_1");
            } else {
                bowl.anim("module_part_toilet_dirty_clogged");
            }
        } else if (e.hasDirty()) {
            bowl.anim("module_part_toilet_dirty");
        } else if (e.isClogged()) {
            bowl.anim("module_part_toilet_clogged");
        } else {
            bowl.anim("module_part_toilet");
        }
    }
}
