package morningsage.itemphysiclite.events.tick;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class RenderTickEvents {
    public static final Event<RenderTick> START_RENDER_TICK = EventFactory.createArrayBacked(RenderTick.class,
        callbacks -> (tickDelta) -> {
            for (RenderTick event : callbacks) {
                event.onRenderTick(tickDelta);
            }
        }
    );
    public static final Event<RenderTick> END_RENDER_TICK = EventFactory.createArrayBacked(RenderTick.class,
        callbacks -> (tickDelta) -> {
            for (RenderTick event : callbacks) {
                event.onRenderTick(tickDelta);
            }
        }
    );

    @FunctionalInterface
    public interface RenderTick {
        void onRenderTick(float tickDelta);
    }
}
