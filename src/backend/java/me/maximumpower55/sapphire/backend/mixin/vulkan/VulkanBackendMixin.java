package me.maximumpower55.sapphire.backend.mixin.vulkan;

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
					"createDevice(JLcom/mojang/renderpearl/api/device/GpuDebugOptions;)Lcom/mojang/renderpearl/api/device/GpuDevice;"
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
