package me.maximumpower55.sapphire.backend.mixin.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;

import me.maximumpower55.sapphire.backend.SDLHelper;
import me.maximumpower55.sapphire.backend.SapphireEventHandler;
import net.minecraft.client.KeyboardHandler;

import net.minecraft.client.input.CharacterEvent;

import net.minecraft.client.input.KeyEvent;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyboardHandler.class)
@Implements(@Interface(iface = SapphireEventHandler.KeyboardCallbacks.class, prefix = "sapphire$"))
public abstract class KeyboardHandlerMixin {
	@Shadow
	protected abstract void keyPress(long handle, @KeyEvent.Action int action, KeyEvent event);

	@Shadow
	protected abstract void charTyped(long handle, CharacterEvent event);

	@Overwrite
	public void setup(Window window) {
		SapphireEventHandler.keyboardCallbacks = (SapphireEventHandler.KeyboardCallbacks) this;
	}

	public void sapphire$onKey(long windowHandle, int scancode, int key, short mod, boolean down, boolean repeat) {
		int action;
		if (repeat) {
			action = InputConstants.REPEAT;
		} else {
			action = down ? InputConstants.PRESS : InputConstants.RELEASE;
		}

		KeyEvent event = new KeyEvent(SDLHelper.mapKeyToGlfw(key), scancode, SDLHelper.mapModifiersToGlfw(mod));
		this.keyPress(windowHandle, action, event);
	}

	public void sapphire$onText(long windowHandle, String text) {
		text.codePoints().forEach(codePoint -> {
			CharacterEvent event = new CharacterEvent(codePoint);
			this.charTyped(windowHandle, event);
		});
	}
}
