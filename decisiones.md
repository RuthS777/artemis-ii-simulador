   Decisiones tecnicas


# Cambios relevantes

## Uso de la IA 
A lo largo del entregable #0 y #1
       
### -Aclaración de conceptos físicos y aeroespaciales
Razón: Debido al desconocimiento de todos los implicados sobre física aeroespacial, se tuvo que hacer uso de la IA para aclarar muchos conceptos 
relevantes en el funcionamiento del entregable, como qué es una órbita LEO o cuál es la función de los propagadores.
        
### -Explicación teórica de las funciónes y abstracciones
Razón: Ya entrados en Orekit, se uso para entender a un nivel teórico cuál es el proceso que sigue Orekit para calcular los cambios de posición en 
órbita, especialmente para entender el rol que cumplen los elementos órbitales Keplerianos y cómo afectan la trayectoria
        
### -Despejar dudas sobre definiciones clave.
Razón: En el documento de especificación de requisitos se usó para aclarar confusiones en torno a ciertos conceptos, como el enteder que es todo lo que
abarcan los requisitos funcionales y los no funcionales.
        
### -Formateo del documento y deteción de duplicidades
Razón: Se consultó para confirmar que no hubiera errores en el formato seguido en el documeto de requisitos y para eliminar cualquier pieza de
información que pudiera ser redundante
        
       
     nota: Todos estos usos fueron puramente orientativos y todo lo escrito tanto en el código como en el documento de requisitos
     es el resultado de la aplicación de los  conocimientos de todos los implicados.
<br>
<br>
<br>

    
A lo largo del entregable #2

### -Corrección de notación UML en diagramas de casos de uso, secuencia, estado y actividad
Razón:  Se detectaron errores de notación en diagramas ya elaborados por el equipo (por ejemplo, flujos secuenciales representados como asociaciones directas entre casos de uso, o decisiones modeladas con rombos dentro de diagramas de estado). Se usó la IA para identificar estos errores contra la notación UML formal y corregirlos. 
También se detectó que uno de los diagramas etiquetados como "de estado" correspondía en realidad a un diagrama de actividad, y que el diagrama de estado restante no representaba estados compuestos.
### -Revisión de las secciones introductorias y de restricciones del documento 
Razón: Para revisar que las secciones 1.1, 1.2, 1.3, 2 y 3 del documento reflejaran estos conceptos de forma coherente con el resto del contenido, sin agregar apartados aislados que rompieran la fluidez del documento, identificando cualquier concepto y término que se haya pasado por alto a medida que el documento se iba actualizando o corrigiendo. 
nota: todos estos usos fueron de verificación, corrección y comparación con la rúbrica y la notación UML formal. Todo el contenido técnico, las decisiones de diseño y el criterio final sobre qué incluir en el documento fueron responsabilidad y resultado del trabajo de todos los implicados (dos miembros del grupo y aportes de un tercero).

<br>
<br>
<br>

A lo largo del entregable #3

### -Consulta sobre la integración de Orekit y resolución de errores de compilación

Razón: Se utilizó la IA para consultar el uso de algunas clases y métodos de Orekit, así como para identificar posibles causas de errores de compilación relacionados con dependencias, configuración del proyecto y compatibilidad entre versiones de la biblioteca durante la implementación del Spike TLI.         ### -Apoyo en la revisión y organización del código

Razón: Se utilizó la IA para identificar código redundante, mejorar la organización de algunas clases y revisar la estructura final del proyecto antes de integrarlo al repositorio, sin modificar las decisiones de diseño adoptadas por el equipo.

# Registros de Decisiones de Arquitectura (ADR)

## ADR-001 — Uso de propagación numérica con Orekit

**Estado:** Aceptada

### Contexto

El simulador necesita representar una trayectoria lunar a partir de una órbita inicial terrestre y una maniobra de Inyección Translunar (TLI). El proyecto utiliza Java y la librería Orekit como parte del stack tecnológico establecido.

La simulación requiere considerar la gravedad terrestre mediante armónicos esféricos, así como la influencia gravitatoria de la Luna y el Sol. También es necesario aplicar una maniobra impulsiva durante la propagación y registrar los puntos de la trayectoria.

### Decisión

Se decidió utilizar el `NumericalPropagator` de Orekit como motor principal de propagación orbital, configurado con un integrador numérico adaptativo y los modelos de fuerza requeridos para la simulación.

La maniobra TLI se representa mediante un `ImpulseManeuver` durante la propagación.

### Alternativas consideradas

