---
name: commit-policy
description: Redactar y validar mensajes de commit estilo kernel con formato 50/72, titulo en imperativo, linea en blanco obligatoria, cuerpo centrado en que y por que, referencias Fixes con SHA-1 de al menos 12 caracteres y etiqueta Assisted-by. Usar cuando Codex deba escribir, revisar o corregir commit messages, incluidas limpiezas de historial antes de PR.
---

# Commit Policy

Autor: Omar Berroterán Silva.

## Objetivo

Generar mensajes de commit autocontenidos, escaneables y auditables.
Mantener un cambio funcional por commit.

## Flujo

1. Delimitar un alcance unico por commit.
2. Escribir titulo imperativo con maximo 50 caracteres.
3. Dejar segunda linea en blanco.
4. Escribir cuerpo tecnico con lineas de maximo 72 caracteres.
5. Explicar problema, impacto y razon del cambio.
6. Omitir detalles de implementacion evidentes en el codigo.
7. Agregar referencias con `Fixes:` cuando aplique.
8. Cerrar siempre con `Assisted-by:`.
9. No usar `Signed-off-by:` generado por IA.

## Reglas Obligatorias

- Usar el formato de titulo: `[Component/Module]: Imperative summary`.
- Capitalizar la primera letra del titulo.
- No terminar el titulo con punto.
- Mantener la linea 2 completamente vacia.
- Limitar todas las lineas del cuerpo a 72 caracteres.
- Incluir `Assisted-by: <Model or Tool>` como ultima linea no vacia.
- Si hay `Fixes:`, usar `Fixes: <sha12+> ("<commit title>")`.
- No incluir `Signed-off-by:` en mensajes generados por IA.

## Plantilla

```text
[Component/Module]: Imperative summary under 50 chars

Explain the original problem context and user or system impact.
Describe what changed and why this approach is safer or clearer.
Mention design tradeoffs only when they affect maintenance.

Fixes: a1b2c3d4e5f6 ("Original commit title")
Assisted-by: OpenAI GPT-5
```

## Checklist de Validacion

- Titulo <= 50 caracteres.
- Titulo en imperativo, con formato `[Component/Module]: ...`.
- Linea 2 vacia.
- Cuerpo con lineas <= 72 caracteres.
- Referencia `Fixes:` valida cuando aplique.
- Ultima linea `Assisted-by:` presente.
- Sin `Signed-off-by:`.

## Preguntas Minimas si Falta Contexto

- Cual era el problema observable antes del cambio.
- Cual es el impacto tecnico o de usuario que corrige.
- Si existe commit previo para relacionar con `Fixes:`.

