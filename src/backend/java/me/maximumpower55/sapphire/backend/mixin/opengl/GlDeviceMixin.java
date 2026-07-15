package me.maximumpower55.sapphire.backend.mixin.opengl;

import static org.lwjgl.sdl.SDLVideo.SDL_GL_CreateContext;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_MakeCurrent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.mojang.renderpearl.backend.opengl.GlDevice")
public class GlDeviceMixin {
	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwMakeContextCurrent(J)V"))
	private static void createSdlGlContext(long windowHandle) {
		long contextHandle = SDL_GL_CreateContext(windowHandle);
		SDL_GL_MakeCurrent(windowHandle, contextHandle);
	}
}
