package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.component.Guard;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

/**
 * @author Daan van Yperen
 */
public class MouseCatapultSystem extends FluidSystem {

    Fixture focusFixture;

    private BoxPhysicsSystem boxPhysicsSystem;
    private QueryCallback callback = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            if (fixture.getFilterData().categoryBits == StagepieceSystem.CAT_AGENT) {
                focusFixture = fixture;
                return true;
            } else return false;
        }
    };
    private float posX;
    private float posY;
    private MouseJoint mouseJoint;
    private E arrow;
    private E tutorial;
    private ParticleSystem particleSystem;
    private E spotlight;
    private E tutorialFocus;
    private GameScreenAssetSystem gameScreenAssetSystem;
    private float lastPosX;
    private float lastPosY;
    private E sfxButton;
    private E musicButton;

    public void setTutorialFocus(E tutorialFocus) {
        this.tutorialFocus = tutorialFocus;
    }

    public MouseCatapultSystem() {
        super(Aspect.all(MouseCursor.class, Pos.class));
    }

    @Override
    protected void initialize() {
        super.initialize();
        arrow = E.E()
                .pos(0, 0)
                .invisible()
                .tint(1f, 1f, 1f, 0.6f)
                .bounds(0, 0, 14, 28)
                .anim("arrow")
                .renderLayer(GameScreenAssetSystem.LAYER_ACTORS + 1000);
        spotlight = E.E()
                .pos(0, 0)
                .invisible()
                .tint(1f, 1f, 1f, 0.4f)
                .bounds(0, 0, 28, 28)
                .anim("spotlight")
                .renderLayer(GameScreenAssetSystem.LAYER_CAR - 55);

        sfxButton = E.E()
                .pos(GameRules.SCREEN_WIDTH / 2 - 50, GameRules.SCREEN_HEIGHT / 2 - 50)
                .tint(1f, 1f, 1f, 0.4f)
                .anim("icon_sound")
                .renderLayer(8000);


        musicButton = E.E()
                .pos(GameRules.SCREEN_WIDTH / 2 - 50 - 50, GameRules.SCREEN_HEIGHT / 2 - 50)
                .tint(1f, 1f, 1f, 0.4f)
                .anim("icon_music")
                .renderLayer(8000);

        sfxButton.anim(GameRules.sfxOn ? "icon_sound" : "icon_sound_off");
        musicButton.anim(GameRules.sfxOn ? "icon_music" : "icon_music_off");
    }

    @Override
    protected void begin() {
        super.begin();
    }

    private Vector2 origin = new Vector2();
    private Vector2 v2 = new Vector2();
    private E dragging;

    boolean released = true;

    @Override
    protected void end() {
        super.end();
    }

    @Override
    protected void process(E e) {
        Vector3 pos = e.getPos().xy;
        posX = pos.x / boxPhysicsSystem.SCALING;
        posY = pos.y / boxPhysicsSystem.SCALING;

        focusFixture = null;
        scanFor(pos, 5);
        if (focusFixture == null) scanFor(pos, 10);
        if (focusFixture == null) scanFor(pos, 15);


        sfxButton.tint(1f,1f,1f,0.4f);
        musicButton.tint(1f,1f,1f,0.4f);
        if (released && e.posY() > GameRules.SCREEN_HEIGHT / 2 - 50) {
            if (e.posX() > GameRules.SCREEN_WIDTH / 2 - 50) {
                sfxButton.tint(1f,1f,1f,1f);
            } else if (e.posX() > GameRules.SCREEN_WIDTH / 2 - 100) {
                musicButton.tint(1f,1f,1f,1f);
            }
        }

        if (Gdx.input.isTouched()) {

            if (released && e.posY() > GameRules.SCREEN_HEIGHT / 2 - 50) {
                if (e.posX() > GameRules.SCREEN_WIDTH / 2 - 50) {
                    released = false;
                    GameRules.sfxOn = !GameRules.sfxOn;
                    sfxButton.anim(GameRules.sfxOn ? "icon_sound" : "icon_sound_off");

                } else if (e.posX() > GameRules.SCREEN_WIDTH / 2 - 100) {
                    released = false;
                    GameRules.musicOn = !GameRules.musicOn;
                    if (GameRules.musicOn) {
                        GameRules.music.play();
                    } else {
                        GameRules.music.pause();
                    }
                    musicButton.anim(GameRules.musicOn ? "icon_music" : "icon_music_off");
                }
            }

            if (focusFixture != null && focusFixture.getBody() != boxPhysicsSystem.groundBody && focusFixture.getBody().getUserData() != null && dragging == null) {
                dragging = (E) focusFixture.getBody().getUserData();
                origin.set(e.posX(), e.posY());
                if (!dragging.hasCatapultProjectile()) {
                    dragging = null;
                } else {
                    dragging.guardState(Guard.State.CROUCHING);
                }
            }

            if (dragging != null) {
                dragging.slowTimeCooldown(0.2f);

                if (lastPosX != pos.x && lastPosY != pos.y && v2.set(lastPosX, lastPosY).dst2(pos.x, pos.y) > 16) {
                    lastPosX = pos.x;
                    lastPosY = pos.y;
                    gameScreenAssetSystem.playSfx("tick1", 0.7f);
                }
            }
        } else {
            released = true;
            if (dragging != null) {
                if (dragging.hasBoxed()) {
                    // start jump.
                    Body body = dragging.boxedBody();
                    dragging.guardState(Guard.State.JUMPING);
                    dragging.slowTimeCooldown(3f);
                    dragging.animAge(0);
                    v2.set(dragging.posX(), dragging.posY()).sub(e.posX() - 12, e.posY() - 12).clamp(20f, 60f).scl(1.2f).scl(body.getMass());
                    body.applyLinearImpulse(v2.x, v2.y,
                            (dragging.posX() + dragging.boundsCx()) / boxPhysicsSystem.SCALING,
                            (dragging.posY() + dragging.boundsCy()) / boxPhysicsSystem.SCALING, true);
                    gameScreenAssetSystem.playSfx("jump1");
//                    particleSystem.confettiBomb(dragging.posX() + dragging.boundsCx(), dragging.posY() + dragging.boundsCy());

                }
                dragging = null;
            }
        }

        if (dragging != null && dragging.hasBoxed()) {

            Body body = dragging.boxedBody();
            v2.set(dragging.posX(), dragging.posY()).sub(e.posX() - 12, e.posY() - 12).clamp(20f, 60f).scl(body.getMass()).scl(0.2f);

            arrow.removeInvisible();
            arrow.posX(dragging.posX() + dragging.boundsCx() + v2.x - arrow.boundsCx());
            arrow.posY(dragging.posY() + dragging.boundsCy() + v2.y - arrow.boundsCy());
            arrow.angleRotation(v2.angle() - 90);

        } else {
            arrow.invisible();
        }

        E spotlightTarget = (dragging != null && dragging.hasBoxed() ? dragging : tutorialFocus);
        if (spotlightTarget != null) {
            spotlight.removeInvisible();
            spotlight.posX(spotlightTarget.posX() + spotlightTarget.boundsCx() - spotlight.boundsCx());
            spotlight.posY(spotlightTarget.posY() + spotlightTarget.boundsCy() - spotlight.boundsCy() - 4);
        } else {
            spotlight.invisible();
        }
    }

    private void scanFor(Vector3 pos, int pixeldistance) {
        boxPhysicsSystem.box2d.QueryAABB(callback, (pos.x - pixeldistance) / boxPhysicsSystem.SCALING, (pos.y - pixeldistance) / boxPhysicsSystem.SCALING, (pos.x + pixeldistance) / boxPhysicsSystem.SCALING, (pos.y + pixeldistance) / boxPhysicsSystem.SCALING);
    }

    public void forgetJoint(JointEdge jointEdge) {
        if (mouseJoint == jointEdge.joint) {
            mouseJoint = null;
        }
    }
}
