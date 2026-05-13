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

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashScreen {
    private static final Duration SPLASH_DURATION = Duration.seconds(2.2);

    public void show(Runnable onFinished) {
        Stage splashStage = new Stage(StageStyle.UNDECORATED);

        Label title = new Label("Fusion-Ja!");
        title.getStyleClass().add("splash-title");
        Label subtitle = new Label("Compositor de imágenes PNG / JPG");
        subtitle.getStyleClass().add("splash-subtitle");
        Label build = new Label("JavaFX 21");
        build.getStyleClass().add("splash-build");

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(280);
        progressBar.getStyleClass().add("splash-progress");
        Timeline progressTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(SPLASH_DURATION, new KeyValue(progressBar.progressProperty(), 1))
        );

        Region separator = new Region();
        separator.setPrefHeight(4);

        VBox root = new VBox(10, title, subtitle, separator, progressBar, build);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(28));
        root.getStyleClass().add("splash-root");
        root.setOpacity(0);

        Scene scene = new Scene(root, 420, 240);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

        splashStage.setScene(scene);
        splashStage.show();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(320), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition wait = new PauseTransition(SPLASH_DURATION);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(280), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> {
            splashStage.close();
            onFinished.run();
        });

        progressTimeline.play();
        new SequentialTransition(fadeIn, wait, fadeOut).play();
    }
}

