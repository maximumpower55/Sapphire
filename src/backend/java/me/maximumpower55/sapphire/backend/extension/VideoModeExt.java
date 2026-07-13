package me.maximumpower55.sapphire.backend.extension;

import java.nio.IntBuffer;

import org.lwjgl.sdl.SDLPixels;
import org.lwjgl.sdl.SDL_DisplayMode;
import org.lwjgl.system.MemoryStack;

import com.mojang.blaze3d.platform.VideoMode;

public interface VideoModeExt {
	static VideoMode create(SDL_DisplayMode displayMode) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer bpp = stack.mallocInt(1);
			IntBuffer Rmask = stack.mallocInt(1);
			IntBuffer Gmask = stack.mallocInt(1);
			IntBuffer Bmask = stack.mallocInt(1);
			IntBuffer Amask = stack.mallocInt(1);
			SDLPixels.SDL_GetMasksForPixelFormat(displayMode.format(), bpp, Rmask, Gmask, Bmask, Amask);

			VideoMode videoMode = new VideoMode(
					displayMode.w(),
					displayMode.h(),
					Integer.bitCount(Rmask.get()),
					Integer.bitCount(Gmask.get()),
					Integer.bitCount(Bmask.get()),
					(int) displayMode.refresh_rate()
			);
			videoMode.sapphire$refreshRate(displayMode.refresh_rate());

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
