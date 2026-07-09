package me.maximumpower55.sapphire.backend.mixin.window;

import org.lwjgl.sdl.SDLVulkan;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.renderpearl.backend.vulkan.VulkanPhysicalDevice;

@Mixin(VulkanPhysicalDevice.class)
public class VulkanPhysicalDeviceMixin {
	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFWVulkan;glfwGetPhysicalDevicePresentationSupport(Lorg/lwjgl/vulkan/VkInstance;Lorg/lwjgl/vulkan/VkPhysicalDevice;I)Z"))
	private static boolean sdlGetPresentationSupport(VkInstance instance, VkPhysicalDevice device, int queuefamily) {
		return SDLVulkan.SDL_Vulkan_GetPresentationSupport(instance, device, queuefamily);
	}
}
