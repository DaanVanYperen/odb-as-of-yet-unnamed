package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.component.Desire;
import net.mostlyoriginal.game.component.Emotion;
import net.mostlyoriginal.game.component.Interactable;
import net.mostlyoriginal.game.component.Player;
import net.mostlyoriginal.game.component.state.InUse;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static com.artemis.E.E;
import static net.mostlyoriginal.game.system.view.GameScreenAssetSystem.LAYER_ACTORS;
import static net.mostlyoriginal.game.system.view.GameScreenAssetSystem.LAYER_ACTORS_BUSY;
import static net.mostlyoriginal.game.system.view.GameScreenAssetSystem.LAYER_PLAYER;

/**
 * @author Daan van Yperen
 */
public class UseSystem extends FluidSystem {

    public static final int ACT_OFFSET_Y = 32;
    public static final float COOLDOWN_AFTER_USAGE = 0.8f;
    private CoinSystem coinSystem;

    public UseSystem() {
        super(Aspect.all(InUse.class, Interactable.class));
    }

    GameScreenAssetSystem assetSystem;
    RenderBatchingSystem renderBatchingSystem;
    EmotionService emotionService;

    @Override
    protected void process(E e) {

        float delta = world.getDelta();
        E actor = getActor(e);

        if (e.inUseDuration() == 0) {
            startUsing(e, actor);
        }

        if (!actor.hasPlayer() || e.interactableDuration() == 0) {
            e.inUseDuration(e.inUseDuration() + delta);
        }

        if (e.inUseDuration() >= e.interactableDuration()) {
            finishUsing(e);
        } else {
            continueUsing(e);
        }
    }

    private void continueUsing(E e) {
        if (e.interactableEndAnimId() != null) {
            e.anim(e.interactableStartAnimId());
        }
    }

    private void finishUsing(E e) {
        if (e.inUseUserId() != -1) {
            E actor = getActor(e);
            e.interactableCooldownBefore(COOLDOWN_AFTER_USAGE);
            applyEffects(e, actor);
            stopBeingUsed(e, actor);
        }
    }

    private E getActor(E e) {
        return E(e.inUseUserId());
    }

    private void applyEffects(E thing, E actor) {
        if (actor.hasPlayer()) {
            finishAsPlayer(thing, actor);
        } else {
            finishAsVisitor(thing, actor);
        }
    }

    private void finishAsPlayer(E thing, E actor) {
        if (thing.hasDirty() && actor.playerTool() == Player.Tool.MOP) {
            thing.removeDirty();
        }
        if (thing.isClogged() && actor.playerTool() == Player.Tool.PLUNGER) {
            thing.removeClogged();
        }
        if (thing.isInventory()) {
            actor.playerNextTool();
        }
    }

    private void finishAsVisitor(E thing, E actor) {
        if (thing.hasToilet()) {
            worsenToiletState(thing);
            washHandsTipOrLeave(thing, actor);
        }
        if (thing.hasUrinal()) {
            worsenUrinalState(thing);
            washHandsTipOrLeave(thing, actor);
        }
        if (thing.isSink()) {
            worsenSinkState(thing);
            tipOrLeave(actor);
        }
        if (thing.hasTipBowl()) {
            actor.desireType(Desire.Type.LEAVE);
            coinSystem.payCoin(actor);
        }
        if (thing.isExit()) {
            if (!isHappyEnoughToTip(actor)) {
                coinSystem.leaveAngrily(actor);
            }
            actor.deleteFromWorld();
        }
    }

    private void washHandsTipOrLeave(E thing, E actor) {
        if (thing.hasDirty()) {
            // angry, so dirty, go wash hands. Chance to become even MORE angry!
            washHands(actor);
        } else {
            tipOrLeave(actor);
        }
    }

    private void washHands(E actor) {
        actor.desireType(Desire.Type.WASH_HANDS);
    }

    private void tipOrLeave(E actor) {
        if (isHappyEnoughToTip(actor)) {
            actor.desireType(Desire.Type.TIP);
        } else {
            actor.desireType(Desire.Type.LEAVE);
        }
    }

    private boolean isHappyEnoughToTip(E actor) {
        return actor.emotionState() == Emotion.State.NEUTRAL
                || actor.emotionState() == Emotion.State.HAPPY;
    }

