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

import com.imagefusion.controller.CanvasController;
import com.imagefusion.repository.InMemoryLayerRepository;
import com.imagefusion.service.ImageCompositionService;
import com.imagefusion.ui.SplashScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ImageFusionApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        SplashScreen splashScreen = new SplashScreen();
        splashScreen.show(() -> showMainWindow(primaryStage));
    }

    private void showMainWindow(Stage stage) {
        CanvasController controller = new CanvasController(
                stage,
                new InMemoryLayerRepository(),
                new ImageCompositionService()
        );

        Scene scene = new Scene(controller.createView(), 1280, 780);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

        stage.setTitle("Fusion-Ja!");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.show();

        controller.initialize();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

