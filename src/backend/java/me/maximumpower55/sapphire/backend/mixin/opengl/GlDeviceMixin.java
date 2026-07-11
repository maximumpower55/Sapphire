package me.maximumpower55.sapphire.backend.mixin.opengl;

import static org.lwjgl.sdl.SDLVideo.SDL_GL_CreateContext;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_MakeCurrent;

import java.lang.foreign.MemorySegment;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.renderpearl.backend.opengl.GlDevice;

@Mixin(GlDevice.class)
public class GlDeviceMixin {
	@Unique
	@Nullable
	private MemorySegment window;

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwMakeContextCurrent(J)V"))
	private static void createSdlGlContext(long windowHandle) {
		long contextHandle = SDL_GL_CreateContext(windowHandle);
		SDL_GL_MakeCurrent(windowHandle, contextHandle);
	}
}
