package dragonBones.events;

/**
 * Created by liujingzhao on 6/3/16.
 */
public abstract class EventManager {
    public abstract void handle(EventData eventData);
    public abstract boolean isHandle(EventData.EventType eventData);
}
