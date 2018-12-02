package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import net.mostlyoriginal.api.component.graphics.TintWhenSlowdown;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.component.BathroomLevel;
import net.mostlyoriginal.game.component.Stagepiece;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public class StagepieceSystem extends FluidSystem {

    private static final int BUILDING_Y = BoxPhysicsSystem.FLOOR_LEVEL_Y - 8;
    public BoxPhysicsSystem boxPhysicsSystem;
    public GameScreenAssetSystem gameScreenAssetSystem;
    private float scrollOffset=0;
    private SlowTimeSystem slowtimeSystem;

    public StagepieceSystem() {
        super(Aspect.all(Stagepiece.class));
    }

    @Override
    protected void begin() {
        super.begin();
        scrollOffset = world.delta * slowtimeSystem.slowdownFactor() * 100f;
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
    }

    private void loadLevel() {
        {
            for (int i = 0; i < 5; i++) {
                addAgent(100 + i * 30, BoxPhysicsSystem.FLOOR_LEVEL_Y + 20, i % 2 == 1 ? GameScreenAssetSystem.LAYER_CAR - 50 : GameScreenAssetSystem.LAYER_CAR + 50);
            }

            for (int i = 0; i < 5; i++) {
                addAgent(320 + i * 30, BoxPhysicsSystem.FLOOR_LEVEL_Y + 20, i % 2 == 1 ? GameScreenAssetSystem.LAYER_CAR - 50 : GameScreenAssetSystem.LAYER_CAR + 50);
            }

            addPresident(GameRules.SCREEN_WIDTH / 4, 200);
        }
    }

    public static short CAT_BOUNDARY = 0x1;
    public static short CAT_AGENT = 0x2;
    public static short CAT_CAR = 0x4;
    public static short CAT_BULLET = 0x8;
    public static short CAT_PRESIDENT = 0x16;

    private void addPresident(int x, int y) {
        E e = E()
                .pos(x, y)
                .animId("limo")
                .locomotion()
                .tag("presidentcar")
                .bounds(0, 0, 72, 24)
                .renderLayer(GameScreenAssetSystem.LAYER_CAR);
        Body car = boxPhysicsSystem.addAsBox(e, e.getBounds().cx(), e.getBounds().cy(), 10f, CAT_CAR, (short) (CAT_BOUNDARY), 0);

        E e2 = E()
                .pos(x, y)
                .animId("president")
                .tag("president")
                .locomotion()
                .bounds(0, 0, 32, 16)
                .renderLayer(GameScreenAssetSystem.LAYER_CAR - 10);
        Body president = boxPhysicsSystem.addAsBox(e2, e2.getBounds().cx(), e2.getBounds().cy(), 1f, CAT_PRESIDENT, (short) (CAT_BULLET), 0);

        E e3 = E()
                .pos(x, y)
                .tag("presidenthead")
                .cameraFocus()
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

    private void addAgent(int x, int y, int layer) {
        E e = E()
                .pos(x, y)
                .animId("bodyguard_01")
                .bounds(8, 0, 16, 24)
                .guard()
                .renderLayer(layer);
        boxPhysicsSystem.addAsBox(e, 8, e.getBounds().cy(), 1f, CAT_AGENT, (short) (CAT_BOUNDARY | CAT_BULLET), 0);
    }

    private int gx = 0;

    private int spawnBuilding(int x, int y) {

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
