package com.dragonBones.events;

public abstract class EventManager {
    public abstract void handle(EventData eventData);
    public abstract boolean isHandle(EventData.EventType eventData);
}
