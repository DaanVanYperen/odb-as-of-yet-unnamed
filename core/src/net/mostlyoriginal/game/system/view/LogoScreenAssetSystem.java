package net.mostlyoriginal.game.system.view;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.Animation;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class LogoScreenAssetSystem extends AbstractAssetSystem {

	public static final int LOGO_WIDTH = 344;
	public static final int LOGO_HEIGHT = 209;

	public LogoScreenAssetSystem() {
		super("tileset.png");
	}

	@Override
	protected void initialize() {
		super.initialize();
		add("logo", 359,665, LOGO_WIDTH, LOGO_HEIGHT, 1);
	}

	@Override
	public Animation get(String identifier) {
		return super.get(identifier);
	}

}
