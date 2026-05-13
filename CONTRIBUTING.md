# Contributing

Gracias por contribuir a **Fusion-Ja!**.

## Flujo recomendado

1. Crea una rama desde `main`:
   - `feature/<short-name>`
   - `fix/<short-name>`
2. Implementa cambios pequeños y coherentes.
3. Ejecuta validaciones locales:
   - `mvn clean test`
4. Abre Pull Request con:
   - contexto del problema
   - solución aplicada
   - evidencia de pruebas
   - screenshots si cambia UI

## Estándares técnicos

- Java 21 y Maven.
- Arquitectura por capas (`controller`, `service`, `repository`, `dto/model`).
- Nombres en inglés para clases, métodos y variables.
- Manejo explícito de errores con mensajes claros.
- Logging útil para diagnóstico.
- Evitar dependencias innecesarias y hacks frágiles.

## Licencia de contribuciones

Al enviar contribuciones a este repositorio, aceptas que se distribuyan bajo
Apache License 2.0 y conserven los avisos de `LICENSE` y `NOTICE`.

## Reglas de calidad

- Sin warnings críticos en compilación.
- Tests unitarios para lógica de negocio nueva o modificada.
- Mantener compatibilidad con PNG/JPG, capas y exportación.

## Commits

Usar mensajes claros y orientados a impacto:

- `feat: add layer lock action`
- `fix: preserve alpha on merge preview`
- `test: cover dpi normalization edge case`
