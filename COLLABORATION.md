# Collaboration Guide

## Objetivo

Mantener un flujo de trabajo predecible para cambios de UI, servicios de imagen
y exportación.

## Roles sugeridos

- **UI owner**: JavaFX, controles, layout y experiencia de usuario.
- **Core owner**: composición, DPI, compresión y exportación.
- **QA owner**: pruebas manuales y unitarias.

## Acuerdos de colaboración

- Diseñar primero la interfaz de cambios en `service` antes de tocar UI.
- Evitar acoplar lógica de negocio dentro de `controller`.
- Todo cambio de formato/exportación debe incluir test.
- Si una mejora rompe compatibilidad, documentarla en `CHANGELOG.md`.

## Pull request checklist

- [ ] Compila y pasa tests con `mvn clean test`
- [ ] Incluye pruebas para comportamiento nuevo
- [ ] Mantiene límite de 4 capas y reglas de exportación
- [ ] Documentación actualizada si cambió comportamiento
- [ ] Mantiene consistencia legal con `LICENSE`, `NOTICE` y `AUTHORS`
