package me.maximumpower55.sapphire.backend.mixin.window;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.renderpearl.backend.vulkan.VulkanBackend;

@Mixin(VulkanBackend.class)
public class VulkanBackendMixin {
	/**
	 * @author Maximum
	 * @reason SDL
	 */
	@Overwrite
	public void setWindowHints() {
	}

	@Redirect(
			method = {
					"checkBackendAvailable",
					"createDevice(JLcom/mojang/blaze3d/shaders/GpuDebugOptions;)Lcom/mojang/blaze3d/systems/GpuDevice;"
			},
			at = @At(
					value = "INVOKE",
					target = "Lorg/lwjgl/glfw/GLFWVulkan;glfwVulkanSupported()Z"
			)
	)
	private static boolean vulkanIsEvenOnTheMoon() {
		return true;
	}
}
