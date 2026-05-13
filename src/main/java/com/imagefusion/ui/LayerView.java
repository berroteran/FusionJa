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

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LayerView extends Group {
    private final String layerId;
    private final ImageView imageView;
    private final Rectangle selectionOutline;

    public LayerView(String layerId, Image image) {
        this.layerId = layerId;
        this.imageView = new ImageView(image);
        this.imageView.setSmooth(true);
        this.imageView.setPreserveRatio(false);

        this.selectionOutline = new Rectangle(image.getWidth(), image.getHeight());
        this.selectionOutline.setFill(Color.TRANSPARENT);
        this.selectionOutline.setStroke(Color.DODGERBLUE);
        this.selectionOutline.setStrokeWidth(1.5);
        this.selectionOutline.getStrokeDashArray().addAll(6.0, 4.0);
        this.selectionOutline.setVisible(false);

        getChildren().addAll(imageView, selectionOutline);
    }

    public String getLayerId() {
        return layerId;
    }

    public double getLayerWidth() {
        return imageView.getImage().getWidth();
    }

    public double getLayerHeight() {
        return imageView.getImage().getHeight();
    }

    public void setSelected(boolean selected) {
        selectionOutline.setVisible(selected);
    }
}

