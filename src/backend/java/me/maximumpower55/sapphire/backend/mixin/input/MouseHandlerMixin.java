package me.maximumpower55.sapphire.backend.mixin.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;

import me.maximumpower55.sapphire.backend.SDLHelper;
import me.maximumpower55.sapphire.backend.SapphireEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

import net.minecraft.client.input.MouseButtonInfo;

import org.lwjgl.sdl.SDLKeyboard;
import org.lwjgl.sdl.SDLKeycode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MouseHandler.class)
@Implements(@Interface(iface = SapphireEventHandler.MouseCallbacks.class, prefix = "sapphire$"))
public abstract class MouseHandlerMixin {
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
		SapphireEventHandler.mouseCallbacks = (SapphireEventHandler.MouseCallbacks) this;
	}

	// Note: reimplement movement handling to handle relative movement
	public void sapphire$onMotion(long windowHandle, float x, float y, float xrel, float yrel) {
		if (this.ignoreFirstMove) {
			this.xpos = x;
			this.ypos = y;
			this.ignoreFirstMove = false;
		} else {
			if (this.minecraft.isWindowActive()) {
				this.accumulatedDX += xrel;
				this.accumulatedDY += yrel;
			}

			this.xpos = x;
			this.ypos = y;
		}
	}

	public void sapphire$onButton(long windowHandle, byte button, boolean down) {
		MouseButtonInfo buttonInfo = new MouseButtonInfo(SDLHelper.mapButtonToGlfw(button), SDLHelper.mapModifiersToGlfw(SDLKeyboard.SDL_GetModState()));
		this.onButton(windowHandle, buttonInfo, down ? InputConstants.PRESS : InputConstants.RELEASE);
	}

	public void sapphire$onWheel(long windowHandle, float x, float y) {
		this.onScroll(windowHandle, x, y);
	}
}
