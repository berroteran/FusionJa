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
    private final Rectangle layerOutline;
    private final Rectangle selectionOutline;

    public LayerView(String layerId, Image image) {
        this.layerId = layerId;
        this.imageView = new ImageView(image);
        this.imageView.setSmooth(true);
        this.imageView.setPreserveRatio(false);

        this.layerOutline = new Rectangle(image.getWidth(), image.getHeight());
        this.layerOutline.setFill(Color.TRANSPARENT);
        this.layerOutline.setStroke(Color.color(1.0, 1.0, 1.0, 0.95));
        this.layerOutline.setStrokeWidth(2.0);
        this.layerOutline.setMouseTransparent(true);

        this.selectionOutline = new Rectangle(image.getWidth(), image.getHeight());
        this.selectionOutline.setFill(Color.TRANSPARENT);
        this.selectionOutline.setStroke(Color.web("#00d4ff"));
        this.selectionOutline.setStrokeWidth(3.0);
        this.selectionOutline.getStrokeDashArray().addAll(10.0, 6.0);
        this.selectionOutline.setMouseTransparent(true);
        this.selectionOutline.setVisible(false);

        getChildren().addAll(imageView, layerOutline, selectionOutline);
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
        layerOutline.setStroke(selected ? Color.web("#ffb703") : Color.color(1.0, 1.0, 1.0, 0.95));
        layerOutline.setStrokeWidth(selected ? 3.0 : 2.0);
    }
}

