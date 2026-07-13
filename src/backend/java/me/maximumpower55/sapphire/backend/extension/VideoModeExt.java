package me.maximumpower55.sapphire.backend.extension;

import java.nio.IntBuffer;

import org.lwjgl.sdl.SDLPixels;
import org.lwjgl.sdl.SDL_DisplayMode;
import org.lwjgl.system.MemoryStack;

import com.mojang.blaze3d.platform.VideoMode;

import me.maximumpower55.sapphire.backend.SDLHelper;

public interface VideoModeExt {
	static VideoMode create(SDL_DisplayMode displayMode) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			float scale = displayMode.pixel_density();
			float refreshRate = displayMode.refresh_rate();

			IntBuffer bpp = stack.mallocInt(1);
			IntBuffer Rmask = stack.mallocInt(1);
			IntBuffer Gmask = stack.mallocInt(1);
			IntBuffer Bmask = stack.mallocInt(1);
			IntBuffer Amask = stack.mallocInt(1);
			SDLPixels.SDL_GetMasksForPixelFormat(displayMode.format(), bpp, Rmask, Gmask, Bmask, Amask);

			VideoMode videoMode = new VideoMode(
					SDLHelper.scalePixel(displayMode.w(), scale),
					SDLHelper.scalePixel(displayMode.h(), scale),
					Integer.bitCount(Rmask.get()),
					Integer.bitCount(Gmask.get()),
					Integer.bitCount(Bmask.get()),
					(int) displayMode.refresh_rate()
			);
			videoMode.sapphire$refreshRate(refreshRate);

			return videoMode;
		}
	}

	default float sapphire$refreshRate() {
		throw new AssertionError("Implemented in Mixin");
	}

	default void sapphire$refreshRate(float refreshRate) {
		throw new AssertionError("Implemented in Mixin");
	}
}
