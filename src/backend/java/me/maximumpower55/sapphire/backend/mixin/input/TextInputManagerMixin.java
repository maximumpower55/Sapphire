package me.maximumpower55.sapphire.backend.mixin.input;

import com.mojang.blaze3d.platform.TextInputManager;

import com.mojang.blaze3d.platform.Window;

import static org.lwjgl.sdl.SDLKeyboard.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: Handle IME
@Mixin(TextInputManager.class)
public class TextInputManagerMixin {
	@Shadow
	@Final
	private Window window;

	@Inject(method = "startTextInput", at = @At("TAIL"))
	private void sdlStartTextInput(CallbackInfo ci) {
		SDL_StartTextInput(this.window.handle());
	}

	@Inject(method = "stopTextInput", at = @At("TAIL"))
	private void sdlStopTextInput(CallbackInfo ci) {
		SDL_StopTextInput(this.window.handle());
	}
}
