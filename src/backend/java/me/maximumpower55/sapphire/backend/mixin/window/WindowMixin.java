package me.maximumpower55.sapphire.backend.mixin.window;

import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_CLOSE_REQUESTED;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_FOCUS_GAINED;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_FOCUS_LOST;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_MOUSE_ENTER;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_MOUSE_LEAVE;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_MOVED;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_PIXEL_SIZE_CHANGED;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_RESIZED;
import static org.lwjgl.sdl.SDLProperties.SDL_CreateProperties;
import static org.lwjgl.sdl.SDLProperties.SDL_SetBooleanProperty;
import static org.lwjgl.sdl.SDLProperties.SDL_SetNumberProperty;
import static org.lwjgl.sdl.SDLProperties.SDL_SetStringProperty;
import static org.lwjgl.sdl.SDLVideo.SDL_CreateWindowWithProperties;
import static org.lwjgl.sdl.SDLVideo.SDL_DestroyWindow;
import static org.lwjgl.sdl.SDLVideo.SDL_GetWindowSizeInPixels;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_HEIGHT_NUMBER;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_HIDDEN_BOOLEAN;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_HIGH_PIXEL_DENSITY_BOOLEAN;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_OPENGL_BOOLEAN;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_RESIZABLE_BOOLEAN;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_TITLE_STRING;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_VULKAN_BOOLEAN;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_WIDTH_NUMBER;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_Y_NUMBER;
import static org.lwjgl.sdl.SDLVideo.SDL_SetWindowTitle;
import static org.lwjgl.sdl.SDLVideo.SDL_WINDOWPOS_CENTERED_DISPLAY;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorEnterCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallbackI;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallbackI;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowPosCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.sdl.SDLError;
import org.lwjgl.sdl.SDL_WindowEvent;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.opengl.GlBackend;
import com.mojang.blaze3d.platform.IconSet;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.BackendCreationException;
import com.mojang.blaze3d.systems.GpuBackend;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vulkan.VulkanBackend;

import me.maximumpower55.sapphire.backend.SapphireEventHandler;
import me.maximumpower55.sapphire.backend.extension.WindowExt;
import net.minecraft.server.packs.PackResources;

// TODO: Reimplement monitor and mode management
@Mixin(Window.class)
public abstract class WindowMixin implements WindowExt {
	@Shadow
	private int framebufferHeight;

	@Shadow
	private int framebufferWidth;

	@Shadow
	@Final
	private long handle;

	@Shadow
	protected abstract void onFramebufferResize(long handle, int newWidth, int newHeight);

	@Shadow
	protected abstract void onMove(long handle, int x, int y);

	@Shadow
	protected abstract void onResize(long handle, int newWidth, int newHeight);

	@Shadow
	protected abstract void onFocus(long handle, boolean focused);

	@Shadow
	protected abstract void onEnter(long handle, boolean entered);

	@Unique
	private boolean closeRequested;

