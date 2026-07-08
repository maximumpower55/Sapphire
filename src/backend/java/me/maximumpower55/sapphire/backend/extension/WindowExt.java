package me.maximumpower55.sapphire.backend.extension;

import org.lwjgl.sdl.SDL_WindowEvent;

public interface WindowExt {
	default void sapphire$handleEvent(SDL_WindowEvent event) {
		throw new AssertionError("Implemented in Mixin");
	}
}
