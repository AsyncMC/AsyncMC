package com.github.asyncmc.boot.internal;

import com.github.asyncmc.boot.AsyncMCBoot;
import com.github.asyncmc.core.boot.AsyncMCBootLoader;

import java.io.File;
import java.io.IOException;
import java.lang.module.ModuleFinder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

public class AsyncMCLauncher {
    public static void main(final String[] args) {
        var main = getCurrentJar().map(AsyncMCLauncher::loadLibraries).orElse(null);
        if (main != null) {
            launch(main, args);
        } else {
            directLaunch(args);
        }
    }
    
    private static void directLaunch(final String[] args) {
        AsyncMCBootLoader.main(args);
    }

    private static void launch(final Method main, final String[] args) {
        try {
            main.invoke(null, (Object) args);
        } catch (IllegalAccessException e) {
            throw new BootstrapMethodError("Could not launch the application", e);
        } catch (InvocationTargetException e) {
            var cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new BootstrapMethodError("The application startup failed with an uncaught exception", e);
        }
    }

    private static Method loadLibraries(final Path path) {
        var mainClassName = "com.github.asyncmc.core.boot.AsyncMCBootLoader";
        var mainModuleName = "com.github.asyncmc.internal.core";

        try(var jar = FileSystems.newFileSystem(path);
            var walk = Files.walk(jar.getPath("/libs/"))
        ) {
            var finder = ModuleFinder.of(walk.toArray(Path[]::new));
            var config = ModuleLayer.boot().configuration().resolve(finder,
                    ModuleFinder.of(), Set.of(mainModuleName));
            var layer =
                    ModuleLayer.boot().defineModulesWithOneLoader(config,
                            ClassLoader.getSystemClassLoader());
            return layer.findLoader(mainModuleName)
                    .loadClass(mainClassName)
                    .getDeclaredMethod("main",String[].class);
        } catch (IOException | ClassNotFoundException | NoSuchMethodException e) {
            throw new BootstrapMethodError(e);
        }
    }

    private static Optional<Path> getCurrentJar() {
        try {
            return Optional.of(new File(AsyncMCBoot.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toPath())
                    .filter(it-> it.toString().toLowerCase().endsWith(".jar"));
        } catch (URISyntaxException ignored) {
            return Optional.empty();
        }
    }
}
