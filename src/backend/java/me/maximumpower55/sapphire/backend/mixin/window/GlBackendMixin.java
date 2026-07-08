package me.maximumpower55.sapphire.backend.mixin.window;

import com.mojang.blaze3d.opengl.GlBackend;

import static org.lwjgl.sdl.SDLVideo.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

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
