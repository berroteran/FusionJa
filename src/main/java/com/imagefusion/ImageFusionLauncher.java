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
package com.imagefusion;

import javafx.application.Application;

/**
 * Punto de entrada neutral para ejecutar JavaFX sin depender del launcher implícito del JDK.
 */
public final class ImageFusionLauncher {

    private ImageFusionLauncher() {
        throw new IllegalStateException("Utility class");
    }

    public static void main(String[] args) {
        configurePrismPipeline();
        Application.launch(ImageFusionApplication.class, args);
    }

    /**
     * Mitiga inestabilidades conocidas del pipeline D3D en Windows.
     * Se permite override explícito por VM options cuando sea necesario.
     */
    private static void configurePrismPipeline() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        if (!osName.contains("win")) {
            return;
        }
        if (System.getProperty("prism.order") != null) {
            return;
        }
        if (System.getProperty("prism.d3d") != null) {
            return;
        }
        System.setProperty("prism.d3d", "false");
    }
}
