package me.maximumpower55.sapphire.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.VideoMode;

import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.MutableComponent;

@Mixin(VideoSettingsScreen.class)
public class VideoSettingsScreenMixin {
	@WrapOperation(
			method = "lambda$addOptions$0",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"
			)
	)
	private static MutableComponent expandRefreshRate(String key, Object[] args, Operation<MutableComponent> original, @Local(name = "mode") VideoMode mode) {
		//noinspection resource
		args[2] = mode.sapphire$displayMode().refresh_rate();
		return original.call(key, args);
	}
}
