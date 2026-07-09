package me.maximumpower55.sapphire.backend;

import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_KEY_DOWN;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_KEY_UP;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_MOUSE_BUTTON_DOWN;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_MOUSE_BUTTON_UP;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_MOUSE_MOTION;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_MOUSE_WHEEL;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_TEXT_INPUT;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_FIRST;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_LAST;
import static org.lwjgl.sdl.SDLEvents.SDL_GetWindowFromEvent;
import static org.lwjgl.sdl.SDLEvents.SDL_PollEvent;

import java.util.Map;

import org.lwjgl.sdl.SDL_Event;

import com.google.common.collect.MapMaker;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;

// TODO: plumb to main class
public class SapphireEventHandler {
	private static final SDL_Event event = SDL_Event.malloc();

	public static final Map<Long, Window> windows = new MapMaker().weakValues().makeMap();

	public static void pollEvents() {
		while (SDL_PollEvent(event)) {
			if (event.type() >= SDL_EVENT_WINDOW_FIRST && event.type() <= SDL_EVENT_WINDOW_LAST) {
				windows.get(SDL_GetWindowFromEvent(event)).sapphire$onEvent(event.window());
				continue;
			}

			switch (event.type()) {
				case SDL_EVENT_MOUSE_MOTION -> {
					Minecraft.getInstance().mouseHandler.sapphire$onMotion(event.motion());
				}
				case SDL_EVENT_MOUSE_BUTTON_DOWN, SDL_EVENT_MOUSE_BUTTON_UP -> {
					Minecraft.getInstance().mouseHandler.sapphire$onButton(event.button());
				}
				case SDL_EVENT_MOUSE_WHEEL -> {
					Minecraft.getInstance().mouseHandler.sapphire$onWheel(event.wheel());
				}
				case SDL_EVENT_KEY_DOWN, SDL_EVENT_KEY_UP -> {
					Minecraft.getInstance().keyboardHandler.sapphire$onKey(event.key());
				}
				case SDL_EVENT_TEXT_INPUT -> {
					Minecraft.getInstance().keyboardHandler.sapphire$onText(event.text());
				}
			}
		}
	}
}
