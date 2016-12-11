package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.component.module.TipBowl;
import net.mostlyoriginal.game.screen.LogoScreen;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.logic.TransitionSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static com.artemis.E.E;
import static net.mostlyoriginal.api.operation.OperationFactory.*;

/**
 * @author Daan van Yperen
 */
public class CoinSystem extends FluidSystem {
    public CoinSystem() {
        super(Aspect.all(TipBowl.class));
    }

    int xOffset=0;
    int coinsPending = 0;
    int angerPending = 0;
    boolean finishing = false;

    @Override
    protected void process(E e) {
        increaseTipsWithPending(e);
        showMassedWealth(e);
    }

    private void showMassedWealth(E e) {
        E(e.tipBowlBowlId()).anim(coinAnim(e));
    }

    private void increaseTipsWithPending(E e) {
        if (coinsPending>0) {
            e.tipBowlCoins(e.tipBowlCoins() + coinsPending);
            GameRules.lastScore += coinsPending;
            coinsPending = 0;
        }
        if (angerPending >0) {
            e.tipBowlAnger(e.tipBowlAnger() + angerPending);
            angerPending = 0;
            considerLossCondition(e);
        }
    }

    private void considerLossCondition(E e) {
        if ( !finishing && e.tipBowlAnger() >= e.tipBowlMaxAnger()) {
            finishing = true;
            world.getSystem(TransitionSystem.class).transition(LogoScreen.class, 3);
        }
    }

    GameScreenAssetSystem assetSystem;

    public void payCoin(E e) {
        coinsPending++;
        assetSystem.playCoinSfx();
        feedbackIcon("icon_coin", e.posX()+2, e.posY() + 32);
    }

    public void leaveAngrily(E e) {
        angerPending++;
        assetSystem.playGuestAngrySfx();
        feedbackIcon("icon_sad", e.posX()+2, e.posY() + 48);
    }

    private void feedbackIcon(String icon_coin, float x, float y) {
//        E()
//                .pos(24 + (xOffset+=10),12)
//                .anim(icon_coin)
//                .renderLayer(GameScreenAssetSystem.LAYER_ICONS);
        E()
                .pos(x, y)
                .anim(icon_coin)
                .renderLayer(GameScreenAssetSystem.LAYER_ICONS)
                .physicsVy(20)
                .physicsFriction(0)
                .script(
                        tween(Tint.WHITE, Tint.TRANSPARENT,3f)
                );
    }

    private String coinAnim(E e ) {
        if ( e.tipBowlCoins() > 16 )
            return "coin_5";
        if ( e.tipBowlCoins() > 8 )
            return "coin_4";
        if ( e.tipBowlCoins() > 4 )
            return "coin_3";
        if ( e.tipBowlCoins() > 1 )
            return "coin_2";
        if ( e.tipBowlCoins() > 0 )
            return "coin_1";
        return "missing";
    }
}
