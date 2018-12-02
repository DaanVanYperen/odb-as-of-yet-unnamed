package net.mostlyoriginal.game.screen;

import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.manager.FontManager;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.api.system.mouse.MouseCursorSystem;
import net.mostlyoriginal.api.system.physics.PhysicsSystem;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.system.*;
import net.mostlyoriginal.game.system.logic.TransitionSystem;
import net.mostlyoriginal.game.system.render.CameraFollowSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;
import net.mostlyoriginal.game.system.view.MyClearScreenSystem;
import net.mostlyoriginal.plugin.OperationsPlugin;
import net.mostlyoriginal.plugin.ProfilerPlugin;

/**
 * Example main game screen.
 *
 * @author Daan van Yperen
 */
public class GameScreen extends TransitionableWorldScreen {

    public static final String BACKGROUND_COLOR_HEX = "969291";

    Class nextScreen;

    @Override
    protected World createWorld() {
        RenderBatchingSystem renderBatchingSystem;
        return new World(new WorldConfigurationBuilder()
                .dependsOn(EntityLinkManager.class, ProfilerPlugin.class, OperationsPlugin.class)
                .with(
                        new SuperMapper(),
                        new EmotionService(),
                        new FontManager(),
                        new TagManager(),
                        new TutorialService()
                )
                .with(

                        // Replace with your own systems!
                        new CameraSystem(2),
                        new MyClearScreenSystem(Color.valueOf(BACKGROUND_COLOR_HEX)),
                        new GameScreenAssetSystem(),
                        new StagepieceSystem(),
                        //new EntranceSystem(),
                        //new DesireSystem(),
                        //new HuntSystem(),
                        //new UseSystem(),
                        //new ToiletSystem(),
                        //new SinkSystem(),
                        //new UrinalSystem(),
                        //new VisitorSystem(),
                        //new PlayerControlSystem(),
                        //new PlayerSystem(),
                        //new ClockSystem(),
                        //new CoinSystem(),
                        new MouseCursorSystem(),
                        new PhysicsSystem(),
                        new BoxPhysicsSystem(),
                        new BoxPhysicsAgentSystem(),
                        new StruckSystem(),
                        new CameraFollowSystem(),
                        new LaserPointingSystem(),
                        new SlowTimeSystem(),
                        new TintWhenSlowdownSystem(),
                        //new MouseThrowSystem(),
                        new MouseCatapultSystem(),
                        new InteractableCooldownSystem(),
                        new ScoreSystem(),
                        renderBatchingSystem = new RenderBatchingSystem(),
                        new MyAnimRenderSystem(renderBatchingSystem),
                        new MyLabelRenderSystem(renderBatchingSystem),
                        new LaserRenderSystem(renderBatchingSystem),
                        //new StatusRenderSystem(),
                        new BoxPhysicsDebugRenderSystem(),
                        new TransitionSystem(GdxArtemisGame.getInstance(),this)
                        //new DiscoSystem()
                ).build());
    }

}
