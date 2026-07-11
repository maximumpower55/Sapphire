package me.maximumpower55.sapphire.backend.mixin.input;

import static org.lwjgl.sdl.SDLMouse.SDL_CreateSystemCursor;
import static org.lwjgl.sdl.SDLMouse.SDL_GetDefaultCursor;
import static org.lwjgl.sdl.SDLMouse.SDL_SetCursor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.platform.cursor.CursorType;

@Mixin(CursorType.class)
public class CursorTypeMixin {
	@ModifyConstant(method = "<clinit>", constant = @Constant(longValue = 0))
	private static long sdlDefaultCursor(long constant) {
		return SDL_GetDefaultCursor();
	}

	@Redirect(method = "select", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSetCursor(JJ)V"))
	private static void sdlSetCursor(long window, long cursor) {
		SDL_SetCursor(cursor);
	}

	@Redirect(method = "createStandardCursor", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwCreateStandardCursor(I)J"))
	private static long sdlCreateSystemCursor(int shape) {
		return SDL_CreateSystemCursor(shape);
	}
}
