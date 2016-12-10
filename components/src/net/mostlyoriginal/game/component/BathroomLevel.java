package net.mostlyoriginal.game.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class BathroomLevel extends Component {
    public static final int MAX_SLOTS = 32;

    public BathroomLevel() {}

    public Type[] modules;
    public boolean initialized;

    public enum Type {
        ENTRANCE,
        TOILET,
        SUPPLY_CLOSET
    }
}
