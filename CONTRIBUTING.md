# Guía de contribución

Gracias por contribuir a **Fusion-Ja!**.

## Flujo recomendado

1. Crea una rama desde `main`:
   - `feature/<short-name>`
   - `fix/<short-name>`
2. Implementa cambios pequeños y coherentes.
3. Ejecuta validaciones locales:
   - `mvn clean test`
   - `mvn checkstyle:check`
4. Abre Pull Request con:
   - contexto del problema
   - solución aplicada
   - evidencia de pruebas
   - screenshots si cambia UI

## Estándares técnicos

Seguir [AGENTS.md](AGENTS.md), fuente de referencia para las convenciones técnicas y la forma de trabajo.
Antes de implementar, identificar el dominio o componente propietario y sus contratos. La estructura existente por capas debe evolucionar incrementalmente hacia los límites allí definidos.

## Licencia de contribuciones

Al enviar contribuciones a este repositorio, aceptas que se distribuyan bajo
Apache License 2.0 y conserven los avisos de `LICENSE` y `NOTICE`.
También aceptas preservar las atribuciones indicadas en `AUTHORS`.

## Reglas de calidad

- Sin warnings críticos en compilación.
- Sin violaciones de Checkstyle (`mvn checkstyle:check`).
- Tests unitarios para lógica de negocio nueva o modificada.
- Mantener compatibilidad con PNG/JPG, capas y exportación.

## Commits

Usar mensajes claros y orientados a impacto:

- `feat: add layer lock action`
- `fix: preserve alpha on merge preview`
- `test: cover dpi normalization edge case`
