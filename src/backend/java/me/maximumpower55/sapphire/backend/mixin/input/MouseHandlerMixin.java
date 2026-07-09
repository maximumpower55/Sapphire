package me.maximumpower55.sapphire.backend.mixin.input;

import org.lwjgl.sdl.SDLKeyboard;
import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.sdl.SDL_MouseButtonEvent;
import org.lwjgl.sdl.SDL_MouseMotionEvent;
import org.lwjgl.sdl.SDL_MouseWheelEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;

import me.maximumpower55.sapphire.backend.SDLHelper;
import me.maximumpower55.sapphire.backend.extension.MouseHandlerExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin implements MouseHandlerExt {
	@Shadow
	protected abstract void onButton(long handle, MouseButtonInfo rawButtonInfo, @MouseButtonInfo.Action int action);

	@Shadow
	protected abstract void onScroll(long handle, double xoffset, double yoffset);

	@Shadow
	private boolean ignoreFirstMove;

	@Shadow
	private double xpos;

	@Shadow
	private double ypos;

	@Shadow
	private double accumulatedDX;

	@Shadow
	private double accumulatedDY;

	@Shadow
	@Final
	private Minecraft minecraft;

	@Overwrite
	public void setup(Window window) {
	}

	// Note: reimplement movement handling to handle relative movement
	@Override
	public void sapphire$onMotion(SDL_MouseMotionEvent event) {
		if (this.minecraft.isWindowActive()) {
			this.accumulatedDX += event.xrel();
			this.accumulatedDY += event.yrel();
		}

		this.xpos = event.x();
		this.ypos = event.y();
	}

	@Override
	public void sapphire$onButton(SDL_MouseButtonEvent event) {
		MouseButtonInfo buttonInfo = new MouseButtonInfo(
				SDLHelper.mapButtonToGlfw(event.button()),
				SDLHelper.mapModifiersToGlfw(SDLKeyboard.SDL_GetModState())
		);
		this.onButton(SDLVideo.SDL_GetWindowFromID(event.windowID()), buttonInfo, event.down() ? InputConstants.PRESS : InputConstants.RELEASE);
	}

	@Override
	public void sapphire$onWheel(SDL_MouseWheelEvent event) {
		this.onScroll(SDLVideo.SDL_GetWindowFromID(event.windowID()), event.x(), event.y());
	}
}
