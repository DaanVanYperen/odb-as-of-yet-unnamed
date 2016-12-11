package net.mostlyoriginal.game;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.component.module.TipBowl;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

/**
 * @author Daan van Yperen
 */
public class AngerRenderSystem extends FluidSystem {

    CameraSystem cameraSystem;
    GameScreenAssetSystem assetSystem;
    private TextureRegion coinTexture;
    private TextureRegion sadTexture;
    private TextureRegion sadDisabledTexture;

    public AngerRenderSystem() {
        super(Aspect.all(TipBowl.class));
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
        coinTexture = assetSystem.get("icon_coin").getKeyFrame(0);
        sadTexture = assetSystem.get("icon_sad").getKeyFrame(0);
        sadDisabledTexture = assetSystem.get("icon_sad2").getKeyFrame(0);
    }

    @Override
    protected void end() {
        super.end();
        batch.end();
    }

    @Override
    protected void process(E e) {

        for (int i = 0; i < e.tipBowlCoins(); i++) {
            batch.draw(coinTexture, 64+i * 8, 24);
        }

        for (int i = 0; i < e.tipBowlMaxAnger(); i++) {
            batch.draw(e.tipBowlAnger() > i ? sadTexture : sadDisabledTexture, 28 + i * 8, 148);
        }

    }
}
