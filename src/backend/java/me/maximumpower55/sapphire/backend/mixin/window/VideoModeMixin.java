package me.maximumpower55.sapphire.backend.mixin.window;

import org.lwjgl.sdl.SDL_DisplayMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.mojang.blaze3d.platform.VideoMode;

import me.maximumpower55.sapphire.backend.extension.VideoModeExt;

@Mixin(VideoMode.class)
public class VideoModeMixin implements VideoModeExt {
	@SuppressWarnings("NotNullFieldNotInitialized")
	@Unique
	private SDL_DisplayMode displayMode;

	@Override
	public SDL_DisplayMode sapphire$displayMode() {
		return this.displayMode;
	}

	@Override
	public void sapphire$displayMode(SDL_DisplayMode displayMode) {
		this.displayMode = displayMode;
	}
}
