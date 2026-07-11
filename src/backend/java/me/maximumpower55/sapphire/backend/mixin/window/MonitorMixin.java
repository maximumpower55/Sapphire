package me.maximumpower55.sapphire.backend.mixin.window;

import static org.lwjgl.sdl.SDLVideo.SDL_GetDisplayBounds;
import static org.lwjgl.sdl.SDLVideo.SDL_GetDisplayName;
import static org.lwjgl.sdl.SDLVideo.SDL_GetFullscreenDisplayModes;

import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.sdl.SDL_DisplayMode;
import org.lwjgl.sdl.SDL_Rect;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;

import me.maximumpower55.sapphire.backend.extension.VideoModeExt;

@Mixin(Monitor.class)
public class MonitorMixin {
	@Shadow
	@Final
	private static Logger LOGGER;

	/**
	 * @author Maximum
	 * @reason SDL
	 */
	@Overwrite
	@Nullable
	public static Monitor tryCreate(long monitor) {
		int displayId = (int) monitor;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			String displayName = Objects.requireNonNull(SDL_GetDisplayName(displayId));
			ImmutableList.Builder<VideoMode> videoModes = ImmutableList.builder();
			PointerBuffer modes = SDL_GetFullscreenDisplayModes(displayId);
			if (modes == null) {
				LOGGER.warn("Failed to query video modes of monitor {}", displayName);
				return null;
			}

			for (int i = modes.limit() - 1; i >= 0; i--) {
				SDL_DisplayMode displayMode = SDL_DisplayMode.create(modes.get(i));
				VideoMode videoMode = VideoModeExt.create(displayMode);
				if (videoMode.getRedBits() >= 8 && videoMode.getGreenBits() >= 8 && videoMode.getBlueBits() >= 8) {
					videoModes.add(videoMode);
				}
			}

			SDL_Rect displayBounds = SDL_Rect.malloc(stack);
			if (SDL_GetDisplayBounds(displayId, displayBounds)) {
				// Note: Actual current display mode is broken on wayland, this should emulate the correct behavior
				SDL_DisplayMode currentDisplayMode = SDL_DisplayMode.create(modes.get(0));
				return new Monitor(
						displayName,
						monitor,
						videoModes.build(),
						VideoModeExt.create(currentDisplayMode),
						displayBounds.x(),
						displayBounds.y()
				);
			}

			LOGGER.warn("Failed to query current video mode of monitor {}", displayName);
		}

		return null;
	}
}
