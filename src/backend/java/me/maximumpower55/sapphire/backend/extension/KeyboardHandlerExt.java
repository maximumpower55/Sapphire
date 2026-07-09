package me.maximumpower55.sapphire.backend.extension;

import org.lwjgl.sdl.SDL_KeyboardEvent;
import org.lwjgl.sdl.SDL_TextInputEvent;

public interface KeyboardHandlerExt {
	default void sapphire$onKey(SDL_KeyboardEvent event) {
		throw new AssertionError("Implemented in Mixin");
	}

	default void sapphire$onText(SDL_TextInputEvent event) {
		throw new AssertionError("Implemented in Mixin");
	}
}
