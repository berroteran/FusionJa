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
package com.imagefusion.dto;

import com.imagefusion.model.ImageLayer;

public record LayerDto(
        String id,
        String name,
        int width,
        int height,
        int x,
        int y,
        int dpi
) {
    public static LayerDto from(ImageLayer layer) {
        return new LayerDto(
                layer.getId(),
                layer.getName(),
                layer.getWidth(),
                layer.getHeight(),
                (int) Math.round(layer.getX()),
                (int) Math.round(layer.getY()),
                layer.getDpi()
        );
    }

    @Override
    public String toString() {
        return "%s (%dx%d) @ (%d,%d) - %d DPI".formatted(name, width, height, x, y, dpi);
    }
}

