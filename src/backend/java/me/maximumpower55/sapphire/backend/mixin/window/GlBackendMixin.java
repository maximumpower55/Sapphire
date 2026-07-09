package me.maximumpower55.sapphire.backend.mixin.window;

import static org.lwjgl.sdl.SDLVideo.SDL_GL_CONTEXT_FLAGS;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_CONTEXT_FORWARD_COMPATIBLE_FLAG;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_CONTEXT_MAJOR_VERSION;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_CONTEXT_MINOR_VERSION;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_CONTEXT_PROFILE_CORE;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_CONTEXT_PROFILE_MASK;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_SetAttribute;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mojang.renderpearl.backend.opengl.GlBackend;

@Mixin(GlBackend.class)
public class GlBackendMixin {
	/**
	 * @author Maximum
	 * @reason SDL
	 */
	@Overwrite
	public void setWindowHints() {
		SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, 3);
		SDL_GL_SetAttribute(SDL_GL_CONTEXT_MINOR_VERSION, 3);
		SDL_GL_SetAttribute(SDL_GL_CONTEXT_PROFILE_MASK, SDL_GL_CONTEXT_PROFILE_CORE);
		SDL_GL_SetAttribute(SDL_GL_CONTEXT_FLAGS, SDL_GL_CONTEXT_FORWARD_COMPATIBLE_FLAG);
	}
}