- **Propagación analítica mediante ecuaciones simplificadas:** descartada porque no permite representar adecuadamente la influencia de múltiples cuerpos y las maniobras requeridas.
- **Implementar un propagador orbital propio:** descartada por la complejidad, el riesgo de errores numéricos y el tiempo disponible para el proyecto.
- **Utilizar únicamente una órbita de Kepler sin perturbaciones:** descartada porque no representa las condiciones necesarias para una trayectoria lunar.

### Consecuencias

**Positivas:**
- Permite utilizar modelos físicos establecidos por Orekit.
- Facilita la incorporación de la gravedad terrestre, lunar y solar.
- Permite modelar la TLI como una maniobra impulsiva.
- Reduce la necesidad de implementar matemáticas orbitales desde cero.

**Negativas:**
- La configuración de Orekit es más compleja que una propagación simplificada.
- El simulador depende de los datos y configuraciones requeridos por Orekit.
- Los resultados dependen de la fidelidad de los modelos de fuerzas utilizados.

## ADR-002 — Separación de la propagación y la interfaz JavaFX

**Estado:** Aceptada

### Contexto

La propagación orbital puede requerir un tiempo considerable debido a la cantidad de cálculos realizados por Orekit. Ejecutarla directamente en el hilo de JavaFX podría bloquear la interfaz y provocar que la ventana deje de responder mientras se ejecuta una simulación.

Además, la interfaz necesita reproducir posteriormente los puntos calculados y mostrar los valores de telemetría.

### Decisión

Se decidió separar la ejecución de la simulación de la interfaz gráfica.

La propagación se ejecuta mediante una tarea independiente (`SimulacionTask`) y devuelve los resultados de la simulación. JavaFX utiliza posteriormente esos resultados para actualizar la visualización, la trayectoria y la telemetría.

### Alternativas consideradas

- **Ejecutar la propagación directamente desde JavaFX:** descartada porque puede bloquear el hilo de interfaz durante los cálculos.
- **Actualizar la interfaz durante cada paso de propagación:** descartada porque aumentaría el acoplamiento entre el motor orbital y la interfaz.
- **Ejecutar toda la simulación y actualizar la interfaz posteriormente:** elegida como base porque permite separar claramente el cálculo de la visualización.

### Consecuencias

**Positivas:**
- La interfaz puede mantenerse responsiva mientras se ejecuta la simulación.
- Se reduce el acoplamiento entre el motor orbital y JavaFX.
- Los resultados pueden reutilizarse para reproducir la trayectoria.
- Facilita las pruebas del motor orbital independientemente de la interfaz.

**Negativas:**
- Es necesario gestionar correctamente la comunicación entre la tarea de simulación y JavaFX.
- Los resultados deben almacenarse antes de iniciar la reproducción.
- La actualización de la interfaz requiere mecanismos propios de JavaFX para trabajar con el hilo gráfico.


## ADR-003 — Visualización orbital 2D mediante JavaFX Canvas

**Estado:** Aceptada

### Contexto

El simulador necesita proporcionar una representación visual de la misión que permita observar la Tierra, la Luna, la nave espacial y el recorrido de la trayectoria.

El alcance obligatorio requiere una visualización orbital 2D como mínimo. El proyecto también tiene restricciones de tiempo y no requiere una representación tridimensional para cumplir su funcionalidad principal.

### Decisión

Se decidió utilizar JavaFX con un `Canvas` para construir una representación orbital 2D.

La visualización representa la Tierra, la Luna, la nave espacial y el rastro de la trayectoria. La animación utiliza los puntos previamente calculados por el motor de simulación.

### Alternativas consideradas

- **Visualización 3D con JavaFX:** descartada porque aumenta considerablemente la complejidad de implementación y no es necesaria para cumplir el alcance obligatorio.
- **Utilizar una librería gráfica externa:** descartada para evitar agregar dependencias y complejidad al proyecto.
- **Utilizar únicamente gráficos estáticos:** descartada porque no permitiría representar adecuadamente la reproducción de la trayectoria.

### Consecuencias

**Positivas:**
- Cumple el requisito de visualización orbital 2D.
- Mantiene la interfaz relativamente sencilla.
- Permite representar y animar la trayectoria.
- Evita la complejidad adicional de una solución 3D.
- Facilita la integración con los puntos calculados por la simulación.

**Negativas:**
- La representación no proporciona una vista tridimensional de la misión.
- La escala visual debe adaptarse para representar correctamente distancias orbitales muy diferentes.
- La visualización es una representación gráfica y no una reproducción tridimensional físicamente proporcional del espacio.
