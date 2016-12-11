package net.mostlyoriginal.game;

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
        if ( e.isDirty() && e.isClogged() ) {
            bowl.anim("module_part_toilet_dirty_clogged");
        } else if ( e.isDirty() ) {
            bowl.anim("module_part_toilet_dirty");
        } else if ( e.isClogged() ) {
            bowl.anim("module_part_toilet_clogged");
        } else {
            bowl.anim("module_part_toilet");
        }
    }
}
