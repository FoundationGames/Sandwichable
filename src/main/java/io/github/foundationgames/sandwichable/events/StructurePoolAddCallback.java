package io.github.foundationgames.sandwichable.events;

import io.github.foundationgames.sandwichable.worldgen.ModifiableStructurePool;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface StructurePoolAddCallback {
    //Huge thanks to Draylar for help with this; https://github.com/Draylar/structurized
    Event<StructurePoolAddCallback> EVENT = EventFactory.createArrayBacked(StructurePoolAddCallback.class,
        listeners -> pool -> {
            for (StructurePoolAddCallback listener : listeners) {
                listener.add(pool);
            }
        });

    void add(ModifiableStructurePool pool);
}
