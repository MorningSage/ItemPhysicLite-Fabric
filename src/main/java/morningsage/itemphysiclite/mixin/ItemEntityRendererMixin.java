package morningsage.itemphysiclite.mixin;

import morningsage.itemphysiclite.ItemPhysicLite;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {
	@Shadow @Final private ItemRenderer itemRenderer;
	@Shadow @Final private Random random;

	protected ItemEntityRendererMixin(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Inject(
		at = @At("HEAD"),
		method = "render",
		cancellable = true
	)
	public void render(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo callbackInfo) {
		if (ItemPhysicLite.renderItem(itemEntity, matrixStack, vertexConsumerProvider, i, this.itemRenderer, this.random)) {
			super.render(itemEntity, f, g, matrixStack, vertexConsumerProvider, i);
			callbackInfo.cancel();
		}
	}
}
