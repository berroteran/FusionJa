# Instrucciones de agentes y forma de trabajo

Este documento es la fuente de referencia para las convenciones del proyecto y aplica a código, pruebas, documentación y cambios realizados por agentes o colaboradores.

## Stack y criterios generales

- Revisar `pom.xml`, el código y la configuración antes de proponer cambios. El stack actual es Java 21, JavaFX, Maven, JUnit 5 y Checkstyle.
- Mantener compatibilidad con el stack existente. No incorporar frameworks o dependencias sin una necesidad concreta y justificada.
- Priorizar mantenibilidad, seguridad, legibilidad, rendimiento razonable y simplicidad operativa.
- Aplicar Clean Code, SOLID, responsabilidad única (SRP), alta cohesión, bajo acoplamiento y composición antes que herencia innecesaria.
- Reutilizar comportamiento y componentes cuando compartan una responsabilidad real; evitar duplicación y abstracciones prematuras.

## Nombres, comentarios y documentación

- Usar inglés para paquetes, clases, interfaces, métodos, variables, constantes, propiedades y claves de recursos.
- Elegir nombres representativos de la intención y del dominio; evitar abreviaturas ambiguas y nombres genéricos como `data`, `manager` o `helper` sin contexto.
- Escribir comentarios, JavaDocs, documentación técnica y explicaciones de trabajo en español técnico.
- Los comentarios deben explicar decisiones, restricciones o motivos; evitar describir literalmente lo que ya expresa el código.
- Documentar contratos públicos, validaciones, errores y efectos secundarios relevantes. Actualizar la documentación junto con el comportamiento.
- Mantener un máximo de 200 caracteres por línea de código y respetar Checkstyle.
- Separar el idioma de documentación del idioma de presentación: los textos para usuarios se resuelven mediante recursos de localización.

## Organización por dominio y componente

Organizar primero por dominio o componente funcional. Dentro de cada componente, separar las capas que realmente necesite (`service`, `repository`, `model`, `dto`, etc.).
No usar paquetes globales por tipo técnico como destino de toda funcionalidad nueva.

Estructura de referencia bajo `com.imagefusion`:

```text
bootstrap/                 Arranque y ensamblado de dependencias y adaptadores.
core/                      Dominio y casos de uso independientes de la interfaz.
  composition/             Composición, capas y reglas relacionadas.
  export/                  Casos de uso y contratos de exportación.
security/                  Políticas transversales de autorización y protección, si se necesitan.
identity/                  Autenticación y ciclo de vida de sesiones, si se necesitan.
  authentication/
  session/
infrastructure/            Adaptadores de archivos, codecs, persistencia e integración externa.
ui/                        Presentación JavaFX y adaptación de acciones a casos de uso.
  canvas/                  Pantallas y controladores del lienzo.
  components/              Controles y elementos visuales reutilizables.
  theme/                   Gestión y aplicación de temas.
  i18n/                    Selección de idioma y resolución de recursos.
cli/                       Comandos, argumentos, entrada/salida y códigos de salida.
```

- Esta estructura es una guía de responsabilidades, no una obligación de crear paquetes vacíos o funcionalidades no solicitadas.
- `core` no es un contenedor de utilidades generales. Cada clase debe pertenecer a un dominio o componente identificable.
- Mantener autenticación y sesiones separadas de políticas de seguridad transversales y de la presentación.
- Ubicar modelos, DTO, contratos de repositorio y excepciones junto al componente propietario; evitar dependencias cíclicas entre componentes.
- Exponer contratos explícitos entre componentes y reducir la visibilidad de detalles internos.
- El código existente conserva temporalmente paquetes globales por capas. Migrar de forma incremental al intervenir el componente y documentar lo pendiente.
- No presentar esta estructura objetivo como si toda la migración ya estuviera implementada.

## Desacople y dirección de dependencias

- UI, CLI y futuros adaptadores de servicios deben invocar los mismos casos de uso del core, sin duplicar reglas de negocio.
- El core no debe depender de `ui`, `cli`, JavaFX, controles visuales, diálogos, propiedades observables de UI ni del arranque de la aplicación.
- Mantener las reglas e invariantes del dominio en el core; las validaciones de UI o CLI complementan esas reglas, pero no las sustituyen.
- Los controladores y comandos adaptan entradas, invocan casos de uso y presentan resultados; no implementan composición, exportación ni persistencia.
- Definir puertos o interfaces en el componente consumidor cuando se requiera invertir dependencias de E/S o integraciones; implementarlos en adaptadores externos.
- Ensamblar dependencias en `bootstrap` con inyección por constructor; evitar estado global mutable, localizadores de servicios y singletons innecesarios.
- Mantener los contratos independientes de tipos de presentación. Usar DTO o modelos propios e inmutables cuando aporte claridad.
- Poder ejecutar y probar casos de uso sin iniciar JavaFX, abrir ventanas ni depender de una sesión gráfica.
- Los tipos de procesamiento de imágenes del JDK, como `BufferedImage`, no son controles visuales; evaluar su uso por responsabilidad y compatibilidad headless.
- Preferir implementaciones sencillas; no crear una interfaz por clase ni añadir capas que solo deleguen sin aportar un límite útil.

## UI: temas, idiomas y reutilización

