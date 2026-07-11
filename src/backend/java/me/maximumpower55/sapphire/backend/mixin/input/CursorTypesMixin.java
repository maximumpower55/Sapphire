package me.maximumpower55.sapphire.backend.mixin.input;

import static org.lwjgl.sdl.SDLMouse.SDL_SYSTEM_CURSOR_CROSSHAIR;
import static org.lwjgl.sdl.SDLMouse.SDL_SYSTEM_CURSOR_DEFAULT;
import static org.lwjgl.sdl.SDLMouse.SDL_SYSTEM_CURSOR_EW_RESIZE;
import static org.lwjgl.sdl.SDLMouse.SDL_SYSTEM_CURSOR_MOVE;
import static org.lwjgl.sdl.SDLMouse.SDL_SYSTEM_CURSOR_NOT_ALLOWED;
import static org.lwjgl.sdl.SDLMouse.SDL_SYSTEM_CURSOR_NS_RESIZE;
import static org.lwjgl.sdl.SDLMouse.SDL_SYSTEM_CURSOR_POINTER;
import static org.lwjgl.sdl.SDLMouse.SDL_SYSTEM_CURSOR_TEXT;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.mojang.blaze3d.platform.cursor.CursorTypes;

@Mixin(CursorTypes.class)
public class CursorTypesMixin {
	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = GLFW.GLFW_ARROW_CURSOR))
	private static int sdlArrowCursor(int constant) {
		return SDL_SYSTEM_CURSOR_DEFAULT;
	}

	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = GLFW.GLFW_IBEAM_CURSOR))
	private static int sdlIBeamCursor(int constant) {
		return SDL_SYSTEM_CURSOR_TEXT;
	}

	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = GLFW.GLFW_CROSSHAIR_CURSOR))
	private static int sdlCrosshairCursor(int constant) {
		return SDL_SYSTEM_CURSOR_CROSSHAIR;
	}

	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = GLFW.GLFW_POINTING_HAND_CURSOR))
	private static int sdlPointingHandCursor(int constant) {
		return SDL_SYSTEM_CURSOR_POINTER;
	}

	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = GLFW.GLFW_RESIZE_NS_CURSOR))
	private static int sdlResizeNSCursor(int constant) {
		return SDL_SYSTEM_CURSOR_NS_RESIZE;
	}

	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = GLFW.GLFW_RESIZE_EW_CURSOR))
	private static int sdlResizeEWCursor(int constant) {
		return SDL_SYSTEM_CURSOR_EW_RESIZE;
	}

	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = GLFW.GLFW_RESIZE_ALL_CURSOR))
	private static int sdlResizeAllCursor(int constant) {
		return SDL_SYSTEM_CURSOR_MOVE;
	}

	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = GLFW.GLFW_NOT_ALLOWED_CURSOR))
	private static int sdlNotAllowedCursor(int constant) {
		return SDL_SYSTEM_CURSOR_NOT_ALLOWED;
	}
}
