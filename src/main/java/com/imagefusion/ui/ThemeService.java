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

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;

import java.util.Objects;
import java.util.prefs.Preferences;

public class ThemeService {
    private static final String LIGHT_CLASS = "theme-light";
    private static final String DARK_CLASS = "theme-dark";
    private static final String THEME_MODE_KEY = "themeMode";
    private static final String BASE_THEME_KEY = "baseTheme";

    private final Preferences preferences = Preferences.userNodeForPackage(ThemeService.class);
    private final SystemThemeResolver systemThemeResolver = new SystemThemeResolver();
    private final ObjectProperty<ThemeMode> themeMode = new SimpleObjectProperty<>(readThemeMode());
    private final ObjectProperty<JavaFxBaseTheme> baseTheme = new SimpleObjectProperty<>(readBaseTheme());

    private Scene scene;

    public ThemeService() {
        themeMode.addListener((obs, oldMode, newMode) -> {
            preferences.put(THEME_MODE_KEY, newMode.name());
            applyThemeMode();
        });
        baseTheme.addListener((obs, oldTheme, newTheme) -> {
            preferences.put(BASE_THEME_KEY, newTheme.name());
            applyBaseTheme();
        });
        applyBaseTheme();
    }

    public void attachScene(Scene scene) {
        this.scene = Objects.requireNonNull(scene, "scene");
        applyThemeMode();
    }

    public ThemeMode getThemeMode() {
        return themeMode.get();
    }

    public void setThemeMode(ThemeMode themeMode) {
        this.themeMode.set(Objects.requireNonNull(themeMode, "themeMode"));
    }

    public ReadOnlyObjectProperty<ThemeMode> themeModeProperty() {
        return themeMode;
    }

    public JavaFxBaseTheme getBaseTheme() {
        return baseTheme.get();
    }

    public void setBaseTheme(JavaFxBaseTheme baseTheme) {
        this.baseTheme.set(Objects.requireNonNull(baseTheme, "baseTheme"));
    }

    public ReadOnlyObjectProperty<JavaFxBaseTheme> baseThemeProperty() {
        return baseTheme;
    }

    private void applyBaseTheme() {
        Application.setUserAgentStylesheet(baseTheme.get().getUserAgentStylesheet());
    }

    private void applyThemeMode() {
        if (scene == null) {
            return;
        }

        ThemeMode effectiveMode = themeMode.get() == ThemeMode.SYSTEM ? systemThemeResolver.resolve() : themeMode.get();
        scene.getRoot().getStyleClass().removeAll(LIGHT_CLASS, DARK_CLASS);
        scene.getRoot().getStyleClass().add(effectiveMode == ThemeMode.DARK ? DARK_CLASS : LIGHT_CLASS);
    }

    private ThemeMode readThemeMode() {
        return readEnum(THEME_MODE_KEY, ThemeMode.class, ThemeMode.SYSTEM);
    }

    private JavaFxBaseTheme readBaseTheme() {
        return readEnum(BASE_THEME_KEY, JavaFxBaseTheme.class, JavaFxBaseTheme.MODENA);
    }

    private <T extends Enum<T>> T readEnum(String key, Class<T> enumType, T defaultValue) {
        String rawValue = preferences.get(key, defaultValue.name());
        try {
            return Enum.valueOf(enumType, rawValue);
        } catch (IllegalArgumentException ex) {
            return defaultValue;
        }
    }
}