- Toda UI nueva o modificada debe admitir inglés (`en`) y español (`es`) y respetar el sistema de temas.
- Externalizar etiquetas, menús, botones, diálogos, ayudas, accesibilidad y mensajes visibles en recursos UTF-8 mediante `ResourceBundle` o el mecanismo central del proyecto.
- Mantener las mismas claves en ambos idiomas, usar parámetros para mensajes y evitar concatenaciones que impidan una traducción correcta.
- Centralizar la selección de `Locale`, el idioma predeterminado y el fallback. Formatear números y fechas según el idioma seleccionado.
- Mantener formatos de archivos y contratos técnicos independientes del idioma de presentación.
- Permitir cambiar idioma y tema desde la UI y conservar la preferencia, con un valor predeterminado documentado cuando no exista configuración.
- Reutilizar y evolucionar el soporte de temas existente; mantener los modos claro, oscuro y del sistema y un fallback si no se puede detectar este último.
- Centralizar estilos, colores y tokens en CSS y recursos de tema; evitar colores o estilos incrustados en controladores que bloqueen el cambio de tema.
- Colocar componentes visuales reutilizables en `ui.components`, con contratos pequeños, sin depender de pantallas concretas ni ejecutar reglas de negocio.
- Mantener operaciones costosas fuera del hilo de JavaFX y aplicar actualizaciones visuales en el hilo correcto; prever errores, progreso y cancelación cuando corresponda.
- Verificar legibilidad, contraste, navegación por teclado y textos largos en ambos idiomas y temas.

## CLI: convenciones Unix/Linux

Estas reglas aplican cuando se implemente o modifique una CLI; documentar sus convenciones sin afirmar conformidad POSIX completa si no se ha verificado.

- Mantener comandos en `cli` y ejecutarlos sin inicializar JavaFX ni requerir escritorio.
- Usar nombres en inglés y sintaxis consistente: `command [options] [operands]`; para varias operaciones, subcomandos claros y coherentes.
- Seguir las convenciones de utilidades POSIX para opciones cortas y operandos; admitir `--` para terminar las opciones y rutas que comiencen con `-`.
- Ofrecer opciones largas descriptivas como extensión GNU cuando corresponda, incluyendo `--help` y `--version`, con salida exitosa.
- Enviar resultados a `stdout`; diagnósticos, progreso y errores a `stderr`, sin contaminar salidas destinadas a scripts o tuberías.
- Devolver `0` en éxito y códigos distintos de cero para errores; documentar su significado y limitar `System.exit` al punto de entrada de la CLI.
- Validar argumentos y combinaciones incompatibles antes de producir efectos secundarios; dar errores accionables y ayuda de uso breve.
- Admitir `stdin`, `stdout` y el operando `-` cuando la operación permita streaming; documentar restricciones de formatos que necesiten acceso aleatorio.
- No solicitar interacción en ejecuciones no interactivas; ofrecer opciones explícitas para decisiones necesarias y proteger contra sobrescrituras accidentales.
- Evitar colores ANSI, animaciones o progreso en salida redirigida; mantener formatos procesables estables e independientes de textos traducidos.
- Manejar recursos, cancelación y fallos de E/S de forma controlada; no mostrar trazas internas por defecto ni construir comandos de shell con entrada del usuario.

## Validación, errores y seguridad

- Validar entradas en los límites del sistema y preservar invariantes en el dominio; usar excepciones específicas y conservar la causa original.
- Traducir errores del core a mensajes localizados en UI o diagnósticos y códigos de salida en CLI; no incluir diálogos ni terminación de procesos dentro del core.
- Añadir logging útil con contexto, sin credenciales, tokens, datos sensibles ni duplicación innecesaria del mismo error entre capas.
- Cerrar recursos con mecanismos seguros, controlar tamaños y consumo de memoria y evitar escrituras parciales o pérdida accidental de archivos.
- Si se incorpora identidad, separar autenticación de autorización y centralizar caducidad e invalidación de sesiones; no implementar criptografía propia.

## Forma de trabajo y comprobaciones

1. Leer estas instrucciones, revisar el estado del repositorio, el stack y los componentes afectados antes de editar.
2. Identificar la responsabilidad propietaria, los consumidores y el contrato del caso de uso; comprobar si ya existe código reutilizable.
3. Definir límites y dirección de dependencias antes de implementar UI, CLI o integraciones.
4. Aplicar cambios pequeños y coherentes, preservando trabajo ajeno y evitando refactorizaciones o dependencias ajenas al alcance solicitado.
5. Actualizar pruebas y documentación cuando cambie comportamiento, arquitectura o contratos; explicar decisiones y migraciones pendientes.
6. Verificar nombres en inglés, documentación en español, SRP, ausencia de ciclos y separación entre presentación, dominio e infraestructura.
7. Ejecutar `mvn clean test` antes de abrir PR y `mvn checkstyle:check` para estilo y longitud de línea.
8. Reportar cambios, validaciones realmente ejecutadas y limitaciones; no presentar una revisión estática como prueba de ejecución.

- Probar reglas de negocio y casos de uso sin UI, incluyendo errores y límites; mantener compatibilidad de PNG/JPG, capas, DPI y exportación.
- Cuando se modifiquen contratos de CLI, comprobar argumentos, streams y códigos de salida en modo no interactivo.
- Cuando se modifique presentación, comprobar claves de traducción, ambos idiomas, temas y comportamiento visual; indicar si falta verificación manual.
- Para cambios exclusivamente documentales sin PR, revisar coherencia y diff; no es necesario ejecutar pruebas de aplicación.
- Mantener `AGENTS.md` como fuente de las convenciones y enlazarlo desde las guías; evitar copias divergentes de estas reglas.
