# Fusion-Ja!

Aplicación desktop en JavaFX para combinar imágenes PNG/JPG con capas (máximo 4), moverlas libremente, forzar DPI, fusionarlas y exportar como una sola imagen.

## Autor

- Omar Berroterán Silva

## Requisitos

- Java 21
- Maven 3.9+

## Ejecutar

```bash
mvn clean javafx:run
```

## Ejecutar tests

```bash
mvn test
```

## Funcionalidad principal

- Lienzo inicial en blanco.
- Carga de imágenes PNG/JPG.
- Hasta 4 capas.
- Arrastre libre de capas con expansión automática del lienzo.
- Orden de capas (frente/fondo).
- Fusión de capas en una sola.
- Exportación PNG o JPG.
- Compresión configurable:
  - JPEG calidad `0.1` a `1.0`.
  - PNG nivel `0` a `9`.
- Escritura de DPI en metadatos de exportación.

## Estructura

- `com.imagefusion.controller`: lógica de interfaz y eventos.
- `com.imagefusion.service`: composición, DPI y exportación.
- `com.imagefusion.repository`: almacenamiento en memoria de capas.
- `com.imagefusion.model` y `com.imagefusion.dto`: entidades y opciones.
- `com.imagefusion.ui`: componentes visuales (splash y vista de capa).

## Documentación de proyecto

- `LICENSE`: licencia Apache License 2.0.
- `NOTICE`: avisos y atribución del proyecto.
- `CONTRIBUTING.md`: guía para contribuir.
- `COLLABORATION.md`: convenciones de trabajo colaborativo.
- `CODE_OF_CONDUCT.md`: reglas de convivencia técnica.
- `SECURITY.md`: política de reporte de seguridad.
- `CHANGELOG.md`: historial de cambios.

## Licencia

Este proyecto está licenciado bajo Apache License 2.0.
Consulta `LICENSE` y `NOTICE` para el detalle legal y de atribución.
