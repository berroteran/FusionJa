/*
 * Copyright 2026 Omar Berroterán Silva
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.imagefusion.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemThemeResolver {
    private static final Logger LOGGER = Logger.getLogger(SystemThemeResolver.class.getName());
    private static final Duration COMMAND_TIMEOUT = Duration.ofSeconds(2);

    public ThemeMode resolve() {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        try {
            if (osName.contains("win")) {
                return resolveWindowsTheme();
            }
            if (osName.contains("mac")) {
                return resolveMacTheme();
            }
            return resolveLinuxTheme();
        } catch (IOException ex) {
            LOGGER.log(Level.FINE, "No se pudo resolver el tema del sistema operativo.", ex);
            return ThemeMode.LIGHT;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.FINE, "Resolución del tema del sistema interrumpida.", ex);
            return ThemeMode.LIGHT;
        }
    }

    private ThemeMode resolveWindowsTheme() throws IOException, InterruptedException {
        String output = executeCommand(
                "reg",
                "query",
                "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                "/v",
                "AppsUseLightTheme"
        );
        return output.contains("0x0") ? ThemeMode.DARK : ThemeMode.LIGHT;
    }

    private ThemeMode resolveMacTheme() throws IOException, InterruptedException {
        String output = executeCommand("defaults", "read", "-g", "AppleInterfaceStyle");
        return output.toLowerCase(Locale.ROOT).contains("dark") ? ThemeMode.DARK : ThemeMode.LIGHT;
    }

    private ThemeMode resolveLinuxTheme() throws IOException, InterruptedException {
        String output = executeCommand("gsettings", "get", "org.gnome.desktop.interface", "color-scheme");
        if (output.toLowerCase(Locale.ROOT).contains("dark")) {
            return ThemeMode.DARK;
        }
        return ThemeMode.LIGHT;
    }

    private String executeCommand(String... command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
        boolean finished = process.waitFor(COMMAND_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        if (!finished) {
            process.destroyForcibly();
            return "";
        }

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
            }
        }
        return output.toString();
    }
}
