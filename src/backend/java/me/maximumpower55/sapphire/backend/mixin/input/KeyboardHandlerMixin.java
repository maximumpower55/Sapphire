package me.maximumpower55.sapphire.backend.mixin.input;

import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.sdl.SDL_KeyboardEvent;
import org.lwjgl.sdl.SDL_TextInputEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;

import me.maximumpower55.sapphire.backend.SDLHelper;
import me.maximumpower55.sapphire.backend.extension.KeyboardHandlerExt;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin implements KeyboardHandlerExt {
	@Shadow
	protected abstract void keyPress(long handle, @KeyEvent.Action int action, KeyEvent event);

	@Shadow
	protected abstract void charTyped(long handle, CharacterEvent event);

	@Overwrite
	public void setup(Window window) {
	}

	@Override
	public void sapphire$onKey(SDL_KeyboardEvent sdlEvent) {
		int action;
		if (sdlEvent.repeat()) {
			action = InputConstants.REPEAT;
		} else {
			action = sdlEvent.down() ? InputConstants.PRESS : InputConstants.RELEASE;
		}

		KeyEvent event = new KeyEvent(
				SDLHelper.mapKeyToGlfw(sdlEvent.key()),
				sdlEvent.scancode(),
				SDLHelper.mapModifiersToGlfw(sdlEvent.mod())
		);
		this.keyPress(SDLVideo.SDL_GetWindowFromID(sdlEvent.windowID()), action, event);
	}

	@Override
	public void sapphire$onText(SDL_TextInputEvent event) {
		String text = event.textString();
		if (text != null) {
			long windowHandle = SDLVideo.SDL_GetWindowFromID(event.windowID());
			text.codePoints().forEach(codePoint -> {
				CharacterEvent characterEvent = new CharacterEvent(codePoint);
				this.charTyped(windowHandle, characterEvent);
			});
		}
	}
}
