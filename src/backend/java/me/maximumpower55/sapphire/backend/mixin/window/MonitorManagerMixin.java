package me.maximumpower55.sapphire.backend.mixin.window;

import static org.lwjgl.sdl.SDLVideo.SDL_GetDisplayForWindow;
import static org.lwjgl.sdl.SDLVideo.SDL_GetDisplays;
import static org.lwjgl.sdl.SDLVideo.SDL_GetPrimaryDisplay;

import java.nio.IntBuffer;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.MonitorManager;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

// TODO: Handle connecting and disconnecting
@Mixin(MonitorManager.class)
public class MonitorManagerMixin {
	@Shadow
	@Final
	private Long2ObjectMap<Monitor> monitors;

	@Inject(
			method = "<init>",
			at = @At(
					value = "FIELD",
					target = "Lcom/mojang/blaze3d/platform/MonitorManager;monitors:Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;",
					opcode = Opcodes.PUTFIELD,
					shift = At.Shift.AFTER
			)
	)
	private void sdlGetDisplays(CallbackInfo $, @Cancellable CallbackInfo ci) {
		// clearly inject head unconditional cancel
		ci.cancel();

		IntBuffer displays = SDL_GetDisplays();
		if (displays != null) {
			for (int i = 0; i < displays.limit(); i++) {
				Monitor monitor = Monitor.tryCreate(displays.get(i));
				if (monitor != null) {
					this.monitors.put(displays.get(i), monitor);
				}
			}
		}
	}

	@Redirect(method = "findBestMonitor", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwGetWindowMonitor(J)J"))
	private static long sdlGetWindowDisplay(long window) {
		return SDL_GetDisplayForWindow(window);
	}

	@Redirect(method = "findBestMonitor", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwGetPrimaryMonitor()J"))
	private static long sdlGetPrimaryDisplay() {
		return SDL_GetPrimaryDisplay();
	}

	/**
	 * @author Maximum
	 * @reason Noop
	 */
	@Overwrite
	public void close() {
	}
}
