package morningsage.itemphysiclite;

import morningsage.itemphysiclite.events.tick.RenderTickEvents;
import morningsage.itemphysiclite.mixin.accessors.EntityAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class ItemPhysicLite implements ClientModInitializer {
	private static final MinecraftClient client = MinecraftClient.getInstance();
	public static long lastTickTime;

	@Override
	public void onInitializeClient() {
		RenderTickEvents.END_RENDER_TICK.register(tickDelta -> lastTickTime = System.nanoTime());
	}

	public static boolean renderItem(ItemEntity entityIn, MatrixStack matrixStackIn, VertexConsumerProvider vertexConsumers, int packedLightIn, ItemRenderer itemRenderer, Random random) {
		if (entityIn.getAge() == 0) return false;

		ItemStack itemstack = entityIn.getStack();
		BakedModel bakedModel = itemRenderer.getHeldItemModel(itemstack, entityIn.world, null);
		Vec3d motionMultiplier = ((EntityAccessor) entityIn).getMovementMultiplier();

		double height = 0.2D;
		float rotateBy = 0.0F;
		boolean hasDepth = bakedModel.hasDepth();
		int itemCount = getStackCount(itemstack);

		matrixStackIn.push();
		random.setSeed(itemstack.isEmpty() ? 187 : Item.getRawId(itemstack.getItem()) + itemstack.getDamage());

		if (!client.isPaused()) rotateBy = (System.nanoTime() - lastTickTime) / 200000000F;
		if (motionMultiplier != null && motionMultiplier.lengthSquared() > 0) rotateBy *= motionMultiplier.x * 0.2;

		matrixStackIn.multiply(Vector3f.POSITIVE_X.getRadialQuaternion((float) Math.PI / 2));
		matrixStackIn.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(entityIn.yaw));

		if (hasDepth || client.getEntityRenderDispatcher().gameOptions != null) {
			if (!entityIn.isOnGround()) {
				entityIn.pitch += rotateBy * 2;
			} else {
				entityIn.pitch = 0;
			}

			if (hasDepth) {
				matrixStackIn.translate(0.0, -0.2, -0.08);
			} else if (entityIn.world != null && entityIn.world.getBlockState(entityIn.getBlockPos()).getBlock() == Blocks.SNOW) {
				matrixStackIn.translate(0.0, 0.0, -0.14);
			} else {
				matrixStackIn.translate(0.0, 0.0, -0.04);
			}

			if (hasDepth) matrixStackIn.translate(0, height, 0);
			matrixStackIn.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(entityIn.pitch));
			if (hasDepth) matrixStackIn.translate(0, -height, 0);
		}

		if (!hasDepth) {
			matrixStackIn.translate(
				-0.0F * (itemCount - 1) * 0.5F,
				-0.0F * (itemCount - 1) * 0.5F,
				-0.09375F * (itemCount - 1) * 0.5F
			);
		}

		for (int i = 0; i < itemCount; ++i) {
			matrixStackIn.push();
			if (i > 0) {
				if (hasDepth) {
					matrixStackIn.translate(
						(random.nextFloat() * 2.0F - 1.0F) * 0.15F,
						(random.nextFloat() * 2.0F - 1.0F) * 0.15F,
						(random.nextFloat() * 2.0F - 1.0F) * 0.15F
					);
				} else {
					matrixStackIn.translate(0.0, 0.0, 0.05375F);
				}
			}

			itemRenderer.renderItem(itemstack, ModelTransformation.Mode.GROUND, false, matrixStackIn, vertexConsumers, packedLightIn, OverlayTexture.DEFAULT_UV, bakedModel);
			matrixStackIn.pop();
		}

		matrixStackIn.pop();
		return true;
	}

	public static int getStackCount(ItemStack stack) {
		if (stack.getCount() > 48) return 5;
		if (stack.getCount() > 32) return 4;
		if (stack.getCount() > 16) return 3;
		if (stack.getCount() > 1) return 2;

		return 1;
	}
}