	@Redirect(
			method = "createWindow",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/platform/Window;createGlfwWindow(IILjava/lang/String;JLcom/mojang/blaze3d/systems/GpuBackend;)J"
			)
	)
	private long sdlCreateWindow(int width, int height, String title, long monitor, GpuBackend backend) throws BackendCreationException {
		backend.setWindowHints();

		int properties = SDL_CreateProperties();

		SDL_SetBooleanProperty(
				properties,
				switch (backend) {
					case GlBackend ignored -> SDL_PROP_WINDOW_CREATE_OPENGL_BOOLEAN;
					case VulkanBackend ignored -> SDL_PROP_WINDOW_CREATE_VULKAN_BOOLEAN;
					default -> throw new IllegalStateException("Unexpected backend: " + backend);
				},
				true
		);
		SDL_SetBooleanProperty(properties, SDL_PROP_WINDOW_CREATE_RESIZABLE_BOOLEAN, true);
		SDL_SetBooleanProperty(properties, SDL_PROP_WINDOW_CREATE_HIGH_PIXEL_DENSITY_BOOLEAN, true);
		SDL_SetNumberProperty(properties, SDL_PROP_WINDOW_CREATE_WIDTH_NUMBER, width);
		SDL_SetNumberProperty(properties, SDL_PROP_WINDOW_CREATE_HEIGHT_NUMBER, height);
		SDL_SetStringProperty(properties, SDL_PROP_WINDOW_CREATE_TITLE_STRING, title);
		SDL_SetNumberProperty(properties, SDL_PROP_WINDOW_CREATE_Y_NUMBER, SDL_WINDOWPOS_CENTERED_DISPLAY(0));
		SDL_SetBooleanProperty(properties, SDL_PROP_WINDOW_CREATE_HIDDEN_BOOLEAN, true);

		long window = SDL_CreateWindowWithProperties(properties);
		if (window == 0) {
			throw new BackendCreationException(Objects.requireNonNull(SDLError.SDL_GetError()), BackendCreationException.Reason.OTHER);
		}

		SapphireEventHandler.windows.put(window, (Window) (Object) this);
		return window;
	}

	@Override
	public void sapphire$handleEvent(SDL_WindowEvent event) {
		switch (event.type()) {
			case SDL_EVENT_WINDOW_PIXEL_SIZE_CHANGED -> {
				this.onFramebufferResize(this.handle, event.data1(), event.data2());
			}
			case SDL_EVENT_WINDOW_MOVED -> {
				this.onMove(this.handle, event.data1(), event.data2());
			}
			case SDL_EVENT_WINDOW_RESIZED -> {
				this.onResize(this.handle, event.data1(), event.data2());
			}
			case SDL_EVENT_WINDOW_FOCUS_GAINED -> {
				this.onFocus(this.handle, true);
			}
			case SDL_EVENT_WINDOW_FOCUS_LOST -> {
				this.onFocus(this.handle, false);
			}
			case SDL_EVENT_WINDOW_MOUSE_ENTER -> {
				this.onEnter(this.handle, true);
			}
			case SDL_EVENT_WINDOW_MOUSE_LEAVE -> {
				this.onEnter(this.handle, false);
			}
			case SDL_EVENT_WINDOW_CLOSE_REQUESTED -> {
				this.closeRequested = true;
			}
		}
	}

	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lorg/lwjgl/glfw/GLFW;glfwSetFramebufferSizeCallback(JLorg/lwjgl/glfw/GLFWFramebufferSizeCallbackI;)Lorg/lwjgl/glfw/GLFWFramebufferSizeCallback;"
			)
	)
	@Nullable
	private static GLFWFramebufferSizeCallback sapphireSetFramebufferSizeCallback(long $, GLFWFramebufferSizeCallbackI cbfun) {
		return null;
	}

	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lorg/lwjgl/glfw/GLFW;glfwSetWindowPosCallback(JLorg/lwjgl/glfw/GLFWWindowPosCallbackI;)Lorg/lwjgl/glfw/GLFWWindowPosCallback;"
			)
	)
	@Nullable
	private static GLFWWindowPosCallback sapphireSetWindowPosCallback(long $, GLFWWindowPosCallbackI cbfun) {
		return null;
	}

	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lorg/lwjgl/glfw/GLFW;glfwSetWindowSizeCallback(JLorg/lwjgl/glfw/GLFWWindowSizeCallbackI;)Lorg/lwjgl/glfw/GLFWWindowSizeCallback;"
			)
	)
	@Nullable
	private static GLFWWindowSizeCallback sapphireSetWindowSizeCallback(long $, GLFWWindowSizeCallbackI cbfun) {
		return null;
	}

	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lorg/lwjgl/glfw/GLFW;glfwSetWindowFocusCallback(JLorg/lwjgl/glfw/GLFWWindowFocusCallbackI;)Lorg/lwjgl/glfw/GLFWWindowFocusCallback;"
			)
	)
	@Nullable
	private static GLFWWindowFocusCallback sapphireSetWindowFocusCallback(long $, GLFWWindowFocusCallbackI cbfun) {
		return null;
	}

	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lorg/lwjgl/glfw/GLFW;glfwSetCursorEnterCallback(JLorg/lwjgl/glfw/GLFWCursorEnterCallbackI;)Lorg/lwjgl/glfw/GLFWCursorEnterCallback;"
			)
	)
	@Nullable
	private static GLFWCursorEnterCallback sapphireSetCursorEnterCallback(long $, GLFWCursorEnterCallbackI cbfun) {
		return null;
	}

	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lorg/lwjgl/glfw/GLFW;glfwSetWindowIconifyCallback(JLorg/lwjgl/glfw/GLFWWindowIconifyCallbackI;)Lorg/lwjgl/glfw/GLFWWindowIconifyCallback;"
			)
	)
	@Nullable
	private static GLFWWindowIconifyCallback sapphireSetWindowIconifyCallback(long $, GLFWWindowIconifyCallbackI cbfun) {
		return null;
	}

	@Overwrite
	public boolean shouldClose() {
		return this.closeRequested;
	}

	@Overwrite
	public void setIcon(PackResources resources, IconSet iconSet) throws IOException {

	}

	@Overwrite
	private void setMode() {
	}

	@Redirect(method = "setTitle", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSetWindowTitle(JLjava/lang/CharSequence;)V"))
	private void sdlSetWindowTitle(long window, CharSequence title) {
		SDL_SetWindowTitle(window, title);
	}

	/**
	 * @author Maximum
	 * @reason SDL
	 */
	@Overwrite
	public void close() {
		RenderSystem.assertOnRenderThread();
		SDL_DestroyWindow(this.handle);
	}

	/**
	 * @author Maximum
	 * @reason SDL
	 */
	@Overwrite
	private void refreshFramebufferSize() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			if (SDL_GetWindowSizeInPixels(this.handle, w, h)) {
				this.framebufferWidth = w.get();
				this.framebufferHeight = h.get();
			} else {
				this.framebufferWidth = 1;
				this.framebufferHeight = 1;
			}
		}
	}

	@Overwrite
	public void updateRawMouseInput(boolean value) {
	}

	@Overwrite
	public void setWindowCloseCallback(Runnable task) {
// TODO: close callback
	}
}
