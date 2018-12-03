package net.mostlyoriginal.game.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class RocketLauncher extends Component {
    public RocketType type = RocketType.SMALL;
    public float offsetX = 25;
    public float offsetY = 15;
    public float cooldown = 2;
    public float interval = 4;

    public enum RocketType {
        SMALL,
        BIG;
    }
}
