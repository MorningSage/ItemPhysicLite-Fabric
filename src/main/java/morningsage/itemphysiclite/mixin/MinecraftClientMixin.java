package morningsage.itemphysiclite.mixin;

import morningsage.itemphysiclite.events.tick.RenderTickEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public class MinecraftClientMixin {
    @Shadow private boolean paused;
    @Shadow private float pausedTickDelta;
    @Shadow @Final private RenderTickCounter renderTickCounter;

    @Inject(
        at = @At(
            value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
            args = "ldc=gameRenderer"
        ),
        method = "render"
    )
    private void renderPre(boolean tick, CallbackInfo callbackInfo) {
        RenderTickEvents.START_RENDER_TICK.invoker().onRenderTick(this.paused ? this.pausedTickDelta : this.renderTickCounter.tickDelta);
    }

    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiler/Profiler;pop()V",
            shift = At.Shift.AFTER
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE_STRING",
                target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
                args = "ldc=gameRenderer"
            )
        ),
        method = "render"
    )
    private void renderPost(boolean tick, CallbackInfo callbackInfo) {
        RenderTickEvents.END_RENDER_TICK.invoker().onRenderTick(this.paused ? this.pausedTickDelta : this.renderTickCounter.tickDelta);
    }
}
