package me.maximumpower55.sapphire.backend.mixin.opengl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.backend.opengl.DirectStateAccess;

@Mixin(targets = "com.mojang.renderpearl.backend.opengl.GlCommandEncoder")
public class GlCommandEncoderMixin {
	@WrapOperation(method = "presentTexture", at = @At(value = "INVOKE", target = "Lcom/mojang/renderpearl/backend/opengl/DirectStateAccess;blitFrameBuffers(IIIIIIIIIIII)V"))
	private static void splitSourceAndSwapchainSize(
			DirectStateAccess instance,
			int source, int dest,
			int srcX0, int srcY0, int srcX1, int srcY1,
			int dstX0, int dstY0, int dstX1, int dstY1,
			int mask, int filter, Operation<Void> original,
			@Local(argsOnly = true, name = "textureView") GpuTextureView textureView,
			@Local(argsOnly = true, name = "swapchainWidth") int swapchainWidth,
			@Local(argsOnly = true, name = "swapchainHeight") int swapchainHeight
	) {
		original.call(instance, source, dest, 0, 0, textureView.getWidth(0), textureView.getHeight(0), 0, 0, swapchainWidth, swapchainHeight, mask, filter);
	}
}
