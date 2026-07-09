package me.maximumpower55.sapphire.backend.mixin.window;

import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.sdl.SDLVulkan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.renderpearl.backend.vulkan.VulkanInstance;

@Mixin(VulkanInstance.class)
public class VulkanInstanceMixin {
	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFWVulkan;glfwGetRequiredInstanceExtensions()Lorg/lwjgl/PointerBuffer;"))
	@Nullable
	private static PointerBuffer sdlGetInstanceExtensions() {
		return SDLVulkan.SDL_Vulkan_GetInstanceExtensions();
	}
}
