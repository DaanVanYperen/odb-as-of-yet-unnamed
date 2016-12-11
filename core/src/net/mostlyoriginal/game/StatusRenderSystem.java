package net.mostlyoriginal.game;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.component.module.TipBowl;
import net.mostlyoriginal.game.component.state.Clogged;
import net.mostlyoriginal.game.component.state.Dirty;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

/**
 * @author Daan van Yperen
 */
public class StatusRenderSystem extends FluidSystem {

    CameraSystem cameraSystem;
    GameScreenAssetSystem assetSystem;
    private TextureRegion iconCoin;
    private TextureRegion iconTexture;
    private TextureRegion iconSad;
    private TextureRegion iconPlunger;
    private TextureRegion iconMop;

    private float age;

    public StatusRenderSystem() {
        super(Aspect.one(TipBowl.class, Dirty.class, Clogged.class));
    }

    private SpriteBatch batch;

    @Override
    protected void initialize() {
        batch = new SpriteBatch(100);
    }

    @Override
    protected void begin() {
        super.begin();

        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();
        iconCoin = assetSystem.get("icon_coin").getKeyFrame(0);
        iconTexture = assetSystem.get("icon_sad").getKeyFrame(0);
        iconSad = assetSystem.get("icon_sad2").getKeyFrame(0);
        iconPlunger = assetSystem.get("icon_plunger").getKeyFrame(0);
        iconMop = assetSystem.get("icon_mop").getKeyFrame(0);

        age += world.delta;
    }

    @Override
    protected void end() {
        super.end();
        batch.end();
    }

    @Override
    protected void process(E e) {

        if ( e.hasTipBowl() ) {
            renderTipBowlUI(e);
        }

        if ( e.isDirty() || e.isClogged() )
        {
            renderActionablesUI(e);
        }

    }

    private void renderActionablesUI(E e) {

        int yBounce = (int) Interpolation.fade.apply(0,8,Math.abs(1-(((age+e.posX()*0.1f)*2f)%2f)));
        int yOff = 64+yBounce;
        int xOff = 4;
        if ( e.isClogged() ) {
            batch.draw(iconPlunger, e.posX()+ xOff, e.posY()+ yOff );
        }
        if ( e.isDirty() ) {
            batch.draw(iconMop, e.posX()+ xOff, e.posY()+ yOff);
        }
    }

    private void renderTipBowlUI(E e) {
        for (int i = 0; i < e.tipBowlCoins(); i++) {
            batch.draw(iconCoin, 64 + i * 8, 24);
        }

        for (int i = 0; i < e.tipBowlMaxAnger(); i++) {
            batch.draw(e.tipBowlAnger() > i ? iconTexture : iconSad, 28 + i * 8, 148);
        }
    }
}
