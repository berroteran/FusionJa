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
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashScreen {
    private static final Duration LOADING_DURATION = Duration.seconds(2.2);

    public void show(Runnable onFinished) {
        Stage splashStage = new Stage(StageStyle.TRANSPARENT);

        Label logo = new Label("FJ");
        logo.getStyleClass().add("splash-logo");
        Label title = new Label("Fusion-Ja!");
        title.getStyleClass().add("splash-title");
        Label subtitle = new Label("Compositor de imágenes PNG / JPG");
        subtitle.getStyleClass().add("splash-subtitle");
        Label status = new Label("Inicializando módulos...");
        status.getStyleClass().add("splash-status");
        Label build = new Label("JavaFX 21");
        build.getStyleClass().add("splash-build");

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(320);
        progressBar.getStyleClass().add("splash-progress");
        Timeline progressTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(LOADING_DURATION, new KeyValue(progressBar.progressProperty(), 1))
        );
        Timeline statusTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> status.setText("Inicializando módulos...")),
                new KeyFrame(Duration.seconds(0.8), event -> status.setText("Cargando motor de composición...")),
                new KeyFrame(Duration.seconds(1.6), event -> status.setText("Preparando espacio de trabajo...")),
                new KeyFrame(LOADING_DURATION, event -> status.setText("Listo"))
        );

        VBox card = new VBox(10, logo, title, subtitle, progressBar, status, build);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(26));
        card.getStyleClass().add("splash-card");

        StackPane root = new StackPane(card);
        root.getStyleClass().add("splash-root");
        root.setPadding(new Insets(18));
        root.setOpacity(0);
        root.setScaleX(0.96);
        root.setScaleY(0.96);

        Scene scene = new Scene(root, 500, 300);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

        splashStage.setScene(scene);
        splashStage.setAlwaysOnTop(true);
        splashStage.centerOnScreen();
        splashStage.show();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(260), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(380), root);
        scaleIn.setFromX(0.96);
        scaleIn.setFromY(0.96);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        ParallelTransition intro = new ParallelTransition(fadeIn, scaleIn);

        PauseTransition wait = new PauseTransition(LOADING_DURATION);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(220), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            splashStage.close();
            onFinished.run();
        });

        progressTimeline.play();
        statusTimeline.play();
        new SequentialTransition(intro, wait, fadeOut).play();
    }
}

