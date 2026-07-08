package me.maximumpower55.sapphire.backend.mixin;

import java.util.function.LongSupplier;

import org.lwjgl.sdl.SDLInit;
import org.lwjgl.sdl.SDLTimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;

import me.maximumpower55.sapphire.backend.SapphireEventHandler;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
	@Redirect(method = "initBackendSystem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GLX;_initGlfw()Ljava/util/function/LongSupplier;"))
	private static LongSupplier sapphireInit() {
		SDLInit.SDL_Init(SDLInit.SDL_INIT_EVENTS | SDLInit.SDL_INIT_VIDEO);
		GLX._initGlfw(); // TODO: Just init glfw for now
		return SDLTimer::SDL_GetTicksNS;
	}

	@Redirect(method = "pollEvents", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwPollEvents()V"))
	private static void sapphirePollEvents() {
		SapphireEventHandler.pollEvents();
	}
}
