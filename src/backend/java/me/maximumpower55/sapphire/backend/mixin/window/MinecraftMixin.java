package me.maximumpower55.sapphire.backend.mixin.window;

import net.minecraft.client.Minecraft;

import static org.lwjgl.sdl.SDLVideo.*;

import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.IntBuffer;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSetWindowSizeLimits(JIIII)V"))
	private void h(long window, int minwidth, int minheight, int maxwidth, int maxheight) {
		SDL_SetWindowMinimumSize(window, minwidth, minheight);
		SDL_SetWindowMaximumSize(window, maxwidth, maxheight);
	}

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwShowWindow(J)V"))
	private void sdlShowWindow(long window) {
		SDL_ShowWindow(window);
	}

	@Redirect(method = "renderFrame", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwGetFramebufferSize(J[I[I)V"))
	private void sdlGetWindowSizeInPixels(long window, int[] width, int[] height) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			if (SDL_GetWindowSizeInPixels(window, w, h)) {
				width[0] = w.get();
				height[0] = h.get();
			}
		}
	}
}
