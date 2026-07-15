package me.maximumpower55.sapphire.backend.mixin.vulkan;

import java.nio.LongBuffer;
import java.util.Objects;

import org.lwjgl.sdl.SDLError;
import org.lwjgl.sdl.SDLVulkan;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkOffset3D;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.backend.vulkan.VulkanGpuSurface;

@Mixin(VulkanGpuSurface.class)
public class VulkanGpuSurfaceMixin {
	@Shadow
	private int swapchainWidth;

	@Shadow
	private int swapchainHeight;

	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lorg/lwjgl/glfw/GLFWVulkan;glfwCreateWindowSurface(Lorg/lwjgl/vulkan/VkInstance;JLorg/lwjgl/vulkan/VkAllocationCallbacks;Ljava/nio/LongBuffer;)I"
			)
	)
	private static int sdlCreateSurface(VkInstance instance, long window, VkAllocationCallbacks allocator, LongBuffer surface) {
		if (!SDLVulkan.SDL_Vulkan_CreateSurface(window, instance, allocator, surface)) {
			throw new IllegalStateException(Objects.requireNonNull(SDLError.SDL_GetError()));
		}
		return 0;
	}

	@Definition(id = "srcOffsets", local = @Local(type = VkOffset3D.Buffer.class, name = "srcOffsets"))
	@Definition(id = "x", method = "Lorg/lwjgl/vulkan/VkOffset3D$Buffer;x(I)Lorg/lwjgl/vulkan/VkOffset3D$Buffer;")
	@Definition(id = "y", method = "Lorg/lwjgl/vulkan/VkOffset3D$Buffer;y(I)Lorg/lwjgl/vulkan/VkOffset3D$Buffer;")
	@Definition(id = "z", method = "Lorg/lwjgl/vulkan/VkOffset3D$Buffer;z(I)Lorg/lwjgl/vulkan/VkOffset3D$Buffer;")
	@Definition(id = "copyWidth", local = @Local(type = int.class, name = "copyWidth"))
	@Definition(id = "copyHeight", local = @Local(type = int.class, name = "copyHeight"))
	@Expression("srcOffsets.x(@(copyWidth)).y(copyHeight).z(1)")
	@ModifyExpressionValue(method = "blitFromTexture", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int useSourceWidth(int original, @Local(argsOnly = true, name = "textureView") GpuTextureView textureView) {
		return textureView.getWidth(0);
	}

	@Definition(id = "srcOffsets", local = @Local(type = VkOffset3D.Buffer.class, name = "srcOffsets"))
	@Definition(id = "x", method = "Lorg/lwjgl/vulkan/VkOffset3D$Buffer;x(I)Lorg/lwjgl/vulkan/VkOffset3D$Buffer;")
	@Definition(id = "y", method = "Lorg/lwjgl/vulkan/VkOffset3D$Buffer;y(I)Lorg/lwjgl/vulkan/VkOffset3D$Buffer;")
	@Definition(id = "z", method = "Lorg/lwjgl/vulkan/VkOffset3D$Buffer;z(I)Lorg/lwjgl/vulkan/VkOffset3D$Buffer;")
	@Definition(id = "copyWidth", local = @Local(type = int.class, name = "copyWidth"))
	@Definition(id = "copyHeight", local = @Local(type = int.class, name = "copyHeight"))
	@Expression("srcOffsets.x(copyWidth).y(@(copyHeight)).z(1)")
	@ModifyExpressionValue(method = "blitFromTexture", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int useSourceHeight(int original, @Local(argsOnly = true, name = "textureView") GpuTextureView textureView) {
		return textureView.getHeight(0);
	}

	@Definition(id = "dstOffsets", local = @Local(type = VkOffset3D.Buffer.class, name = "dstOffsets"))
	@Definition(id = "x", method = "Lorg/lwjgl/vulkan/VkOffset3D$Buffer;x(I)Lorg/lwjgl/vulkan/VkOffset3D$Buffer;")
	@Definition(id = "y", method = "Lorg/lwjgl/vulkan/VkOffset3D$Buffer;y(I)Lorg/lwjgl/vulkan/VkOffset3D$Buffer;")
	@Definition(id = "z", method = "Lorg/lwjgl/vulkan/VkOffset3D$Buffer;z(I)Lorg/lwjgl/vulkan/VkOffset3D$Buffer;")
	@Definition(id = "copyWidth", local = @Local(type = int.class, name = "copyWidth"))
	@Definition(id = "copyHeight", local = @Local(type = int.class, name = "copyHeight"))
	@Expression("dstOffsets.x(0).y(@(copyHeight)).z(0)")
	@ModifyExpressionValue(method = "blitFromTexture", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int useSwapchainHeight(int original) {
		return this.swapchainHeight;
	}

	@Definition(id = "dstOffsets", local = @Local(type = VkOffset3D.Buffer.class, name = "dstOffsets"))
	@Definition(id = "x", method = "Lorg/lwjgl/vulkan/VkOffset3D$Buffer;x(I)Lorg/lwjgl/vulkan/VkOffset3D$Buffer;")
	@Definition(id = "y", method = "Lorg/lwjgl/vulkan/VkOffset3D$Buffer;y(I)Lorg/lwjgl/vulkan/VkOffset3D$Buffer;")
	@Definition(id = "z", method = "Lorg/lwjgl/vulkan/VkOffset3D$Buffer;z(I)Lorg/lwjgl/vulkan/VkOffset3D$Buffer;")
	@Definition(id = "copyWidth", local = @Local(type = int.class, name = "copyWidth"))
	@Definition(id = "copyHeight", local = @Local(type = int.class, name = "copyHeight"))
	@Expression("dstOffsets.x(@(copyWidth)).y(0).z(1)")
	@ModifyExpressionValue(method = "blitFromTexture", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int useSwapchainWidth(int original) {
		return this.swapchainWidth;
	}
}