    private void worsenToiletState(E thing) {
        if (MathUtils.random(1, 100) <= GameRules.PERCENTAGE_CHANCE_OF_TOILET_DIRTY_ESCALATION) {
            if (thing.hasDirty()) {
                if (thing.dirtyLevel() == 1 ) thing.dirtyLevel(2);
                if (thing.dirtyLevel() == 0 ) thing.dirtyLevel(1);
                // if dirty, become clogged as well.
                thing.clogged();
            } else if (thing.isClogged()) {
                // if clogged, become dirty as well.
                thing.dirty();
            } else {
                // become dirty or clogged randomly.
                if (MathUtils.randomBoolean()) {
                    thing.dirty();
                } else {
                    thing.clogged();
                }
            }
        }
    }

    private void worsenUrinalState(E thing) {
        if (MathUtils.random(1, 100) <= GameRules.PERCENTAGE_CHANCE_OF_URINAL_DIRTY_ESCALATION) {
            thing.dirty();
        }
    }


    private void worsenSinkState(E thing) {
        if (MathUtils.random(1, 100) <= GameRules.PERCENTAGE_CHANCE_OF_SINK_DIRTY_ESCALATION) {
            if (thing.hasDirty()) {
                thing.dirtyLevel(1);
            } else {
                thing.dirty();
            }
        }

    }

    public void stopBeingUsed(E e, E actor) {
        actor.renderLayer(actor.hasPlayer() ? LAYER_PLAYER : LAYER_ACTORS);
        actor.posY(actor.posY() - e.interactableUseOffsetY());
        renderBatchingSystem.sortedDirty = true;

        if( !actor.hasPlayer() && (e.hasToilet()||e.hasUrinal())) {
            assetSystem.playFlushSfx();
        }
        if (e.hasExit() || e.hasEntrance()) {
            assetSystem.playDoorCloseSfx();
        }
        if (e.hasToilet()) {
            assetSystem.playDoorCloseSfx();
        }

        if (e.interactableEndAnimId() != null) {
            e.anim(e.interactableEndAnimId());
        }
        if (e.inUseUserId() != -1) {
            getActor(e).removeUsing();
        }
        e.removeInUse();
    }

    public void startUsing(E actor, E item) {
        if (item == null) return;
        if (item.hasTipBowl() && actor.hasPlayer()) return;
        if (item.hasInteractable() && item.interactableCooldownBefore() <= 0) {

            if (actor.hasPlayer() && !item.isInventory()) {
                switch (actor.playerTool()) {
                    case PLUNGER:
                        assetSystem.playPlungerSfx();
                        break;
                    case MOP:
                        assetSystem.playMopSfx();
                        break;
                }
            }
            if (!actor.hasPlayer()){
                if (item.hasToilet()){
                    assetSystem.playPoopSfx();
                }
                if (item.hasUrinal()){
                    assetSystem.playPeeSfx();
                }
                if (item.hasSink())
                {
                    assetSystem.playSinkSfx();
                }
            }

            if (item.hasInUse()) {
                if (item.inUseUserId() == actor.id()) {
                    playerContinueUsing(actor, item);
                }
            } else {

                startUsingFromScratch(actor, item);
            }
        }
    }

    private void playerContinueUsing(E actor, E item) {
        item.inUseDuration(item.inUseDuration() + actor.playerTool().multiplier);
    }

    private void startUsingFromScratch(E actor, E item) {
        if (!actor.hasPlayer()) {
            startUsingAsVisitor(actor, item);
        } else {
            startUsingAsPlayer(actor, item);
        }

        if (item.hasExit() || item.hasEntrance()) {
            assetSystem.playDoorOpenSfx();
        }
        if (item.hasToilet()) {
            assetSystem.playDoorCloseSfx();
        }
        if (item.isInventory()){
            assetSystem.playSuppliesSfx();
        }

        actor.removeHunt().renderLayer(LAYER_ACTORS_BUSY);
        actor.posY(actor.posY() + item.interactableUseOffsetY());
        renderBatchingSystem.sortedDirty = true;
        actor.using(item.id());
        item.inUse(actor.id());
    }

    private void startUsingAsVisitor(E actor, E item) {
        if (item.hasDirty()) emotionService.worsenanger(actor);
        if (item.isClogged()) emotionService.worsenanger(actor);
    }

    private void startUsingAsPlayer(E actor, E item) {

    }

}
