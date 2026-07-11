package me.maximumpower55.sapphire.backend.mixin;

import org.lwjgl.sdl.SDLTimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.Blaze3D;

@Mixin(Blaze3D.class)
public class Blaze3DMixin {
	@Redirect(method = "getTime", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwGetTime()D"))
	private static double sdlGetTime() {
		return SDLTimer.SDL_GetTicks();
	}
}
