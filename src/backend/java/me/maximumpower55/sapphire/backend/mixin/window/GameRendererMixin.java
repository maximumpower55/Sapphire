package me.maximumpower55.sapphire.backend.mixin.window;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Redirect(method = "extractWindow", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;getWidth()I"))
	private static int getRenderWidth(Window instance) {
		return instance.sapphire$renderWidth();
	}

	@Redirect(method = "extractWindow", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;getHeight()I"))
	private static int getRenderHeight(Window instance) {
		return instance.sapphire$renderHeight();
	}
}
