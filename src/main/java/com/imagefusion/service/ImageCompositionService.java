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

import com.imagefusion.dto.ExportOptions;
import com.imagefusion.dto.LoadedImage;
import com.imagefusion.exception.ImageFusionException;
import com.imagefusion.model.ImageLayer;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageCompositionService {
    private static final Logger LOGGER = Logger.getLogger(ImageCompositionService.class.getName());
    private static final int DEFAULT_DPI = 72;
    private static final Set<String> SUPPORTED_INPUT_FORMATS = Set.of("png", "jpg", "jpeg");
    private static final Set<String> SUPPORTED_OUTPUT_FORMATS = Set.of("png", "jpg");

    public LoadedImage loadImage(Path path) throws IOException {
        String extension = getExtension(path);
        if (!SUPPORTED_INPUT_FORMATS.contains(extension)) {
            throw new ImageFusionException("Only PNG and JPG images are supported");
        }

        try (ImageInputStream inputStream = ImageIO.createImageInputStream(path.toFile())) {
            if (inputStream == null) {
                throw new ImageFusionException("Cannot open image file: " + path);
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(inputStream);
            if (!readers.hasNext()) {
                throw new ImageFusionException("Unsupported or invalid image file: " + path.getFileName());
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(inputStream, true, true);
                String readerFormat = normalizeFormat(reader.getFormatName());
                ImageReadParam readParam = reader.getDefaultReadParam();
                BufferedImage image = reader.read(0, readParam);
                if (image == null) {
                    throw new ImageFusionException("Cannot decode image: " + path.getFileName());
                }

                int dpi = readDpi(reader.getImageMetadata(0), readerFormat);
                return new LoadedImage(image, dpi, readerFormat);
            } finally {
                reader.dispose();
            }
        }
    }

    public BufferedImage rescaleToDpi(BufferedImage sourceImage, int sourceDpi, int targetDpi) {
        if (sourceImage == null) {
            throw new IllegalArgumentException("Source image is required");
        }
        if (sourceDpi <= 0 || targetDpi <= 0) {
            throw new IllegalArgumentException("DPI values must be greater than zero");
        }
        if (sourceDpi == targetDpi) {
            return sourceImage;
        }

        double scaleFactor = (double) targetDpi / sourceDpi;
        int targetWidth = Math.max(1, (int) Math.round(sourceImage.getWidth() * scaleFactor));
        int targetHeight = Math.max(1, (int) Math.round(sourceImage.getHeight() * scaleFactor));

        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = resized.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.drawImage(sourceImage, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();
        return resized;
    }

    public BufferedImage mergeLayers(List<ImageLayer> layers) {
        if (layers == null || layers.isEmpty()) {
            throw new ImageFusionException("There are no layers to merge");
        }

        List<ImageLayer> visibleLayers = layers.stream().filter(ImageLayer::isVisible).toList();
        if (visibleLayers.isEmpty()) {
            throw new ImageFusionException("There are no visible layers to merge");
        }

        Rectangle2D bounds = calculateBounds(visibleLayers);
        int width = Math.max(1, (int) Math.ceil(bounds.getWidth()));
        int height = Math.max(1, (int) Math.ceil(bounds.getHeight()));
        int offsetX = (int) Math.round(-bounds.getMinX());
        int offsetY = (int) Math.round(-bounds.getMinY());

        BufferedImage merged = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = merged.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setComposite(AlphaComposite.SrcOver);

        for (ImageLayer layer : visibleLayers) {
            int layerX = (int) Math.round(layer.getX()) + offsetX;
            int layerY = (int) Math.round(layer.getY()) + offsetY;
            graphics.drawImage(layer.getImage(), layerX, layerY, null);
        }
        graphics.dispose();

        return merged;
    }

    public void exportImage(BufferedImage image, Path outputPath, ExportOptions options) throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("Image is required");
        }
        if (outputPath == null) {
            throw new IllegalArgumentException("Output path is required");
        }
        if (options == null) {
            throw new IllegalArgumentException("Export options are required");
        }

        String format = normalizeFormat(options.format());
        if (!SUPPORTED_OUTPUT_FORMATS.contains(format)) {
            throw new ImageFusionException("Unsupported export format: " + format);
        }

        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        BufferedImage imageForExport = "jpg".equals(format) ? flattenForJpeg(image) : image;
        ImageWriter writer = ImageIO.getImageWritersByFormatName(format).next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();

        if (writeParam.canWriteCompressed()) {
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            if ("jpg".equals(format)) {
                writeParam.setCompressionQuality(options.compressionOptions().jpegQuality());
            } else if ("png".equals(format)) {
                float pngQuality = 1.0f - (options.compressionOptions().pngCompressionLevel() / 9.0f);
                writeParam.setCompressionQuality(Math.max(0.0f, Math.min(1.0f, pngQuality)));
            }
        }

        ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromRenderedImage(imageForExport);
        IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
        if (options.writeDpiMetadata() && metadata != null) {
            applyDpiMetadata(metadata, format, options.dpi());
        }

        try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(outputPath.toFile())) {
            writer.setOutput(outputStream);
            writer.write(null, new IIOImage(imageForExport, null, metadata), writeParam);
        } catch (Exception ex) {
            throw new IOException("Error exporting image: " + outputPath, ex);
        } finally {
            writer.dispose();
        }
    }

    private Rectangle2D calculateBounds(List<ImageLayer> layers) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (ImageLayer layer : layers) {
            minX = Math.min(minX, layer.getX());
            minY = Math.min(minY, layer.getY());
            maxX = Math.max(maxX, layer.getX() + layer.getWidth());
            maxY = Math.max(maxY, layer.getY() + layer.getHeight());
        }

        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }

    private BufferedImage flattenForJpeg(BufferedImage source) {
        BufferedImage rgbImage = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = rgbImage.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, source.getWidth(), source.getHeight());
        graphics.drawImage(source, 0, 0, null);
        graphics.dispose();
        return rgbImage;
    }

    private int readDpi(IIOMetadata metadata, String format) {
        if (metadata == null) {
            return DEFAULT_DPI;
        }
        try {
            if ("jpg".equals(format)) {
                return readJpegDpi(metadata);
            }
            if ("png".equals(format)) {
                return readPngDpi(metadata);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.FINE, "Could not read DPI metadata, fallback to default", ex);
        }
        return DEFAULT_DPI;
    }

    private int readJpegDpi(IIOMetadata metadata) throws Exception {
        Node root = metadata.getAsTree("javax_imageio_jpeg_image_1.0");
        Node jfifNode = findNode(root, "app0JFIF");
        if (jfifNode == null) {
            return DEFAULT_DPI;
        }

        NamedNodeMap attributes = jfifNode.getAttributes();
        int xDensity = parseIntAttribute(attributes, "Xdensity", DEFAULT_DPI);
        int yDensity = parseIntAttribute(attributes, "Ydensity", DEFAULT_DPI);
        int units = parseIntAttribute(attributes, "resUnits", 1);

        int averageDensity = (xDensity + yDensity) / 2;
        if (units == 1) {
            return averageDensity;
        }
        if (units == 2) {
            return (int) Math.round(averageDensity * 2.54);
        }
        return DEFAULT_DPI;
    }

    private int readPngDpi(IIOMetadata metadata) throws Exception {
        Node root = metadata.getAsTree("javax_imageio_1.0");
        Node dimensionNode = findNode(root, "Dimension");
        if (dimensionNode == null) {
            return DEFAULT_DPI;
        }

        Double horizontalPixelSize = findDoubleNodeAttribute(dimensionNode, "HorizontalPixelSize", "value");
        Double verticalPixelSize = findDoubleNodeAttribute(dimensionNode, "VerticalPixelSize", "value");
        if (horizontalPixelSize == null || verticalPixelSize == null) {
            return DEFAULT_DPI;
        }

        double averageMmPerPixel = (horizontalPixelSize + verticalPixelSize) / 2.0;
        if (averageMmPerPixel <= 0) {
            return DEFAULT_DPI;
        }

        return (int) Math.max(1, Math.round(25.4 / averageMmPerPixel));
    }

    private void applyDpiMetadata(IIOMetadata metadata, String format, int dpi) {
        try {
            if ("jpg".equals(format)) {
                Node root = metadata.getAsTree("javax_imageio_jpeg_image_1.0");
                Node jfifNode = findNode(root, "app0JFIF");
                if (jfifNode != null) {
                    NamedNodeMap attributes = jfifNode.getAttributes();
                    attributes.getNamedItem("Xdensity").setNodeValue(String.valueOf(dpi));
                    attributes.getNamedItem("Ydensity").setNodeValue(String.valueOf(dpi));
                    attributes.getNamedItem("resUnits").setNodeValue("1");
                    metadata.setFromTree("javax_imageio_jpeg_image_1.0", root);
                }
                return;
            }

            if ("png".equals(format)) {
                IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree("javax_imageio_1.0");
                IIOMetadataNode dimensionNode = getOrCreateChild(root, "Dimension");
                setOrCreateDimensionValue(dimensionNode, "HorizontalPixelSize", String.valueOf(25.4 / dpi));
                setOrCreateDimensionValue(dimensionNode, "VerticalPixelSize", String.valueOf(25.4 / dpi));
                metadata.mergeTree("javax_imageio_1.0", root);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Could not apply DPI metadata", ex);
        }
    }

    private Node findNode(Node node, String name) {
        if (node == null) {
            return null;
        }
        if (name.equals(node.getNodeName())) {
            return node;
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node found = findNode(children.item(i), name);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private Double findDoubleNodeAttribute(Node parent, String childName, String attribute) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (!childName.equals(child.getNodeName())) {
                continue;
            }
            NamedNodeMap attrs = child.getAttributes();
            if (attrs == null || attrs.getNamedItem(attribute) == null) {
                continue;
            }
            try {
                return Double.parseDouble(attrs.getNamedItem(attribute).getNodeValue());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private int parseIntAttribute(NamedNodeMap attributes, String key, int defaultValue) {
        if (attributes == null) {
            return defaultValue;
        }
        Node valueNode = attributes.getNamedItem(key);
        if (valueNode == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(valueNode.getNodeValue());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private IIOMetadataNode getOrCreateChild(IIOMetadataNode root, String nodeName) {
        for (int i = 0; i < root.getLength(); i++) {
            Node child = root.item(i);
            if (nodeName.equals(child.getNodeName())) {
                return (IIOMetadataNode) child;
            }
        }
        IIOMetadataNode newChild = new IIOMetadataNode(nodeName);
        root.appendChild(newChild);
        return newChild;
    }

    private void setOrCreateDimensionValue(IIOMetadataNode dimensionNode, String name, String value) {
        for (int i = 0; i < dimensionNode.getLength(); i++) {
            Node node = dimensionNode.item(i);
            if (name.equals(node.getNodeName())) {
                ((IIOMetadataNode) node).setAttribute("value", value);
                return;
            }
        }
        IIOMetadataNode newNode = new IIOMetadataNode(name);
        newNode.setAttribute("value", value);
        dimensionNode.appendChild(newNode);
    }

    private String normalizeFormat(String format) {
        if (format == null) {
            return "";
        }
        String normalized = format.toLowerCase(Locale.ROOT);
        return "jpeg".equals(normalized) ? "jpg" : normalized;
    }

    private String getExtension(Path path) {
        String fileName = path.getFileName().toString();
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0 || lastDot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDot + 1).toLowerCase(Locale.ROOT);
    }
}

