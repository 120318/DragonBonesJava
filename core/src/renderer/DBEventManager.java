package renderer;

import com.badlogic.gdx.Gdx;
import dragonBones.events.EventData;
import dragonBones.events.EventManager;

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujingzhao on 6/3/16.
 */
public class DBEventManager extends EventManager {

    private List<DBEventHandler> eventHandlers = new ArrayList<DBEventHandler>();

    @Override
    public void handle(EventData eventData) {
        for(DBEventHandler eventHandler : eventHandlers){
            eventHandler.handle(eventData);
        }
    }
    public void addHandle(DBEventHandler eventHandler){
        eventHandlers.add(eventHandler);
    }
    public boolean removeHandle(DBEventHandler eventHandler){
        if(eventHandlers.contains(eventHandler)){
            return eventHandlers.remove(eventHandler);
        }
        return false;
    }

    @Override
    public boolean isHandle(EventData.EventType eventType) {
        return eventHandlers.size() > 0;
    }

}
