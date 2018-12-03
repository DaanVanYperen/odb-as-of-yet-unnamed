package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.graphics.TintWhenSlowdown;
import net.mostlyoriginal.api.component.ui.Label;
import net.mostlyoriginal.api.utils.Duration;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.component.Stagepiece;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static com.artemis.E.E;
import static net.mostlyoriginal.api.operation.JamOperationFactory.tintBetween;
import static net.mostlyoriginal.api.operation.OperationFactory.*;

/**
 * @author Daan van Yperen
 */
public class StagepieceSystem extends FluidSystem {

    private static final int BUILDING_Y = 0;
    private static final int ACTOR_SPAWN_Y = BoxPhysicsSystem.FLOOR_LEVEL_Y + 20;
    public BoxPhysicsSystem boxPhysicsSystem;
    public GameScreenAssetSystem gameScreenAssetSystem;
    private float scrollOffset = 0;
    private SlowTimeSystem slowtimeSystem;
    private LaserPointingSystem laserPointingSystem;

    public StagepieceSystem() {
        super(Aspect.all(Stagepiece.class));
    }

    @Override
    protected void begin() {
        super.begin();
        scrollOffset = world.delta * slowtimeSystem.slowdownFactor() * (40 + laserPointingSystem.difficultyScore * 0.5f);
        gx -= scrollOffset;
    }

    @Override
    protected void end() {
        super.end();

        if (gx < GameRules.SCREEN_WIDTH) {
            spawnBuilding(gx, BUILDING_Y);
        }
    }

