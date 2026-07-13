package me.maximumpower55.sapphire.backend.mixin.window;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.mojang.blaze3d.platform.VideoMode;

import me.maximumpower55.sapphire.backend.extension.VideoModeExt;

@Mixin(VideoMode.class)
public class VideoModeMixin implements VideoModeExt {
	@Unique
	private float refreshRate;

	@Override
	public float sapphire$refreshRate() {
		return this.refreshRate;
	}

	@Override
	public void sapphire$refreshRate(float refreshRate) {
		this.refreshRate = refreshRate;
	}
}
