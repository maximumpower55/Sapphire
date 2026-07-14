package me.maximumpower55.sapphire.backend.mixin.window;

import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_CLOSE_REQUESTED;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_ENTER_FULLSCREEN;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_FOCUS_GAINED;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_FOCUS_LOST;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_LEAVE_FULLSCREEN;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_MAXIMIZED;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_WINDOW_MINIMIZED;
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
import static org.lwjgl.sdl.SDLVideo.SDL_GetPrimaryDisplay;
import static org.lwjgl.sdl.SDLVideo.SDL_GetWindowFlags;
import static org.lwjgl.sdl.SDLVideo.SDL_GetWindowPosition;
import static org.lwjgl.sdl.SDLVideo.SDL_GetWindowSizeInPixels;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_HEIGHT_NUMBER;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_HIDDEN_BOOLEAN;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_HIGH_PIXEL_DENSITY_BOOLEAN;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_OPENGL_BOOLEAN;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_RESIZABLE_BOOLEAN;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_TITLE_STRING;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_VULKAN_BOOLEAN;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_WIDTH_NUMBER;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_X_NUMBER;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_CREATE_Y_NUMBER;
import static org.lwjgl.sdl.SDLVideo.SDL_SetWindowFullscreen;
import static org.lwjgl.sdl.SDLVideo.SDL_SetWindowIcon;
import static org.lwjgl.sdl.SDLVideo.SDL_SetWindowPosition;
import static org.lwjgl.sdl.SDLVideo.SDL_SetWindowTitle;
import static org.lwjgl.sdl.SDLVideo.SDL_WINDOWPOS_CENTERED_DISPLAY;
import static org.lwjgl.sdl.SDLVideo.SDL_WINDOW_FULLSCREEN;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
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
import org.lwjgl.sdl.SDLIOStream;
import org.lwjgl.sdl.SDLSurface;
import org.lwjgl.sdl.SDL_Surface;
import org.lwjgl.sdl.SDL_WindowEvent;
import org.lwjgl.system.MemoryStack;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.IconSet;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.MonitorManager;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.device.BackendCreationException;
import com.mojang.renderpearl.api.device.GpuBackend;
import com.mojang.renderpearl.backend.opengl.GlBackend;
import com.mojang.renderpearl.backend.vulkan.VulkanBackend;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.maximumpower55.sapphire.backend.SapphireEventHandler;
import me.maximumpower55.sapphire.backend.extension.WindowExt;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;

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

	@Shadow
	private boolean fullscreen;
	@Shadow
	@Final
	private MonitorManager monitorManager;
	@Shadow
	@Final
	private static Logger LOGGER;
	@Shadow
	private int x;
	@Shadow
	private int y;
	@Shadow
	private int width;
	@Shadow
	private int height;

	@Shadow
	private static int allowedWindowMinSize(int size) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	private int windowedX;
	@Shadow
	private int windowedY;
	@Shadow
	private int windowedWidth;
	@Shadow
	private int windowedHeight;
	@Shadow
	private Optional<VideoMode> preferredFullscreenVideoMode;

	@Shadow
	public abstract int getWidth();

	@Shadow
	public abstract int getHeight();

	@Shadow
	private boolean minimized;
	@Unique
	private int id;
	@Unique
	private int renderWidth;
	@Unique
	private int renderHeight;
	@Unique
	private boolean closeRequested;

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwGetPrimaryMonitor()J"))
	private static long sdlGetPrimaryDisplay() {
		return SDL_GetPrimaryDisplay();
	}

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwGetWindowPos(J[I[I)V"))
	private static void sdlGetWindowPosition(long window, int[] xpos, int[] ypos) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer x = stack.mallocInt(1);
			IntBuffer y = stack.mallocInt(1);
			if (SDL_GetWindowPosition(window, x, y)) {
				xpos[0] = x.get();
				ypos[0] = y.get();
			}
		}
	}

	@Redirect(
			method = "createWindow",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/platform/Window;createGlfwWindow(IILjava/lang/String;JLcom/mojang/renderpearl/api/device/GpuBackend;)J"
			)
	)
	private long sdlCreateWindow(int width, int height, String title, long monitor, GpuBackend backend) throws BackendCreationException {
		backend.setWindowHints();

		int displayId = (int) monitor;
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
		SDL_SetNumberProperty(properties, SDL_PROP_WINDOW_CREATE_X_NUMBER, SDL_WINDOWPOS_CENTERED_DISPLAY(displayId));
		SDL_SetNumberProperty(properties, SDL_PROP_WINDOW_CREATE_Y_NUMBER, SDL_WINDOWPOS_CENTERED_DISPLAY(displayId));
		SDL_SetBooleanProperty(properties, SDL_PROP_WINDOW_CREATE_HIDDEN_BOOLEAN, true);

		long window = SDL_CreateWindowWithProperties(properties);
		if (window == 0) {
			throw new BackendCreationException(Objects.requireNonNull(SDLError.SDL_GetError()), BackendCreationException.Reason.OTHER);
		}

		SapphireEventHandler.windows.put(window, (Window) (Object) this);
		return window;
	}

	@Override
	public void sapphire$onEvent(SDL_WindowEvent event) {
		switch (event.type()) {
			case SDL_EVENT_WINDOW_PIXEL_SIZE_CHANGED -> this.onFramebufferResize(this.handle, event.data1(), event.data2());
			case SDL_EVENT_WINDOW_MOVED -> this.onMove(this.handle, event.data1(), event.data2());
			case SDL_EVENT_WINDOW_RESIZED -> this.onResize(this.handle, event.data1(), event.data2());
			case SDL_EVENT_WINDOW_FOCUS_GAINED -> this.onFocus(this.handle, true);
			case SDL_EVENT_WINDOW_FOCUS_LOST -> this.onFocus(this.handle, false);
			case SDL_EVENT_WINDOW_MOUSE_ENTER -> this.onEnter(this.handle, true);
			case SDL_EVENT_WINDOW_MOUSE_LEAVE -> this.onEnter(this.handle, false);
			case SDL_EVENT_WINDOW_MINIMIZED -> this.minimized = true;
			case SDL_EVENT_WINDOW_MAXIMIZED -> this.minimized = false;
			case SDL_EVENT_WINDOW_ENTER_FULLSCREEN -> this.fullscreen = true;
			case SDL_EVENT_WINDOW_LEAVE_FULLSCREEN -> this.fullscreen = false;
			case SDL_EVENT_WINDOW_CLOSE_REQUESTED -> this.closeRequested = true;
		}
	}

	@Override
	public int sapphire$renderWidth() {
		return this.renderWidth;
	}

	@Override
	public int sapphire$renderHeight() {
		return this.renderHeight;
	}

	@Redirect(
			method = {"calculateScale", "setGuiScale"},
			at = @At(
					value = "FIELD",
					target = "Lcom/mojang/blaze3d/platform/Window;framebufferWidth:I",
					opcode = Opcodes.GETFIELD
			)
	)
	private static int useRenderWidthInGui(Window instance) {
		return instance.sapphire$renderWidth();
	}

	@Redirect(
			method = {"calculateScale", "setGuiScale"},
			at = @At(
					value = "FIELD",
					target = "Lcom/mojang/blaze3d/platform/Window;framebufferHeight:I",
					opcode = Opcodes.GETFIELD
			)
	)
	private static int useRenderHeightInGui(Window instance) {
		return instance.sapphire$renderHeight();
	}

	@Inject(method = "onFramebufferResize", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/WindowEventHandler;framebufferSizeChanged()V"))
	private void updateRenderSize(long handle, int newWidth, int newHeight, CallbackInfo ci) {
		if (!this.fullscreen) {
			this.renderWidth = newWidth;
			this.renderHeight = newHeight;
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
	private static GLFWFramebufferSizeCallback noopSetFramebufferSizeCallback(long $, GLFWFramebufferSizeCallbackI cbfun) {
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
	private static GLFWWindowPosCallback noopSetWindowPosCallback(long $, GLFWWindowPosCallbackI cbfun) {
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
	private static GLFWWindowSizeCallback noopSetWindowSizeCallback(long $, GLFWWindowSizeCallbackI cbfun) {
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
	private static GLFWWindowFocusCallback noopSetWindowFocusCallback(long $, GLFWWindowFocusCallbackI cbfun) {
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
	private static GLFWCursorEnterCallback noopSetCursorEnterCallback(long $, GLFWCursorEnterCallbackI cbfun) {
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
	private static GLFWWindowIconifyCallback noopSetWindowIconifyCallback(long $, GLFWWindowIconifyCallbackI cbfun) {
		return null;
	}

	@Redirect(method = "shouldClose", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GLX;_shouldClose(Lcom/mojang/blaze3d/platform/Window;)Z"))
	private boolean sdlCloseRequested(Window window) {
		return this.closeRequested;
	}

	/**
	 * @author Maximum
	 * @reason SDL
	 */
	@Overwrite
	public void setIcon(PackResources resources, IconSet iconSet) throws IOException {
		List<IoSupplier<InputStream>> iconStreams = iconSet.getStandardIcons(resources);

		ObjectList<SDL_Surface> icons = new ObjectArrayList<>();
		try (MemoryStack stack = MemoryStack.stackPush()) {
			for (IoSupplier<InputStream> iconStream : iconStreams) {
				InputStream stream = iconStream.get();
				try {
					ByteBuffer iconBuf = stack.bytes(stream.readAllBytes());
					SDL_Surface icon = SDLSurface.SDL_LoadPNG_IO(SDLIOStream.SDL_IOFromMem(iconBuf), true);
					if (icon != null && icon.w() >= 64) {
						if (!icons.isEmpty()) {
							SDLSurface.SDL_AddSurfaceAlternateImage(icons.getFirst(), icon);
						}

						icons.add(icon);
					}
				} finally {
					IOUtils.closeQuietly(stream);
				}
			}

			SDL_SetWindowIcon(this.handle, icons.getFirst());
		} finally {
			icons.forEach(SDLSurface::SDL_DestroySurface);
		}
	}

	/**
	 * @author Maximum
	 * @reason SDL
	 */
	@Overwrite
	private void setMode() {
		boolean wasFullscreen = (SDL_GetWindowFlags(this.handle) & SDL_WINDOW_FULLSCREEN) != 0;
		if (this.fullscreen) {
			Monitor monitor = this.monitorManager.findBestMonitor((Window) (Object) this);
			if (monitor == null) {
				LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
				this.fullscreen = false;
			} else {
				VideoMode mode = monitor.getPreferredVidMode(this.preferredFullscreenVideoMode);
				if (!wasFullscreen) {
					this.windowedX = this.x;
					this.windowedY = this.y;
					this.windowedWidth = allowedWindowMinSize(this.width);
					this.windowedHeight = allowedWindowMinSize(this.height);
				}

				this.x = monitor.x();
				this.y = monitor.y();
				SDL_SetWindowPosition(this.handle, this.x, this.y);

				this.renderWidth = mode.getWidth();
				this.renderHeight = mode.getHeight();

				SDL_SetWindowFullscreen(this.handle, true);
			}
		} else {
			this.x = this.windowedX;
			this.y = this.windowedY;
			SDL_SetWindowPosition(this.handle, this.x, this.y);

			SDL_SetWindowFullscreen(this.handle, false);
		}
	}

	@Redirect(method = "setTitle", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSetWindowTitle(JLjava/lang/CharSequence;)V"))
	private static void sdlSetWindowTitle(long window, CharSequence title) {
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
				this.renderWidth = this.framebufferWidth = w.get();
				this.renderHeight = this.framebufferHeight = h.get();
			} else {
				this.renderWidth = this.framebufferWidth = 1;
				this.renderHeight = this.framebufferHeight = 1;
			}
		}
	}

	/**
	 * @author Maximum
	 * @reason Noop
	 */
	@Overwrite
	public static void setBootErrorCallback() {
	}

	/**
	 * @author Maximum
	 * @reason Noop
	 */
	@Overwrite
	public void setDefaultErrorCallback() {
	}

	/**
	 * @author Maximum
	 * @reason SDL
	 */
	@Overwrite
	public void setWindowCloseCallback(Runnable task) {
	}
}
