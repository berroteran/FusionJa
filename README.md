# Fusion-Ja!

Fusion-Ja! es una aplicación desktop en JavaFX para componer imágenes PNG/JPG mediante capas, ajustar DPI, fusionar el resultado y exportarlo como una imagen final.

El proyecto está orientado a una herramienta simple, mantenible y operativamente ligera: sin base de datos, sin dependencias de UI adicionales y con lógica de negocio separada de la interfaz.

## Estado técnico

- Aplicación desktop JavaFX.
- Proyecto Maven con Java 21.
- JavaFX `21.0.11`.
- JUnit 5 para pruebas unitarias.
- Checkstyle para reglas de estilo y longitud de línea.
- Licencia Apache License 2.0.
- Nombre comercial: Fusion-Ja!
- Autor y mantenedor principal: Omar Berroterán Silva.

## Funcionalidad principal

- Lienzo inicial en blanco de `900x600 px`.
- Importación de imágenes PNG, JPG y JPEG.
- Importación múltiple desde el diálogo `Add Image`.
- Drag and drop de imágenes sobre la barra lateral.
- Máximo de 4 capas activas.
- Selección visual de capas en el lienzo.
- Listado lateral sincronizado con la selección actual.
- Movimiento libre de capas con mouse.
- Expansión automática del lienzo cuando una capa excede los límites visibles.
- Reubicación automática cuando una capa se mueve a coordenadas negativas.
- Ordenamiento de capas:
  - Bring To Front.
  - Send To Back.
- Eliminación de capa seleccionada.
- Fusión de capas visibles en una sola capa.
- Exportación a PNG o JPG.
- Compresión configurable:
  - JPEG quality entre `0.1` y `1.0`.
  - PNG compression level entre `0` y `9`.
- Lectura básica de DPI en PNG/JPG.
- Reescalado opcional al importar para forzar DPI objetivo.
- Escritura de metadatos DPI al exportar.
- Conversión de transparencia a fondo blanco al exportar JPG.

## Mejoras recientes

- Se agregó `ImageFusionLauncher` como entrypoint de bootstrap para JavaFX.
- Se actualizó JavaFX de `21.0.6` a `21.0.11`.
- En Windows, el launcher desactiva `prism.d3d` por defecto para mitigar inestabilidades del pipeline D3D.
- Se permite override manual de `prism.order` o `prism.d3d` mediante VM options.
- Se agregó importación múltiple de imágenes respetando el límite de 4 capas.
- Se agregó soporte de drag and drop para PNG/JPG/JPEG.
- Las imágenes importadas usan posicionamiento inicial consistente en el margen del lienzo.
- Al importar, el zoom se ajusta automáticamente al viewport disponible.
- El lienzo ahora se escala mediante un `Group`, evitando problemas de layout al aplicar zoom.
- Se agregó viewport centrado con padding visual alrededor del lienzo.
- Se agregó botón `Fit` para ajustar el zoom al viewport y botón `100%` para volver al tamaño real.
- Se acotó el zoom manual entre `10%` y `400%`; el ajuste automático puede bajar hasta `1%` para imágenes grandes.
- La barra de estado muestra tamaño del canvas, peso estimado en memoria y zoom actual.
- Se mejoró el estilo visual de botones, listas, scrollbars, viewport y estado de drop activo.
- Se incorporó estructura de skills portable para IDEs y agentes.
- Se agregó skill de política de commits.
- Se agregó base para análisis del código con skills `understand-*`.

## Arquitectura

El código mantiene separación por capas:

- `com.imagefusion`: arranque de aplicación y launcher.
- `com.imagefusion.controller`: control de UI, eventos y coordinación de acciones del usuario.
- `com.imagefusion.service`: reglas de composición, DPI, compresión y exportación de imágenes.
- `com.imagefusion.repository`: almacenamiento de capas; actualmente en memoria.
- `com.imagefusion.model`: entidades del dominio.
- `com.imagefusion.dto`: datos transferidos entre capas.
- `com.imagefusion.exception`: excepciones propias del dominio.
- `com.imagefusion.ui`: componentes visuales reutilizables como splash y vista de capa.

### Componentes relevantes

- `ImageFusionLauncher`: entrypoint recomendado para Maven/IDE y configuración defensiva del pipeline gráfico.
- `ImageFusionApplication`: inicializa splash, escena principal, estilos y dependencias concretas.
- `CanvasController`: construye la UI, gestiona capas, importación, drag and drop, zoom, exportación y estado.
- `ImageCompositionService`: carga imágenes, lee DPI, reescala, fusiona capas y exporta PNG/JPG.
- `LayerRepository`: contrato para persistencia de capas.
- `InMemoryLayerRepository`: implementación simple en memoria.
- `LayerView`: representación visual de cada capa con borde de selección.

