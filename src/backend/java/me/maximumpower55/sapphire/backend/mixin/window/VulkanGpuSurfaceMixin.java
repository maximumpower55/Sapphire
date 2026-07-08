package me.maximumpower55.sapphire.backend.mixin.window;

import com.mojang.blaze3d.vulkan.VulkanGpuSurface;

import org.lwjgl.sdl.SDLError;
import org.lwjgl.sdl.SDLVulkan;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.LongBuffer;
import java.util.Objects;

@Mixin(VulkanGpuSurface.class)
public class VulkanGpuSurfaceMixin {
	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lorg/lwjgl/glfw/GLFWVulkan;glfwCreateWindowSurface(Lorg/lwjgl/vulkan/VkInstance;JLorg/lwjgl/vulkan/VkAllocationCallbacks;Ljava/nio/LongBuffer;)I"
			)
	)
	private int sdlCreateSurface(VkInstance instance, long window, VkAllocationCallbacks allocator, LongBuffer surface) {
		if (!SDLVulkan.SDL_Vulkan_CreateSurface(window, instance, allocator, surface)) {
			throw new IllegalStateException(Objects.requireNonNull(SDLError.SDL_GetError()));
		}
		return 0;
	}
}
