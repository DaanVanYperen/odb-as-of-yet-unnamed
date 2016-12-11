package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
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
    private TextureRegion[] iconProgress = new TextureRegion[5];
    private Animation animIconButton;
    private TextureRegion iconPlungerAndMop;

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
        iconPlungerAndMop = assetSystem.get("icon_plunger_and_mop").getKeyFrame(0);


        iconProgress[0] = assetSystem.get("progress_0").getKeyFrame(0);
        iconProgress[1] = assetSystem.get("progress_25").getKeyFrame(0);
        iconProgress[2] = assetSystem.get("progress_50").getKeyFrame(0);
        iconProgress[3] = assetSystem.get("progress_75").getKeyFrame(0);
        iconProgress[4] = assetSystem.get("progress_100").getKeyFrame(0);
        animIconButton = assetSystem.get("icon_button");


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

        if ( e.hasDirty() || e.isClogged() )
        {
            renderActionablesUI(e);
        }

    }

    private void renderActionablesUI(E e) {

        if ( e.hasInUse() )
        {
            renderProgressPercentage(e);
        } else {
            renderDirtyCloggedIcons(e);
        }
    }

    private void renderDirtyCloggedIcons(E e) {
        int yBounce = (int) Interpolation.fade.apply(0, 8, Math.abs(1 - (((age + e.posX() * 0.1f) * 2f) % 2f)));
        int yOff = 64 + yBounce;
        int xOff = 4;
        if (e.isClogged()&&e.hasDirty()) {
            batch.draw(iconPlungerAndMop, e.posX() + xOff, 64 + yOff);
        } else if (e.isClogged()) {
            batch.draw(iconPlunger, e.posX() + xOff, 64 + yOff);
        } else if (e.hasDirty()) {
            batch.draw(iconMop, e.posX() + xOff, 64 + yOff);
        }
    }

    private void renderProgressPercentage(E e) {
        E actor = getActor(e.inUseUserId());
        if ( actor.hasPlayer() )
        {
            int percentage= (int)(MathUtils.clamp(e.inUseDuration() / e.interactableDuration(), 0f, 1f) * 100);
            // just to indicate player pressed the right button show some progress.
            if ( percentage > 0 && percentage < 25 ) percentage=25;
            batch.draw(iconProgress[(percentage/25)], e.posX()+1, e.posY() + 68);
            batch.draw(animIconButton.getKeyFrame(age,true), e.posX()+3, e.posY() + 74);
        }
    }

    private E getActor(int id) {
        return E.E(id);
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
