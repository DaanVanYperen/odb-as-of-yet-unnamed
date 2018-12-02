package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import net.mostlyoriginal.game.component.Struck;
import net.mostlyoriginal.game.system.common.FluidSystem;

/**
 * @author Daan van Yperen
 */
public class StruckSystem extends FluidSystem {
    private BoxPhysicsSystem boxPhysicsSystem;
    private ScoreSystem scoreSystem;

    public StruckSystem() {
        super(Aspect.all(Struck.class));
    }

    @Override
    protected void process(E e) {
        e.removeStruck();
        e.removeSlowTime();

        if ( e.hasGuard()) {
            Body body = e.boxedBody();
            body.setTransform(body.getPosition(), MathUtils.random(-10f,10f));
            for (Fixture fixture : body.getFixtureList()) {
                Filter filterData = fixture.getFilterData();
                filterData.maskBits = 0;
                fixture.setFilterData(filterData);
            }
            return;
        }

        if (e.isBullet()) {
            scoreSystem.rockets++;
            e.deleteFromWorld();
            return;
        }

        E president = entityWithTag("president");
        if ( president != null && e.id() == president.id()) {
            E head = entityWithTag("presidenthead");
            if ( head != null ) {
                head.deleteFromWorld();
            }

            scoreSystem.gameOver=true;

            if ( e.hasBoxed() ) {
                e.slowTime();
                Body body = e.boxedBody();
                for (JointEdge jointEdge : body.getJointList()) {
                    boxPhysicsSystem.box2d.destroyJoint(jointEdge.joint);
                }
                body.applyLinearImpulse(0,500f,e.posX() / boxPhysicsSystem.SCALING,e.posY() / boxPhysicsSystem.SCALING, true);
            }
        }
    }
}
