package me.maximumpower55.sapphire.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;

@Mixin(Options.class)
public class OptionsMixin {
	@Definition(id = "process", method = "Lnet/minecraft/client/Options$OptionAccess;process(Ljava/lang/String;Lnet/minecraft/client/OptionInstance;)V")
	@Definition(id = "exclusiveFullscreen", field = "Lnet/minecraft/client/Options;exclusiveFullscreen:Lnet/minecraft/client/OptionInstance;")
	@Expression("?.process(?, this.exclusiveFullscreen)")
	@Redirect(method = "processDumpedOptions", at = @At("MIXINEXTRAS:EXPRESSION"))
	private void dontProcessExclusiveFullscreen(@Coerce Object instance, String s, OptionInstance<?> tOptionInstance) {
	}
}
