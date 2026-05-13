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
package com.imagefusion.service;

import com.imagefusion.dto.CompressionOptions;
import com.imagefusion.dto.ExportOptions;
import com.imagefusion.model.ImageLayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageCompositionServiceTest {

    private final ImageCompositionService service = new ImageCompositionService();

    @TempDir
    Path tempDir;

    @Test
    void mergeLayersShouldUseGlobalBounds() {
        BufferedImage image1 = createSolidImage(50, 40, Color.RED);
        BufferedImage image2 = createSolidImage(30, 40, Color.BLUE);

        ImageLayer layer1 = new ImageLayer(UUID.randomUUID().toString(), "Layer 1", image1, 0, 0, 300);
        ImageLayer layer2 = new ImageLayer(UUID.randomUUID().toString(), "Layer 2", image2, 50, 0, 300);

        BufferedImage merged = service.mergeLayers(List.of(layer1, layer2));

        assertEquals(80, merged.getWidth());
        assertEquals(40, merged.getHeight());
    }

    @Test
    void rescaleToDpiShouldScaleImageDimensions() {
        BufferedImage input = createSolidImage(100, 60, Color.GREEN);

        BufferedImage scaled = service.rescaleToDpi(input, 150, 300);

        assertEquals(200, scaled.getWidth());
        assertEquals(120, scaled.getHeight());
    }

    @Test
    void exportImageShouldWritePngAndJpg(@TempDir Path tempExportDir) throws Exception {
        BufferedImage input = createSolidImage(64, 64, Color.ORANGE);

        Path pngPath = tempExportDir.resolve("output.png");
        Path jpgPath = tempExportDir.resolve("output.jpg");

        service.exportImage(input, pngPath, new ExportOptions("png", new CompressionOptions(0.9f, 6), 300, true));
        service.exportImage(input, jpgPath, new ExportOptions("jpg", new CompressionOptions(0.8f, 6), 300, true));

        assertTrue(Files.exists(pngPath));
        assertTrue(Files.exists(jpgPath));
        assertNotNull(ImageIO.read(pngPath.toFile()));
        assertNotNull(ImageIO.read(jpgPath.toFile()));
    }

    private BufferedImage createSolidImage(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(color);
        graphics.fillRect(0, 0, width, height);
        graphics.dispose();
        return image;
    }
}

