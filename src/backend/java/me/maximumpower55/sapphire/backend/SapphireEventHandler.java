package me.maximumpower55.sapphire.backend;

import com.google.common.collect.MapMaker;

import static org.lwjgl.sdl.SDLEvents.*;

import com.mojang.blaze3d.platform.Window;

import org.jspecify.annotations.Nullable;
import org.lwjgl.sdl.SDL_Event;
import org.lwjgl.sdl.SDL_KeyboardEvent;
import org.lwjgl.sdl.SDL_MouseButtonEvent;
import org.lwjgl.sdl.SDL_MouseMotionEvent;
import org.lwjgl.sdl.SDL_MouseWheelEvent;
import org.lwjgl.sdl.SDL_TextInputEvent;

import java.util.Map;
import java.util.Objects;

// TODO: plumb to main class
public class SapphireEventHandler {
	private static final SDL_Event event = SDL_Event.malloc();

	public static final Map<Long, Window> windows = new MapMaker().weakValues().makeMap();
	@Nullable
	public static MouseCallbacks mouseCallbacks;
	@Nullable
	public static KeyboardCallbacks keyboardCallbacks;

	public static void pollEvents() {
		while (SDL_PollEvent(event)) {
			long windowHandle = SDL_GetWindowFromEvent(event);
			if (event.type() >= SDL_EVENT_WINDOW_FIRST && event.type() <= SDL_EVENT_WINDOW_LAST) {
				windows.get(windowHandle).sapphire$handleEvent(event.window());
				continue;
			}

			switch (event.type()) {
				case SDL_EVENT_MOUSE_MOTION -> {
					if (mouseCallbacks != null) {
						SDL_MouseMotionEvent motionEvent = event.motion();
						mouseCallbacks.onMotion(windowHandle, motionEvent.x(), motionEvent.y(), motionEvent.xrel(), motionEvent.yrel());
					}
				}
				case SDL_EVENT_MOUSE_BUTTON_DOWN, SDL_EVENT_MOUSE_BUTTON_UP -> {
					if (mouseCallbacks != null) {
						SDL_MouseButtonEvent buttonEvent = event.button();
						mouseCallbacks.onButton(windowHandle, buttonEvent.button(), buttonEvent.down());
					}
				}
				case SDL_EVENT_MOUSE_WHEEL -> {
					if (mouseCallbacks != null) {
						SDL_MouseWheelEvent wheelEvent = event.wheel();
						mouseCallbacks.onWheel(windowHandle, wheelEvent.x(), wheelEvent.y());
					}
				}
				case SDL_EVENT_KEY_DOWN, SDL_EVENT_KEY_UP -> {
					if (keyboardCallbacks != null) {
						SDL_KeyboardEvent keyEvent = event.key();
						keyboardCallbacks.onKey(windowHandle, keyEvent.scancode(), keyEvent.key(), keyEvent.mod(), keyEvent.down(), keyEvent.repeat());
					}
				}
				case SDL_EVENT_TEXT_INPUT -> {
					if (keyboardCallbacks != null) {
						SDL_TextInputEvent textEvent = event.text();
						keyboardCallbacks.onText(windowHandle, Objects.requireNonNull(textEvent.textString()));
					}
				}
			}
		}
	}

	public interface MouseCallbacks {
		void onMotion(long windowHandle, float x, float y, float xrel, float yrel);

		void onButton(long windowHandle, byte button, boolean down);

		void onWheel(long windowHandle, float x, float y);
	}

	public interface KeyboardCallbacks {
		void onKey(long windowHandle, int scancode, int key, short mod, boolean down, boolean repeat);

		void onText(long windowHandle, String text);
	}
}
