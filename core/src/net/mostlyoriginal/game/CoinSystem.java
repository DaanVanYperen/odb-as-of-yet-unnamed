package net.mostlyoriginal.game;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.game.component.module.TipBowl;
import net.mostlyoriginal.game.system.common.FluidSystem;
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

    int coinsPending = 0;

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
            coinsPending = 0;
        }
    }

    public void payCoin(E e) {
        coinsPending++;
        E()
                .pos(e.posX(), e.posY()+32)
                .anim("icon_coin")
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
