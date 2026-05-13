# Directiva General del Proyecto

## Reglas de código

1. Variables y métodos en inglés.
2. Comentarios y JavaDocs en español técnico.
3. Longitud máxima por línea de código: 200 caracteres.
4. Aplicar buenas prácticas de Java: Clean Code, alta cohesión, responsabilidad única (SRP) y principios SOLID.

## Reglas de diseño

- Mantener separación por capas (`controller`, `service`, `repository`, `dto/model`).
- Evitar acoplamiento innecesario entre UI y lógica de negocio.
- Priorizar legibilidad, mantenibilidad y simplicidad operativa.

## Validación

- Ejecutar `mvn clean test` antes de abrir PR.
- Ejecutar `mvn checkstyle:check` para validar estilo y longitud de línea.
