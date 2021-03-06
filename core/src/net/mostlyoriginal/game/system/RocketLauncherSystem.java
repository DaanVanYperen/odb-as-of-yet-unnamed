package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.component.Boxed;
import net.mostlyoriginal.game.component.RocketLauncher;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static net.mostlyoriginal.game.system.StagepieceSystem.*;

/**
 * @author Daan van Yperen
 */
public class RocketLauncherSystem extends FluidSystem {

    Vector2 v2 = new Vector2();
    private BoxPhysicsSystem boxPhysicsSystem;
    private LaserPointingSystem laserPointingSystem;
    private SlowTimeSystem slowTimeSystem;
    private GameScreenAssetSystem gameScreenAssetSystem;

    public RocketLauncherSystem() {
        super(Aspect.all(RocketLauncher.class, Boxed.class));
    }

    @Override
    protected void process(E e) {
        RocketLauncher launcher = e.getRocketLauncher();
        launcher.cooldown -= world.delta * slowTimeSystem.slowdownFactor();
        if (launcher.cooldown <= 0 && e.posY() > 150) { // don't fire too close to the ground.
            launcher.cooldown = launcher.interval;
            E head = entityWithTag("presidenthead");
            if (head != null) {
                float x = e.posX() + launcher.offsetX;
                float y = e.posY() + launcher.offsetY;
                spawnRocket(x, y,
                        x + 100, y + 50, launcher.type, laserPointingSystem.rocketVelocity * 0.2f)
                        .homingTarget(head.id());
                spawnRocket(x, y,
                        x + 100, y - 50, launcher.type, laserPointingSystem.rocketVelocity * 0.2f)
                        .homingTarget(head.id());
            }
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    public E spawnRocket(float originX, float originY, float targetX, float targetY, RocketLauncher.RocketType type, float baseVelocity) {


        if (type == RocketLauncher.RocketType.BIG) {
            gameScreenAssetSystem.playSfx("woosh1", "woosh2");
        }

        int width = 48;
        int height = 9;
        int cx = 24;
        int cy = 4;
        String sprite = "bullet";
        boolean homing=false;


        switch (type) {
            case SMALL:
                width = 24;
                height = 6;
                cx = 12;
                cy = 3;
                sprite = "bullet_small";
                break;
            case BIG:
                if (originX > GameRules.SCREEN_WIDTH / 4 - 400 && originX < GameRules.SCREEN_WIDTH / 4 + 400) {
                    width = 24;
                    height = 14;
                    cx = 12;
                    cy = 7;
                    sprite = "bullet_bomb";
                    homing=true;
                    break;
                }
        }

        E e = E.E()
                .pos(originX, originY)
                .renderLayer(GameScreenAssetSystem.LAYER_ACTORS + 10)
                .theFloorIsLava()
                .bullet()
                .bounds(0, 0, width, height)
                //.slowTimeCooldown(5f)
                .bullet()
                .anim(sprite);

        if ( homing ) {
            E head = entityWithTag("presidenthead");
            if ( head != null ) {
                e.homingTarget(head.id());
//                if ( originX < GameRules.SCREEN_WIDTH / 4 ) {
//                    originX  = -100;
//                    originY  = GameRules.SCREEN_HEIGHT / 4;
//                    targetY  = GameRules.SCREEN_HEIGHT / 2;
//                } else {
//                    originX  = GameRules.SCREEN_WIDTH;
//                    originY  = GameRules.SCREEN_HEIGHT / 4;
//                    targetY  = GameRules.SCREEN_HEIGHT / 2;
//                }
//                e.pos(originX, originY);
            }
        }

        v2.set(targetX, targetY).sub(originX, originY).nor().scl(baseVelocity);

        Body body = boxPhysicsSystem.addAsBox(e, cx, cy, 5f, CAT_BULLET, (short) (CAT_AGENT | CAT_PRESIDENT), v2.angleRad());
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setSensor(true);
        }
        body.setGravityScale(0f);
        body.setLinearVelocity(v2.x, v2.y);

        return e;
    }
}