## Flujo de uso

1. Ejecutar la aplicación.
2. Agregar imágenes con `Add Image` o arrastrándolas sobre la barra lateral.
3. Seleccionar capas desde el lienzo o desde la lista lateral.
4. Mover capas con el mouse.
5. Reordenar capas con `Bring Front` o `Send Back`.
6. Ajustar DPI, calidad JPEG o compresión PNG según corresponda.
7. Fusionar capas si se necesita consolidar el contenido.
8. Exportar como PNG o JPG.

## Requisitos

- Java 21.
- Maven 3.9+.
- Sistema con soporte gráfico para JavaFX.

En JDK 24+ con ejecución por classpath puede ser necesario agregar:

```text
--enable-native-access=ALL-UNNAMED
```

## Ejecución

```bash
mvn clean javafx:run
```

En IDE, ejecutar:

```text
com.imagefusion.ImageFusionLauncher
```

Si necesitas forzar otro pipeline gráfico de JavaFX, puedes usar VM options explícitas. Ejemplos:

```text
-Dprism.order=sw
-Dprism.d3d=false
```

## Validación

Ejecutar pruebas:

```bash
mvn clean test
```

Validar estilo:

```bash
mvn checkstyle:check
```

El proyecto incluye pruebas unitarias para:

- Cálculo de bounds globales al fusionar capas.
- Reescalado por DPI.
- Exportación PNG y JPG.

## Estructura del repositorio

```text
.
├── config/checkstyle/checkstyle.xml
├── skills/
├── src/main/java/com/imagefusion/
├── src/main/resources/styles/main.css
├── src/test/java/com/imagefusion/
├── AGENTS.md
├── CHANGELOG.md
├── CONTRIBUTING.md
├── LICENSE
├── NOTICE
├── README.md
└── pom.xml
```

## Skills y automatización asistida

El repositorio incluye un workspace `skills/` para flujos portables entre IDEs/agentes.

Skills principales:

- `cross-ide-skill-template`: plantilla para crear skills nuevas.
- `commit-policy`: política de commits con formato controlado.
- `understand-*`: base para análisis, explicación, onboarding y visualización del código.

Crear una skill nueva:

```bash
python skills/cross-ide-skill-template/scripts/create_skill_structure.py --name my-skill --out skills
```

Validar una skill:

```bash
python skills/cross-ide-skill-template/scripts/validate_skill_structure.py --skill-dir skills/my-skill
```

## Directiva de código

- Variables, métodos, clases y tablas en inglés.
- Comentarios y JavaDocs en español técnico.
- Longitud máxima por línea: 200 caracteres.
- Aplicar Clean Code, alta cohesión, SRP y principios SOLID.
- Mantener separación clara entre controlador, servicio, repositorio, DTO y modelo.
- Evitar acoplamiento innecesario entre UI y lógica de negocio.
- Ejecutar `mvn clean test` antes de abrir PR.
- Ejecutar `mvn checkstyle:check` para validar estilo.

## Consideraciones técnicas

- El repositorio de capas es en memoria; no hay persistencia entre ejecuciones.
- El límite de 4 capas es una regla de producto aplicada en UI.
- Los formatos soportados de entrada son PNG/JPG/JPEG.
- Los formatos soportados de salida son PNG/JPG.
- JPG no soporta alpha; por eso se aplana sobre fondo blanco durante la exportación.
- La estimación de peso del canvas en la barra de estado usa `width * height * 4 bytes`; no representa el tamaño final comprimido.
- El reescalado por DPI modifica dimensiones en píxeles según la relación `targetDpi / sourceDpi`.
- Si una imagen no contiene metadatos DPI legibles, se usa DPI por defecto.
- La composición final calcula bounds globales de las capas visibles para evitar recortes.
- En Windows, `prism.d3d=false` se aplica solo si el usuario no define manualmente `prism.order` o `prism.d3d`.

## Documentación relacionada

- `CHANGELOG.md`: historial de cambios.
- `CONTRIBUTING.md`: guía de contribución.
- `COLLABORATION.md`: convenciones de colaboración.
- `CODE_OF_CONDUCT.md`: reglas de convivencia técnica.
- `SECURITY.md`: política de reporte de seguridad.
- `AUTHORS`: autoría y mantenimiento.
- `NOTICE`: avisos y atribución.
- `LICENSE`: Apache License 2.0.

## Licencia

Este proyecto está licenciado bajo Apache License 2.0. Consulta `LICENSE` y `NOTICE` para el detalle legal y de atribución.
