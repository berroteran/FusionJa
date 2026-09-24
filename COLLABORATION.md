# Guía de colaboración

## Objetivo

Mantener un flujo de trabajo predecible para cambios de UI, servicios de imagen
y exportación.

## Roles sugeridos

- **UI owner**: JavaFX, controles, layout y experiencia de usuario.
- **Core owner**: composición, DPI, compresión y exportación.
- **QA owner**: pruebas manuales y unitarias.

## Acuerdos de colaboración

- Aplicar las convenciones y el flujo de [AGENTS.md](AGENTS.md), sin duplicar sus reglas en otras guías.
- Acordar primero los contratos de los casos de uso del componente afectado antes de implementar sus adaptadores de UI, CLI o servicios.
- Todo cambio de formato/exportación debe incluir test.
- Si una mejora rompe compatibilidad, documentarla en `CHANGELOG.md`.

## Lista de revisión de pull request

- [ ] Compila y pasa tests con `mvn clean test`
- [ ] Pasa `mvn checkstyle:check`
- [ ] Respeta los límites de componentes y la dirección de dependencias de `AGENTS.md`
- [ ] Verifica temas e idiomas o contratos de CLI cuando corresponda
- [ ] Incluye pruebas para comportamiento nuevo
- [ ] Mantiene límite de 4 capas y reglas de exportación
- [ ] Documentación actualizada si cambió comportamiento
- [ ] Mantiene consistencia legal con `LICENSE`, `NOTICE` y `AUTHORS`
