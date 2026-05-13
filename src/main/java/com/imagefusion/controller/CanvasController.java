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
package com.imagefusion.controller;

import com.imagefusion.dto.CompressionOptions;
import com.imagefusion.dto.ExportOptions;
import com.imagefusion.dto.LayerDto;
import com.imagefusion.dto.LoadedImage;
import com.imagefusion.exception.ImageFusionException;
import com.imagefusion.model.ImageLayer;
import com.imagefusion.repository.LayerRepository;
import com.imagefusion.service.ImageCompositionService;
import com.imagefusion.ui.LayerView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CanvasController {
    private static final Logger LOGGER = Logger.getLogger(CanvasController.class.getName());

    private static final int MAX_LAYERS = 4;
    private static final double DEFAULT_CANVAS_WIDTH = 900;
    private static final double DEFAULT_CANVAS_HEIGHT = 600;
    private static final double CANVAS_MARGIN = 20;
    private static final int DEFAULT_DPI = 300;

    private final Stage stage;
    private final LayerRepository repository;
    private final ImageCompositionService imageService;

    private final Pane canvasPane = new Pane();
    private final ScrollPane canvasScroll = new ScrollPane(canvasPane);
    private final ListView<LayerDto> layersListView = new ListView<>();
    private final Label statusLabel = new Label("Ready");
    private final Slider zoomSlider = new Slider(0.10, 5.0, 1.0);
    private final Label zoomValueLabel = new Label("100%");

    private final CheckBox forceDpiCheck = new CheckBox("Force DPI on import");
    private final Spinner<Integer> dpiSpinner = new Spinner<>(72, 1200, DEFAULT_DPI);
    private final Slider jpegQualitySlider = new Slider(0.1, 1.0, 0.9);
    private final Spinner<Integer> pngCompressionSpinner = new Spinner<>(0, 9, 6);

    private final Map<String, LayerView> layerViews = new HashMap<>();
    private String selectedLayerId;
    private int mergedCounter = 1;

    public CanvasController(Stage stage, LayerRepository repository, ImageCompositionService imageService) {
        this.stage = stage;
        this.repository = repository;
        this.imageService = imageService;
    }

    public Parent createView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(buildMenuBar());
        root.setLeft(buildSidebar());
        root.setCenter(buildCanvasContainer());
        root.setBottom(buildStatusBar());
        return root;
    }

    public void initialize() {
        dpiSpinner.setEditable(true);
        pngCompressionSpinner.setEditable(true);
        forceDpiCheck.setSelected(true);

        jpegQualitySlider.setShowTickMarks(true);
        jpegQualitySlider.setShowTickLabels(true);
        jpegQualitySlider.setMajorTickUnit(0.1);
        jpegQualitySlider.setMinorTickCount(0);
        jpegQualitySlider.setBlockIncrement(0.05);

        canvasPane.getStyleClass().add("canvas-surface");
        canvasPane.setPrefSize(DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT);
        canvasPane.setOnMouseClicked(event -> {
            if (event.getTarget() == canvasPane) {
                selectLayer(null);
            }
        });

        zoomSlider.setShowTickLabels(false);
        zoomSlider.setShowTickMarks(false);
        zoomSlider.setBlockIncrement(0.05);
        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> applyZoom(newVal.doubleValue(), false));
        applyZoom(1.0, true);
    }

    private MenuBar buildMenuBar() {
        Menu fileMenu = new Menu("File");
        MenuItem newCanvas = new MenuItem("New Canvas");
        newCanvas.setOnAction(event -> clearCanvas());
        MenuItem addImage = new MenuItem("Add Image...");
        addImage.setOnAction(event -> onAddImage());
        MenuItem export = new MenuItem("Export...");
        export.setOnAction(event -> onExportImage());
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> stage.close());
        fileMenu.getItems().addAll(newCanvas, addImage, export, new SeparatorMenuItem(), exit);

        Menu layerMenu = new Menu("Layer");
        MenuItem bringToFront = new MenuItem("Bring To Front");
        bringToFront.setOnAction(event -> onBringToFront());
        MenuItem sendToBack = new MenuItem("Send To Back");
        sendToBack.setOnAction(event -> onSendToBack());
        MenuItem delete = new MenuItem("Delete Selected");
        delete.setOnAction(event -> onDeleteSelectedLayer());
        MenuItem merge = new MenuItem("Merge Layers");
        merge.setOnAction(event -> onMergeLayers());
        layerMenu.getItems().addAll(bringToFront, sendToBack, delete, new SeparatorMenuItem(), merge);

        Menu helpMenu = new Menu("Help");
        MenuItem about = new MenuItem("About");
        about.setOnAction(event -> showInfo(
                "About Image Fusion FX",
                "Simple image compositor with layers, DPI normalization and PNG/JPG export."
        ));
        helpMenu.getItems().add(about);

        return new MenuBar(fileMenu, layerMenu, helpMenu);
    }

    private VBox buildSidebar() {
        Label layersLabel = new Label("Layers (max 4)");
        layersLabel.getStyleClass().add("section-title");
        layersListView.setPrefHeight(230);
        layersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null && !Objects.equals(newItem.id(), selectedLayerId)) {
                selectLayer(newItem.id());
            }
        });

        Button addButton = createActionButton("Add Image", this::onAddImage);
        Button deleteButton = createActionButton("Delete Selected", this::onDeleteSelectedLayer);
        Button frontButton = createActionButton("Bring Front", this::onBringToFront);
        Button backButton = createActionButton("Send Back", this::onSendToBack);
        Button mergeButton = createActionButton("Merge Layers", this::onMergeLayers);
        Button exportButton = createActionButton("Export", this::onExportImage);

        Label dpiLabel = new Label("Target DPI");
        dpiLabel.getStyleClass().add("section-title");

        Label jpegLabel = new Label("JPEG Quality");
        jpegLabel.setTooltip(new Tooltip("Higher value = less compression"));
        Label pngLabel = new Label("PNG Compression (0-9)");
        pngLabel.setTooltip(new Tooltip("Higher value = more compression"));

        VBox controls = new VBox(
                8,
                layersLabel,
                layersListView,
                addButton,
                deleteButton,
                frontButton,
                backButton,
                mergeButton,
                exportButton,
                dpiLabel,
                dpiSpinner,
                forceDpiCheck,
                jpegLabel,
                jpegQualitySlider,
                pngLabel,
                pngCompressionSpinner
        );
        controls.setPadding(new Insets(14));
        controls.setMinWidth(320);
        controls.getStyleClass().add("sidebar");

        return controls;
    }

    private VBox buildCanvasContainer() {
        HBox zoomBar = buildZoomBar();
        canvasScroll.setFitToWidth(false);
        canvasScroll.setFitToHeight(false);
        canvasScroll.setPannable(true);
        canvasScroll.getStyleClass().add("canvas-scroll");
        VBox container = new VBox(8, zoomBar, canvasScroll);
        container.setPadding(new Insets(8, 10, 10, 10));
        container.getStyleClass().add("canvas-container");
        VBox.setVgrow(canvasScroll, Priority.ALWAYS);
        return container;
    }

    private HBox buildZoomBar() {
        Button fitButton = new Button("Fit");
        fitButton.getStyleClass().add("zoom-button");
        fitButton.setOnAction(event -> fitToViewport());

        Button hundredButton = new Button("100%");
        hundredButton.getStyleClass().add("zoom-button");
        hundredButton.setOnAction(event -> applyZoom(1.0, true));

        Label zoomLabel = new Label("Zoom");
        zoomLabel.getStyleClass().add("zoom-label");

        HBox zoomBar = new HBox(8, zoomLabel, fitButton, hundredButton, zoomSlider, zoomValueLabel);
        zoomBar.setAlignment(Pos.CENTER_LEFT);
        zoomBar.getStyleClass().add("zoom-bar");
        HBox.setHgrow(zoomSlider, Priority.ALWAYS);
        return zoomBar;
    }

    private HBox buildStatusBar() {
        Label caption = new Label("Status:");
        caption.setTextFill(Color.web("#555555"));
        HBox statusBar = new HBox(8, caption, statusLabel);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(8, 12, 8, 12));
        statusBar.getStyleClass().add("status-bar");
        return statusBar;
    }

    private Button createActionButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("action-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> action.run());
        return button;
    }

    private void onAddImage() {
        if (repository.size() >= MAX_LAYERS) {
            showWarning("Layer limit reached", "Only 4 layers are allowed.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images (PNG/JPG)", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile == null) {
            return;
        }

        try {
            LoadedImage loadedImage = imageService.loadImage(selectedFile.toPath());
            BufferedImage image = loadedImage.image();
            int sourceDpi = loadedImage.dpi();
            int targetDpi = dpiSpinner.getValue();
            int layerDpi = sourceDpi;

            if (forceDpiCheck.isSelected() && sourceDpi != targetDpi) {
                image = imageService.rescaleToDpi(image, sourceDpi, targetDpi);
                layerDpi = targetDpi;
            }

            Point2D position = calculateNextLayerPosition();
            ImageLayer layer = new ImageLayer(
                    UUID.randomUUID().toString(),
                    "Layer " + (repository.size() + 1),
                    image,
                    position.getX(),
                    position.getY(),
                    layerDpi
            );

            repository.add(layer);
            LayerView layerView = buildLayerView(layer);
            layerViews.put(layer.getId(), layerView);
            canvasPane.getChildren().add(layerView);

            selectLayer(layer.getId());
            ensureCanvasFitsLayers();
            refreshLayerList();
            refreshCanvasOrder();

            statusLabel.setText("Added " + selectedFile.getName() + " (" + layerDpi + " DPI)");
        } catch (IOException | ImageFusionException ex) {
            LOGGER.log(Level.WARNING, "Error adding image", ex);
            showError("Cannot add image", ex.getMessage());
        }
    }

    private LayerView buildLayerView(ImageLayer layer) {
        LayerView layerView = new LayerView(layer.getId(), SwingFXUtils.toFXImage(layer.getImage(), null));
        layerView.relocate(layer.getX(), layer.getY());

        DragContext dragContext = new DragContext();
        layerView.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            selectLayer(layer.getId());
            dragContext.offsetX = event.getX();
            dragContext.offsetY = event.getY();
            event.consume();
        });

        layerView.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            Point2D localPoint = canvasPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            double newX = localPoint.getX() - dragContext.offsetX;
            double newY = localPoint.getY() - dragContext.offsetY;
            moveLayer(layer.getId(), newX, newY);
            event.consume();
        });

        layerView.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            ensureCanvasFitsLayers();
            refreshLayerList();
            statusLabel.setText("Layer moved");
            event.consume();
        });

        layerView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            selectLayer(layer.getId());
            event.consume();
        });
        return layerView;
    }

    private void moveLayer(String layerId, double x, double y) {
        repository.findById(layerId).ifPresent(layer -> {
            layer.setX(x);
            layer.setY(y);
            LayerView view = layerViews.get(layerId);
            if (view != null) {
                view.relocate(x, y);
            }
            ensureCanvasFitsLayers();
        });
    }

    private void ensureCanvasFitsLayers() {
        List<ImageLayer> layers = repository.findAll();
        if (layers.isEmpty()) {
            canvasPane.setPrefSize(DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT);
            return;
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = 0;
        double maxY = 0;
        for (ImageLayer layer : layers) {
            minX = Math.min(minX, layer.getX());
            minY = Math.min(minY, layer.getY());
            maxX = Math.max(maxX, layer.getX() + layer.getWidth());
            maxY = Math.max(maxY, layer.getY() + layer.getHeight());
        }

        double shiftX = minX < 0 ? -minX + CANVAS_MARGIN : 0;
        double shiftY = minY < 0 ? -minY + CANVAS_MARGIN : 0;

        if (shiftX > 0 || shiftY > 0) {
            for (ImageLayer layer : layers) {
                layer.setX(layer.getX() + shiftX);
                layer.setY(layer.getY() + shiftY);
                LayerView layerView = layerViews.get(layer.getId());
                if (layerView != null) {
                    layerView.relocate(layer.getX(), layer.getY());
                }
            }
            maxX += shiftX;
            maxY += shiftY;
        }

        double requiredWidth = Math.max(DEFAULT_CANVAS_WIDTH, maxX + CANVAS_MARGIN);
        double requiredHeight = Math.max(DEFAULT_CANVAS_HEIGHT, maxY + CANVAS_MARGIN);
        canvasPane.setPrefSize(requiredWidth, requiredHeight);
    }

    private void onDeleteSelectedLayer() {
        if (selectedLayerId == null) {
            showWarning("No layer selected", "Select a layer to delete.");
            return;
        }

        repository.removeById(selectedLayerId);
        LayerView removedView = layerViews.remove(selectedLayerId);
        if (removedView != null) {
            canvasPane.getChildren().remove(removedView);
        }

        selectedLayerId = null;
        ensureCanvasFitsLayers();
        refreshLayerList();
        refreshCanvasOrder();
        statusLabel.setText("Layer deleted");
    }

    private void onBringToFront() {
        if (!validateSelectedLayer("bring to front")) {
            return;
        }
        repository.bringToFront(selectedLayerId);
        refreshCanvasOrder();
        refreshLayerList();
        statusLabel.setText("Layer moved to front");
    }

    private void onSendToBack() {
        if (!validateSelectedLayer("send to back")) {
            return;
        }
        repository.sendToBack(selectedLayerId);
        refreshCanvasOrder();
        refreshLayerList();
        statusLabel.setText("Layer moved to back");
    }

    private void onMergeLayers() {
        List<ImageLayer> layers = repository.findAll();
        if (layers.isEmpty()) {
            showWarning("No layers", "Add at least one image before merging.");
            return;
        }

        try {
            BufferedImage mergedImage = imageService.mergeLayers(layers);
            clearCanvas();
            ImageLayer mergedLayer = new ImageLayer(
                    UUID.randomUUID().toString(),
                    "Merged Layer " + mergedCounter++,
                    mergedImage,
                    CANVAS_MARGIN,
                    CANVAS_MARGIN,
                    dpiSpinner.getValue()
            );
            repository.add(mergedLayer);

            LayerView mergedLayerView = buildLayerView(mergedLayer);
            layerViews.put(mergedLayer.getId(), mergedLayerView);
            canvasPane.getChildren().add(mergedLayerView);

            selectLayer(mergedLayer.getId());
            ensureCanvasFitsLayers();
            refreshLayerList();
            statusLabel.setText("Layers merged into one");
        } catch (ImageFusionException ex) {
            showError("Cannot merge layers", ex.getMessage());
        }
    }

    private void onExportImage() {
        List<ImageLayer> layers = repository.findAll();
        if (layers.isEmpty()) {
            showWarning("No content", "There is no image to export.");
            return;
        }

        BufferedImage mergedImage;
        try {
            mergedImage = imageService.mergeLayers(layers);
        } catch (ImageFusionException ex) {
            showError("Cannot export image", ex.getMessage());
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export image");
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG (*.png)", "*.png");
        FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("JPG (*.jpg)", "*.jpg");
        fileChooser.getExtensionFilters().addAll(pngFilter, jpgFilter);
        fileChooser.setSelectedExtensionFilter(pngFilter);
        File rawFile = fileChooser.showSaveDialog(stage);
        if (rawFile == null) {
            return;
        }

        File targetFile = ensureOutputExtension(rawFile, fileChooser.getSelectedExtensionFilter());
        String format = resolveOutputFormat(targetFile);
        ExportOptions options;
        try {
            options = new ExportOptions(
                    format,
                    new CompressionOptions((float) jpegQualitySlider.getValue(), pngCompressionSpinner.getValue()),
                    dpiSpinner.getValue(),
                    true
            );
        } catch (IllegalArgumentException ex) {
            showError("Invalid export settings", ex.getMessage());
            return;
        }

        try {
            imageService.exportImage(mergedImage, targetFile.toPath(), options);
            statusLabel.setText("Exported image: " + targetFile.getName());
            showInfo("Export completed", "Image exported successfully to:\n" + targetFile.getAbsolutePath());
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error exporting image", ex);
            showError("Export failed", ex.getMessage());
        }
    }

    private void clearCanvas() {
        repository.clear();
        layerViews.clear();
        canvasPane.getChildren().clear();
        selectedLayerId = null;
        canvasPane.setPrefSize(DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT);
        applyZoom(1.0, true);
        refreshLayerList();
        statusLabel.setText("Canvas reset");
    }

    private void refreshLayerList() {
        ObservableList<LayerDto> items = FXCollections.observableArrayList();
        for (ImageLayer layer : repository.findAll()) {
            items.add(LayerDto.from(layer));
        }
        layersListView.setItems(items);

        if (selectedLayerId != null) {
            for (int i = 0; i < items.size(); i++) {
                if (Objects.equals(items.get(i).id(), selectedLayerId)) {
                    layersListView.getSelectionModel().select(i);
                    break;
                }
            }
        }
    }

    private void refreshCanvasOrder() {
        List<LayerView> orderedViews = repository.findAll().stream()
                .map(layer -> layerViews.get(layer.getId()))
                .filter(Objects::nonNull)
                .toList();
        canvasPane.getChildren().setAll(orderedViews);
        applySelectionState();
    }

    private void selectLayer(String layerId) {
        selectedLayerId = layerId;
        applySelectionState();
        if (layerId == null) {
            layersListView.getSelectionModel().clearSelection();
            return;
        }
        ObservableList<LayerDto> items = layersListView.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (Objects.equals(items.get(i).id(), layerId)) {
                layersListView.getSelectionModel().select(i);
                break;
            }
        }
    }

    private void applySelectionState() {
        for (Map.Entry<String, LayerView> entry : layerViews.entrySet()) {
            entry.getValue().setSelected(Objects.equals(entry.getKey(), selectedLayerId));
        }
    }

    private Point2D calculateNextLayerPosition() {
        if (repository.size() == 0) {
            return new Point2D(CANVAS_MARGIN, CANVAS_MARGIN);
        }
        double maxX = repository.findAll().stream()
                .mapToDouble(layer -> layer.getX() + layer.getWidth())
                .max()
                .orElse(CANVAS_MARGIN);
        return new Point2D(maxX + CANVAS_MARGIN, CANVAS_MARGIN);
    }

    private boolean validateSelectedLayer(String action) {
        if (selectedLayerId == null) {
            showWarning("No layer selected", "Select a layer to " + action + ".");
            return false;
        }
        return true;
    }

    private File ensureOutputExtension(File file, FileChooser.ExtensionFilter filter) {
        String name = file.getName().toLowerCase(Locale.ROOT);
        if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return file;
        }
        String extension = filter != null && filter.getDescription().toLowerCase(Locale.ROOT).contains("jpg")
                ? ".jpg"
                : ".png";
        return new File(file.getAbsolutePath() + extension);
    }

    private String resolveOutputFormat(File file) {
        String name = file.getName().toLowerCase(Locale.ROOT);
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "jpg";
        }
        return "png";
    }

    private void applyZoom(double zoom, boolean syncSlider) {
        double safeZoom = Math.max(0.10, Math.min(5.0, zoom));
        canvasPane.setScaleX(safeZoom);
        canvasPane.setScaleY(safeZoom);
        zoomValueLabel.setText("%d%%".formatted((int) Math.round(safeZoom * 100)));
        if (syncSlider && Math.abs(zoomSlider.getValue() - safeZoom) > 0.0001) {
            zoomSlider.setValue(safeZoom);
        }
    }

    private void fitToViewport() {
        double viewportWidth = canvasScroll.getViewportBounds().getWidth();
        double viewportHeight = canvasScroll.getViewportBounds().getHeight();
        if (viewportWidth <= 0 || viewportHeight <= 0) {
            return;
        }

        double contentWidth = canvasPane.getPrefWidth();
        double contentHeight = canvasPane.getPrefHeight();
        if (contentWidth <= 0 || contentHeight <= 0) {
            return;
        }

        double scaleX = viewportWidth / contentWidth;
        double scaleY = viewportHeight / contentHeight;
        double fitScale = Math.min(scaleX, scaleY) * 0.96;
        applyZoom(fitScale, true);
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static final class DragContext {
        private double offsetX;
        private double offsetY;
    }
}

