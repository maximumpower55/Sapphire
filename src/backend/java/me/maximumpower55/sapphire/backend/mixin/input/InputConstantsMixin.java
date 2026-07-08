package me.maximumpower55.sapphire.backend.mixin.input;

import com.mojang.blaze3d.platform.InputConstants;

import com.mojang.blaze3d.platform.Window;

import static org.lwjgl.sdl.SDLHints.*;
import static org.lwjgl.sdl.SDLKeyboard.*;
import static org.lwjgl.sdl.SDLMouse.*;

import me.maximumpower55.sapphire.backend.SDLHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.ByteBuffer;

@Mixin(InputConstants.class)
public class InputConstantsMixin {
	@Redirect(method = "isKeyDown", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwGetKey(JI)I"))
	private static int sdlGetKeyState(long window, int key) {
		ByteBuffer keyboardState = SDL_GetKeyboardState();
		if (keyboardState != null) {
			int scancode = SDL_GetScancodeFromKey(SDLHelper.mapKeyToSdl(key), null);
			return keyboardState.get(scancode);
		}
		return 0;
	}

	/**
	 * @author Maximum
	 * @reason SDL
	 */
	@Overwrite
	public static void grabOrReleaseMouse(Window window, int cursorMode, double xpos, double ypos) {
		SDL_WarpMouseInWindow(window.handle(), (float) xpos, (float) ypos);
		SDL_SetWindowRelativeMouseMode(window.handle(), cursorMode == InputConstants.CURSOR_DISABLED);
	}

	@Redirect(method = "isRawMouseInputSupported", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwRawMouseMotionSupported()Z"))
	private static boolean alwaysSupportRawInput() {
		return true;
	}

	/**
	 * @author Maximum
	 * @reason SDL
	 */
	@Overwrite
	public static void updateRawMouseInput(Window window, boolean value) {
		SDL_SetHint(SDL_HINT_MOUSE_RELATIVE_SYSTEM_SCALE, value ? "0" : "1");
	}
}
