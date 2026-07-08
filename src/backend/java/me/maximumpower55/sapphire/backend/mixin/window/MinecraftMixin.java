package me.maximumpower55.sapphire.backend.mixin.window;

import static org.lwjgl.sdl.SDLVideo.SDL_GetWindowSizeInPixels;
import static org.lwjgl.sdl.SDLVideo.SDL_SetWindowMaximumSize;
import static org.lwjgl.sdl.SDLVideo.SDL_SetWindowMinimumSize;
import static org.lwjgl.sdl.SDLVideo.SDL_ShowWindow;

import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSetWindowSizeLimits(JIIII)V"))
	private static void sdlSetWindowSizeLimits(long window, int minwidth, int minheight, int maxwidth, int maxheight) {
		SDL_SetWindowMinimumSize(window, minwidth, minheight);
		SDL_SetWindowMaximumSize(window, maxwidth, maxheight);
	}

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwShowWindow(J)V"))
	private static void sdlShowWindow(long window) {
		SDL_ShowWindow(window);
	}

	@Redirect(method = "renderFrame", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwGetFramebufferSize(J[I[I)V"))
	private static void sdlGetWindowSizeInPixels(long window, int[] width, int[] height) {
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
