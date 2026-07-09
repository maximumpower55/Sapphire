package me.maximumpower55.sapphire.backend.extension;

import org.lwjgl.sdl.SDL_MouseButtonEvent;
import org.lwjgl.sdl.SDL_MouseMotionEvent;
import org.lwjgl.sdl.SDL_MouseWheelEvent;

public interface MouseHandlerExt {
	default void sapphire$onMotion(SDL_MouseMotionEvent event) {
		throw new AssertionError("Implemented in Mixin");
	}

	default void sapphire$onButton(SDL_MouseButtonEvent event) {
		throw new AssertionError("Implemented in Mixin");
	}

	default void sapphire$onWheel(SDL_MouseWheelEvent event) {
		throw new AssertionError("Implemented in Mixin");
	}
}
