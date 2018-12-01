package net.mostlyoriginal.game.system;
/**
 * @author Daan van Yperen
 */

import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Origin;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Render;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.delegate.DeferredEntityProcessingSystem;
import net.mostlyoriginal.api.system.delegate.EntityProcessPrincipal;
import net.mostlyoriginal.game.component.Laser;
import net.mostlyoriginal.game.system.view.FeatureScreenAssetSystem;

/**
 * Render and progress animations.
 *
 * @author Daan van Yperen
 * @see Anim
 */
@Wire
public class LaserRenderSystem extends DeferredEntityProcessingSystem {

    protected M<Origin> mOrigin;
    private M<Laser> mLaser;
    private M<Tint> mTint;


    protected CameraSystem cameraSystem;
    protected AbstractAssetSystem abstractAssetSystem;

    protected SpriteBatch batch;
    private Vector2 v2 = new Vector2();

    public LaserRenderSystem(EntityProcessPrincipal principal) {
        super(Aspect.all(Laser.class, Render.class).exclude(Invisible.class), principal);
    }

    @Override
    protected void initialize() {
        super.initialize();
        batch = new SpriteBatch(2000);
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();
    }

    @Override
    protected void end() {
        batch.end();
    }

    protected void process(final int e) {

        final Laser laser = mLaser.get(e);

        batch.setColor(mTint.getSafe(e,Tint.WHITE).color);

        final Animation<TextureRegion> gdxanim = (Animation<TextureRegion>) abstractAssetSystem.get("laser");
        final TextureRegion frame = gdxanim.getKeyFrame(0, false);

        v2.set(laser.targetX,laser.targetY).sub(laser.sourceX,laser.sourceY);

        batch.draw(frame,
                roundToPixels(laser.sourceX),
                roundToPixels(laser.sourceY),
                0.5f,
                0.5f,
                v2.len(),
                1, 1, 1,
                v2.angle());
    }

    /** Pixel perfect aligning. */
    public float roundToPixels(final float val) {
        // since we use camera zoom rounding to integers doesn't work properly.
        return ((int)(val * cameraSystem.zoom)) / (float)cameraSystem.zoom;
    }

}