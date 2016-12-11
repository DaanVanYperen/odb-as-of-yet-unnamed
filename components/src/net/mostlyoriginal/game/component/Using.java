package net.mostlyoriginal.game.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

/**
 * @author Daan van Yperen
 */
public class Using extends Component {
    @EntityId
    public int usingId=-1;

    public void set(int usingId)
    {
        this.usingId = usingId;
    }

}
