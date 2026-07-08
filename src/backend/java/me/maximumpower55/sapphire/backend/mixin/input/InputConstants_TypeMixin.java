package me.maximumpower55.sapphire.backend.mixin.input;

import com.mojang.blaze3d.platform.InputConstants;

import me.maximumpower55.sapphire.backend.SDLHelper;

import static org.lwjgl.sdl.SDLKeyboard.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InputConstants.Type.class)
public class InputConstants_TypeMixin {
	@Redirect(method = "lambda$static$0", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwGetKeyName(II)Ljava/lang/String;"))
	private static String sdlGetKeyName(int key, int scancode) {
		return SDL_GetKeyName(SDLHelper.mapKeyToSdl(key));
	}

	@Redirect(method = "lambda$static$1", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwGetKeyName(II)Ljava/lang/String;"))
	private static String sdlGetScancodeName(int key, int scancode) {
		return SDL_GetScancodeName(scancode);
	}
}