    @Override
    protected void process(E e) {
        e.posX(e.posX() - scrollOffset);
        if (e.posX() + e.boundsMaxy() < 0) {
            e.deleteFromWorld();
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        loadLevel();

        while (gx < GameRules.SCREEN_WIDTH) {
            spawnBuilding(gx, BUILDING_Y);
        }
        E()
                .pos(GameRules.SCREEN_WIDTH / 4, 200)
                .labelText("Intercept the rockets! Save the president!")
                .tint(0f, 0f, 0f, 0.8f)
                .fontFontName("5x5")
                .labelAlign(Label.Align.RIGHT)
                .slowTime()
                .script(
                        sequence(
                                delay(Duration.seconds(2)),
                                tintBetween(new Tint("000000ff"), Tint.TRANSPARENT, 2f, Interpolation.pow2),
                                deleteFromWorld()
                        ))
                .fontScale(2f)
                .renderLayer(GameScreenAssetSystem.LAYER_ICONS);

        E()
                .pos(GameRules.SCREEN_WIDTH / 4+2, 202)
                .labelText("Intercept the rockets! Save the president!")
                .labelAlign(Label.Align.RIGHT)
                .tint(1f, 1f, 1f, 0.8f)
                .script(
                        sequence(
                                delay(Duration.seconds(2)),
                                tintBetween(Tint.WHITE, Tint.TRANSPARENT, 2f, Interpolation.pow2),
                                deleteFromWorld()
                        ))
                .fontFontName("5x5")
                .fontScale(2f)
                .renderLayer(GameScreenAssetSystem.LAYER_ICONS + 1);
    }

    private void loadLevel() {
        {
            int carHalf = 72/2;

            int tutorialIndex = 0;
            int targetTutorialIndex = MathUtils.random(0,5);

            for (int i = 0; i < 3; i++) {
                addAgent(50+carHalf + i * 80, ACTOR_SPAWN_Y, i % 2 == 1 ? GameScreenAssetSystem.LAYER_CAR - 50 : GameScreenAssetSystem.LAYER_CAR + 50,
                        50+carHalf + i * 80 - 24, false, tutorialIndex++ == targetTutorialIndex);
            }

            for (int i = 0; i < 3; i++) {
                addAgent(380+carHalf + i * 80, ACTOR_SPAWN_Y, i % 2 == 1 ? GameScreenAssetSystem.LAYER_CAR - 50 : GameScreenAssetSystem.LAYER_CAR + 50,
                        380+carHalf + i * 80 - 24, false, tutorialIndex++ == targetTutorialIndex);
            }

            addPresident(GameRules.SCREEN_WIDTH / 4 + carHalf, ACTOR_SPAWN_Y + 2);

            //addHelicopter(GameRules.SCREEN_WIDTH / 8  + carHalf, 250, 250);
        }
    }

    public void addHelicopter(int x, int y, int targetX) {
        E e = E()
                .pos(x, y)
                .animId("helicopter")
                .theFloorIsLava()
                .hoveringTargetX( targetX )
                .hoveringTargetY( y )
                .rocketLauncher()
                .scale(1f)
                .bounds(31, 0, 62, 50)
                .renderLayer(GameScreenAssetSystem.LAYER_CAR-100);
        Body heli = boxPhysicsSystem.addAsBox(e, e.getBounds().cx() * 0.5F, e.getBounds().cy(), 5f, CAT_HELI, (short) (CAT_BOUNDARY|CAT_AGENT), 0);
        heli.setGravityScale(0.03f);
        Fixture fixture1 = heli.getFixtureList().get(0);
        fixture1.setSensor(true);
    }

    public static final short CAT_BOUNDARY = 1;
    public static final short CAT_AGENT = 2;
    public static final short CAT_CAR = 4;
    public static final short CAT_BULLET = 8;
    public static final short CAT_PRESIDENT = 16;
    public static final short CAT_HELI = 32;

    private void addPresident(int x, int y) {
        E e = E()
                .pos(x, y)
                .animId("limo")
                .locomotion()
                .haunted()
                .tag("presidentcar")
                .bounds(0, 0, 72, 24)
                .renderLayer(GameScreenAssetSystem.LAYER_CAR);
        Body car = boxPhysicsSystem.addAsBox(e, e.getBounds().cx(), e.getBounds().cy(), 10f, CAT_CAR, (short) (CAT_BOUNDARY), 0);

        E e2 = E()
                .pos(x, y)
                .animId(GdxArtemisGame.president++ % 2 == 0 ? "president" : "president_02")
                .tag("president")
                .locomotion()
                .bounds(0, 0, 32, 16)
                .renderLayer(GameScreenAssetSystem.LAYER_CAR - 10);
        Body president = boxPhysicsSystem.addAsBox(e2, e2.getBounds().cx(), e2.getBounds().cy(), 1f, CAT_PRESIDENT, (short) (CAT_BULLET), 0);

        E e3 = E()
                .pos(x, y)
                .tag("presidenthead")
                //.cameraFocus()
                .locomotion()
                .bounds(0, 0, 8, 8);
        Body presidentHead = boxPhysicsSystem.addAsBox(e3, 4, 4, 1f, CAT_CAR, CAT_BOUNDARY, 0);

        {
            final WeldJointDef def = new WeldJointDef();
            def.bodyA = car;
            def.bodyB = president;
            def.collideConnected = false;
            def.type = JointDef.JointType.WeldJoint;
            def.localAnchorA.x = -16 / boxPhysicsSystem.SCALING;
            def.localAnchorA.y = 5 / boxPhysicsSystem.SCALING;
            def.localAnchorB.x = 0 / boxPhysicsSystem.SCALING;
            def.localAnchorB.y = 0 / boxPhysicsSystem.SCALING;
            boxPhysicsSystem.box2d.createJoint(def);
        }

        {
            final WeldJointDef def = new WeldJointDef();
            def.bodyA = president;
            def.bodyB = presidentHead;
            def.collideConnected = false;
            def.type = JointDef.JointType.WeldJoint;
            def.localAnchorA.x = 0 / boxPhysicsSystem.SCALING;
            def.localAnchorA.y = 2 / boxPhysicsSystem.SCALING;
            def.localAnchorB.x = 0 / boxPhysicsSystem.SCALING;
            def.localAnchorB.y = 0 / boxPhysicsSystem.SCALING;
            boxPhysicsSystem.box2d.createJoint(def);
        }
    }

    public void replaceAgent(int layer, float targetX, boolean bandaged) {
        addAgent(-700, ACTOR_SPAWN_Y, layer, targetX, bandaged, false);
    }

    private void addAgent(int x, int y, int layer, float targetX, boolean bandaged, boolean tutorial) {
        E e = E()
                .pos(x, y)
                .animId("bodyguard_01")
                .bounds(8, 0, 16, 24)
                .catapultProjectile()
                .guard()
                .renderLayer(layer)
                .guardTargetX(targetX);

        if (bandaged) {
            e.guardBandaged(true);
        }

        if (tutorial) {
            e.guardTutorial(
                    E.E()
                            .pos(0, 0)
                            .tint(1f, 1f, 1f, 0.8f)
                            .bounds(0, 0, 18, 42)
                            .anim("swipe")
                            .renderLayer(GameScreenAssetSystem.LAYER_ACTORS + 1000).id());
        }

        boxPhysicsSystem.addAsBox(e, 8, e.getBounds().cy(), 1f, CAT_AGENT, (short) (CAT_BOUNDARY | CAT_BULLET | CAT_HELI), 0);
    }

    private float gx = 0;

    private int spawnBuilding(float x, int y) {

        String sprite = "building_0" + MathUtils.random(1, 7);
        int width = ((TextureRegion) gameScreenAssetSystem.get(sprite).getKeyFrame(0, false)).getRegionWidth();

        E e = E()
                .pos(x, y)
                .render(GameScreenAssetSystem.LAYER_BACKGROUND + 1)
                .tintWhenSlowdown()
                .stagepiece()
                .tint(1f, 1f, 1f, 1f)
                .bounds(0, 0, width, GameScreenAssetSystem.BUILDING_HEIGHT)
                .anim(sprite);

        TintWhenSlowdown t = e.getTintWhenSlowdown();
        t.normal.set(1f, 1f, 1f, 8f);
        t.slow.set(1f, 1f, 1f, 0.4f);

        gx = gx + width;
        return e.id();
    }


}
