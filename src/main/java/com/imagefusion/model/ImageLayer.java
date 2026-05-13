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
package com.imagefusion.model;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class ImageLayer {
    private final String id;
    private String name;
    private BufferedImage image;
    private double x;
    private double y;
    private int dpi;
    private boolean visible;

    public ImageLayer(String id, String name, BufferedImage image, double x, double y, int dpi) {
        this.id = Objects.requireNonNull(id, "Layer id is required");
        this.name = Objects.requireNonNull(name, "Layer name is required");
        this.image = Objects.requireNonNull(image, "Layer image is required");
        this.x = x;
        this.y = y;
        this.dpi = dpi;
        this.visible = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Layer name is required");
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = Objects.requireNonNull(image, "Layer image is required");
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getDpi() {
        return dpi;
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }
}

