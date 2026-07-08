package me.maximumpower55.sapphire.backend.mixin.window;

import com.mojang.blaze3d.opengl.GlSurface;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static org.lwjgl.sdl.SDLVideo.SDL_GL_SetSwapInterval;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_SwapWindow;

@Mixin(GlSurface.class)
public class GlSurfaceMixin {
	@Redirect(method = "configure", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSwapInterval(I)V"))
	private void sdlSetSwapInterval(int interval) {
		SDL_GL_SetSwapInterval(interval);
	}

	@Redirect(method = "present", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSwapBuffers(J)V"))
	private void sdlSwapWindow(long windowHandle) {
		SDL_GL_SwapWindow(windowHandle);
	}
}
