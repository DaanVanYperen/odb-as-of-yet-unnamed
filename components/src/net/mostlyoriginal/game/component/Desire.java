package net.mostlyoriginal.game.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class Desire extends Component {
    public enum Type {
        LEAVE,
        TIP,
        POOP,
    }
    public Type type;
    public void set(Type type) { this.type = type; }
}
