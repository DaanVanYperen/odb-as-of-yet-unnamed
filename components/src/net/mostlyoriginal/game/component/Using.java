package net.mostlyoriginal.game.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

/**
 * @author Daan van Yperen
 */
public class Using extends Component {
    @EntityId
    public int usingId;

    public void set(int userId)
    {
        this.usingId = usingId;
    }

}
