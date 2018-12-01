package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.component.BathroomLevel;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public class LevelSetupSystem extends FluidSystem {

    public static final int Y_OFFSET = 25;
    public Level activeLevel;

    public BoxPhysicsSystem boxPhysicsSystem;
    public GameScreenAssetSystem gameScreenAssetSystem;

    public static class Level {
        public String name;
        public BathroomLevel.Type[] level;
        public int lossCount = 5;
        public float timeBetweenSpawnsEasiest = 12;
        public float timeBetweenSpawnsHardest = 2;
        private int minCount = 1;
        private int maxCount = 1;
        public boolean extraPoops = false;
        public float clockSpeed = 10;
        public boolean tutorial;
        public boolean startDirty;

        public Level(String name, BathroomLevel.Type[] level) {
            this.name = name;
            this.level = level;
        }

        public Level lossCount(int count) {
            this.lossCount = count;
            return this;
        }

        public Level spawnDelay(float easy, float hard) {
            this.timeBetweenSpawnsEasiest = easy;
            this.timeBetweenSpawnsHardest = hard;
            return this;
        }

        public Level spawnCount(int minCount, int maxCount) {
            this.minCount = minCount;
            this.maxCount = maxCount;
            return this;

        }

        public Level extraPoops() {
            this.extraPoops = true;
            return this;
        }

        public Level clockSpeed(int clockSpeed) {
            this.clockSpeed = clockSpeed;
            return this;
        }

        public Level setTutorial(boolean tutorial) {
            this.tutorial = tutorial;
            return this;
        }

        public Level startDirty(boolean b) {
            this.startDirty = b;
            return this;
        }
    }

    private Level introduction = new Level(
            "Protect the President!",
            new BathroomLevel.Type[]{
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
                    BathroomLevel.Type.BUILDING,
            })
            .lossCount(3)
            .clockSpeed(35)
            .spawnDelay(8, 8)
            .startDirty(true)
            .setTutorial(true);


    private Level[] levels = new Level[]{
            introduction
    };

    public LevelSetupSystem() {
        super(Aspect.all(BathroomLevel.class));
    }

    @Override
    protected void initialize() {
        super.initialize();

//        E().bathroomLevelModules(level1);
        loadLevel(levels[MathUtils.clamp(GameRules.level - 1, 0, levels.length - 1)]);
    }

    private void loadLevel(Level level) {
        this.activeLevel = level;
        E()
                .bathroomLevelModules(level.level)
                .bathroomLevelName(level.name);

        E e = E()
                .pos(50, 200)
                .labelText(level.name)
                .tint(0.3f, 0.3f, 0.3f, 1f)
                .bounds(0, 0, 200, 50)
                .fontFontName("5x5")
                .fontScale(1.5f)
                .renderLayer(GameScreenAssetSystem.LAYER_ICONS);

        for (int i = 0; i < 20; i++) {
            addAgent(200 + MathUtils.random(24), 10 + i * 30);
        }

        addPresident(GameRules.SCREEN_WIDTH / 4, 200);


//        if ( activeLevel.tutorial) {
//            E()
//                    .tutorial().tag("tutorial");
//        }
    }

    short CAT_BOUNDARY=0x1;
    short CAT_AGENT=0x2;
    short CAT_CAR=0x4;

    private void addPresident(int x, int y) {
        E e = E()
                .pos(x, y)
                .animId("limo")
                .bounds(0, 0, 72, 24)
                .renderLayer(GameScreenAssetSystem.LAYER_CAR);
        Body car = boxPhysicsSystem.addAsBox(e, e.getBounds().cx(), e.getBounds().cy(), 10f, CAT_CAR, CAT_BOUNDARY);

        E e2 = E()
                .pos(x, y)
                .animId("president")
                .tag("president")
                .bounds(0, 0, 32, 16)
                .renderLayer(GameScreenAssetSystem.LAYER_CAR+10);
        Body president = boxPhysicsSystem.addAsBox(e2, e2.getBounds().cx(), e2.getBounds().cy(), 1f, CAT_CAR, CAT_BOUNDARY);

        E e3 = E()
                .pos(x, y)
                .tag("presidenthead")
                .cameraFocus()
                .bounds(0, 0, 8, 8);
        Body presidentHead = boxPhysicsSystem.addAsBox(e3,4,4, 1f, CAT_CAR, CAT_BOUNDARY);

        {
            final WeldJointDef def = new WeldJointDef();
            def.bodyA = car;
            def.bodyB = president;
            def.collideConnected = false;
            def.type = JointDef.JointType.WeldJoint;
            def.localAnchorA.x = -16/ boxPhysicsSystem.scaling;
            def.localAnchorA.y = 5 / boxPhysicsSystem.scaling;
            def.localAnchorB.x = 0 / boxPhysicsSystem.scaling;
            def.localAnchorB.y = 0 / boxPhysicsSystem.scaling;
            boxPhysicsSystem.box2d.createJoint(def);
        }

        {
            final WeldJointDef def = new WeldJointDef();
            def.bodyA = president;
            def.bodyB = presidentHead;
            def.collideConnected = false;
            def.type = JointDef.JointType.WeldJoint;
            def.localAnchorA.x = 0 / boxPhysicsSystem.scaling;
            def.localAnchorA.y = 2 / boxPhysicsSystem.scaling;
            def.localAnchorB.x = 0 / boxPhysicsSystem.scaling;
            def.localAnchorB.y = 0 / boxPhysicsSystem.scaling;
            boxPhysicsSystem.box2d.createJoint(def);
        }
    }

    private void addAgent(int x, int y) {
        E e = E()
                .pos(x, y)
                .animId("bodyguard_01")
                .bounds(8, 0, 16, 24)
                .guard()
                .renderLayer(GameScreenAssetSystem.LAYER_ACTORS);
        boxPhysicsSystem.addAsBox(e, 8, e.getBounds().cy(), 1f, CAT_AGENT, (short)(CAT_BOUNDARY|CAT_AGENT));
    }

    private int gx = 0;

    @Override
    protected void process(E e) {
        if (!e.bathroomLevelInitialized()) {
            e.bathroomLevelInitialized(true);
            if (e.bathroomLevelModules() != null) {
                for (BathroomLevel.Type type : e.bathroomLevelModules()) {
                    e.bathroomLevelModuleEntityIds().add(initModule(type));
                }
            }
        }
    }

    private int initIndex = 0;

    private int initModule(BathroomLevel.Type type) {

        int moduleId = -1;

        switch (type) {
            case BUILDING:
                moduleId = spawnBuilding(gx, BoxPhysicsSystem.FLOOR_LEVEL_Y- 10);
                break;
        }
        initIndex++;

        return moduleId;
    }


    private int spawnBuilding(int x, int y) {
//        E()
//                .pos(x, y)
//                .render(GameScreenAssetSystem.LAYER_BACKGROUND)
//                .anim(getBackground());

        String sprite = "building_0" + MathUtils.random(1, 3);
        E e = E()
                .pos(x, y)
                .render(GameScreenAssetSystem.LAYER_BACKGROUND + 1)
                .tint(1f, 1f, 1f, 0.6f)
                .bounds(0, 0, GameScreenAssetSystem.BUILDING_WIDTH, GameScreenAssetSystem.BUILDING_HEIGHT)
                .anim(sprite);
        gx = gx + ((TextureRegion)gameScreenAssetSystem.get(sprite).getKeyFrame(0,false)).getRegionWidth();
        return e.id();


    }


}
