# Simulador de Misión Lunar — Artemis II

## Nombre de nuestra misión: SOLARIS

## Descripción del Proyecto

SOLARIS es un proyecto académico de la asignatura INF-272-01 orientado al desarrollo de un simulador de misión lunar inspirado en Artemis II.

El proyecto integra una simulación orbital desarrollada en Java con Orekit y una interfaz gráfica desarrollada mediante JavaFX. La aplicación permite configurar parámetros de la misión, ejecutar la simulación y visualizar la trayectoria junto con información de telemetría.

---

# Tripulación 2

| Integrante | Rol principal | Siglas | Otros roles | Siglas |
|---|---|---|---|---|
| Ruth Suero | Comandante / Gestor de Proyectos | CDR | Ingeniero de Requisitos | REQ |
| Angel Lachapel | Arquitecto de Software | ARCH | Oficial de Dinámica de Vuelo / Comunicador de Cápsula / Líder de Interfaz | FDO/CAPCOM |

---

# Política de Ramificación y Reglas de Trabajo

Con el objetivo de mantener un desarrollo organizado, estable y sin conflictos dentro del proyecto, el equipo acuerda las siguientes normas de trabajo en Git y GitHub.

## Rama Principal

- La rama `main` representa la versión estable del proyecto.
- Está prohibido realizar cambios directamente sobre `main`.
- Todo cambio deberá integrarse mediante Pull Request.

---

## Convención de Ramas

Cada nueva tarea deberá desarrollarse en una rama independiente creada desde `main`.

| Tipo | Uso | Ejemplo |
|---|---|---|
| `feature/` | Nuevas funcionalidades | `feature/diseno-parche` |
| `fix/` | Corrección de errores | `fix/error-orbita` |
| `docs/` | Documentación | `docs/readme-inicial` |

---

## Flujo de Trabajo

1. Actualizar la rama `main`.
2. Crear una nueva rama para la tarea.
3. Realizar cambios y commits descriptivos.
4. Subir la rama a GitHub.
5. Abrir un Pull Request hacia `main`.
6. Esperar la revisión y aprobación de otro integrante.
7. Fusionar únicamente después de la aprobación.

---

## Revisión de Código

- Ningún integrante puede aprobar su propio Pull Request.
- Al menos un miembro del equipo debe revisar los cambios.
- La revisión debe verificar:
  - funcionamiento,
  - claridad,
  - compilación correcta,
  - ausencia de errores importantes.
- Tiempo máximo recomendado para revisión: 24 horas.

---

## Formato de Commits

Cada commit debe iniciar con una etiqueta que identifique el área modificada.

| Etiqueta | Uso |
|---|---|
| `[CORE]` | Física, lógica y simulación |
| `[UI]` | Interfaz gráfica |
| `[DOCS]` | Documentación |
| `[TEST]` | Pruebas |
| `[CONFIG]` | Configuración del proyecto |

### Ejemplos

```bash
[DOCS] Agregar política de ramificación
[CORE] Implementar cálculo de trayectoria
[UI] Mejorar panel de control

---

# Versiones del Proyecto

| Versión | Descripción |
|---|---|
| `v0.1-fase3` | Implementación inicial |
| `v0.5-beta` | Versión de pruebas |
| `v1.0-final` | Entrega final del proyecto |

---

# Tecnologías Utilizadas

- Java
- JavaFX
- Maven
- Orekit
- Git
- GitHub
- Visual Studio Code

---

# Requisitos

## Para utilizar el producto final

El paquete de despliegue está diseñado para ejecutarse sin necesidad de compilar el código fuente ni utilizar Maven.

Se requiere:

- JDK Java 17 o superior, sujeto a la compatibilidad comprobada de la versión final.
- Un sistema operativo compatible con el paquete correspondiente.
- Los archivos incluidos en el paquete de despliegue.

El paquete incluye los datos necesarios de Orekit para la ejecución del simulador.

## Para desarrollo

Para compilar y modificar el proyecto desde el código fuente se requiere:

- JDK compatible con la versión configurada en el proyecto.
- Apache Maven.
- Código fuente del proyecto.
- Datos de Orekit.

---

# Estructura del Paquete de Despliegue

El paquete de despliegue contiene los elementos necesarios para ejecutar el simulador:

```text
deployment/
├── simulador-artemis-ii-1.0-SNAPSHOT.jar
├── orekit-data/
├── run.bat
└── run.sh

El usuario final no necesita acceder al código fuente ni ejecutar Maven.

---

# Ejecución del Producto

## Windows

Desde la carpeta `deployment`, ejecutar:

```bash
run.bat

Este script inicia el JAR del simulador.

También puede ejecutarse directamente mediante:

```bash
java -jar simulador-artemis-ii-1.0-SNAPSHOT.jar
```

## Linux

Desde la carpeta `deployment`, ejecutar:

```bash
./run.sh
```

El script inicia el JAR del simulador.

---

# Compilación para Desarrollo

Para compilar el proyecto desde el código fuente:

```bash
mvn clean package
```

El archivo JAR generado se encuentra en la carpeta:

```text
target/
```

La compilación también ejecuta las pruebas automatizadas configuradas en el proyecto.

---

# Ejecución durante el Desarrollo

Para ejecutar la aplicación desde el entorno de desarrollo puede utilizarse la clase principal:

```text
com.artemis.Main
```

La ejecución mediante Maven también puede realizarse mediante:

```bash
mvn exec:java
```

Estas opciones corresponden al entorno de desarrollo. Para utilizar el producto final se recomienda ejecutar el paquete de despliegue mediante `run.bat` o `run.sh`.

---

# Estado del Proyecto

El proyecto se encuentra preparado para la fase final de documentación y empaquetado correspondiente al Entregable #6.

La versión final será identificada mediante la etiqueta:

```text
v1.0-final
```

El paquete final incluirá el manual de usuario, el paquete de despliegue, los registros de decisiones de arquitectura y el informe de reflexión correspondiente.