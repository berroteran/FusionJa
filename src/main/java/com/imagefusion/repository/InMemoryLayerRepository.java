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
package com.imagefusion.repository;

import com.imagefusion.model.ImageLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InMemoryLayerRepository implements LayerRepository {

    private final List<ImageLayer> layers = new ArrayList<>();

    @Override
    public List<ImageLayer> findAll() {
        return new ArrayList<>(layers);
    }

    @Override
    public Optional<ImageLayer> findById(String id) {
        return layers.stream().filter(layer -> layer.getId().equals(id)).findFirst();
    }

    @Override
    public void add(ImageLayer layer) {
        layers.add(Objects.requireNonNull(layer, "Layer is required"));
    }

    @Override
    public void removeById(String id) {
        layers.removeIf(layer -> layer.getId().equals(id));
    }

    @Override
    public void clear() {
        layers.clear();
    }

    @Override
    public int size() {
        return layers.size();
    }

    @Override
    public void bringToFront(String id) {
        findById(id).ifPresent(layer -> {
            layers.remove(layer);
            layers.add(layer);
        });
    }

    @Override
    public void sendToBack(String id) {
        findById(id).ifPresent(layer -> {
            layers.remove(layer);
            layers.add(0, layer);
        });
    }
}

