package me.maximumpower55.sapphire.backend;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.function.Function;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.sdl.SDLInit;
import org.lwjgl.sdl.SDLKeycode;
import org.lwjgl.sdl.SDLMouse;
import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.sdl.SDLVulkan;

import com.mojang.blaze3d.platform.NativeLibrariesBootstrap;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.SharedConstants;

public final class SDLHelper {
	// Sourced from VulkanInstance
	public static final String APPLICATION_NAME = "Minecraft Java Edition";
	public static final String APPLICATION_VERSION = SharedConstants.getCurrentVersion().name();
	public static final String APPLICATION_IDENTIFIER = "com.mojang.minecraftje"; // com.mojang.minecraft is used by the official launcher

	private SDLHelper() {
	}

	public static void init() {
		SDLInit.SDL_SetAppMetadata(APPLICATION_NAME, APPLICATION_VERSION, APPLICATION_IDENTIFIER);
		SDLInit.SDL_Init(SDLInit.SDL_INIT_EVENTS | SDLInit.SDL_INIT_VIDEO);

		// Note: GLFW implicitly loads libraries, it is never supplied with libraries by the game
		SDLVideo.SDL_GL_LoadLibrary((ByteBuffer) null);
		if (NativeLibrariesBootstrap.isVulkanLoaderAvailable()) {
			SDLVulkan.SDL_Vulkan_LoadLibrary((ByteBuffer) null);
		}
	}

	public static int scalePixel(int pixel, float scale) {
		return Math.round(pixel * scale);
	}

	private static final Pair<Int2IntMap, Int2IntMap> KEY_MAPPING = createGlfwMapping(SDLKeycode.class, "SDLK_", "GLFW_KEY_", name -> name
			.replace("LEFT_", "L")
			.replace("RIGHT_", "R")
			.replace("ENTER", "RETURN")
			.replace("CONTROL", "CTRL")
			.replace("SUPER", "GUI")
	);
	private static final Pair<Int2IntMap, Int2IntMap> BUTTON_MAPPING = createGlfwMapping(SDLMouse.class, "SDL_BUTTON_", "GLFW_MOUSE_BUTTON_", Function.identity());

	private static Pair<Int2IntMap, Int2IntMap> createGlfwMapping(Class<?> sdlClass, String sdlPrefix, String glfwPrefix, Function<String, String> mapper) {
		Int2IntMap sdlToGlfwMap = new Int2IntOpenHashMap();
		Int2IntMap glfwToSdlMap = new Int2IntOpenHashMap();

		for (Field field : GLFW.class.getFields()) {
			String name = field.getName();
			if (name.startsWith(glfwPrefix)) {
				String valueName = name.substring(glfwPrefix.length());

				Field sdlField;
				try {
					sdlField = sdlClass.getField(sdlPrefix + mapper.apply(valueName));
				} catch (NoSuchFieldException _) {
					System.out.println(valueName);
					continue;
				}

				try {
					int sdlValue = sdlField.getInt(null);
					int glfwValue = field.getInt(null);
					sdlToGlfwMap.put(sdlValue, glfwValue);
					glfwToSdlMap.put(glfwValue, sdlValue);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return Pair.of(Int2IntMaps.unmodifiable(sdlToGlfwMap), Int2IntMaps.unmodifiable(glfwToSdlMap));
	}

	public static int mapModifiersToGlfw(short modifiers) {
		int glfwModifiers = 0;

		if ((modifiers & SDLKeycode.SDL_KMOD_SHIFT) > 0) {
			glfwModifiers |= GLFW.GLFW_MOD_SHIFT;
		}

		if ((modifiers & SDLKeycode.SDL_KMOD_CTRL) > 0) {
			glfwModifiers |= GLFW.GLFW_MOD_SHIFT;
		}

		if ((modifiers & SDLKeycode.SDL_KMOD_ALT) > 0) {
			glfwModifiers |= GLFW.GLFW_MOD_ALT;
		}

		if ((modifiers & SDLKeycode.SDL_KMOD_GUI) > 0) {
			glfwModifiers |= GLFW.GLFW_MOD_SUPER;
		}

		if ((modifiers & SDLKeycode.SDL_KMOD_CAPS) > 0) {
			glfwModifiers |= GLFW.GLFW_MOD_CAPS_LOCK;
		}

		if ((modifiers & SDLKeycode.SDL_KMOD_NUM) > 0) {
			glfwModifiers |= GLFW.GLFW_MOD_NUM_LOCK;
		}

		return glfwModifiers;
	}

	public static int mapKeyToGlfw(int key) {
		return KEY_MAPPING.left().getOrDefault(key, GLFW.GLFW_KEY_UNKNOWN);
	}

	public static int mapKeyToSdl(int key) {
		return KEY_MAPPING.right().getOrDefault(key, SDLKeycode.SDLK_UNKNOWN);
	}

	public static int mapButtonToGlfw(int key) {
		return BUTTON_MAPPING.left().get(key);
	}

	public static int mapButtonToSdl(int key) {
		return BUTTON_MAPPING.right().get(key);
	}
}
