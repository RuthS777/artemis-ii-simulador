# Política de Ramificación - Simulador Artemis II

## Rama Principal
- `main` está protegida. **Nadie puede hacer push directo a main.**
- Todo cambio entra únicamente mediante Pull Request.
- Todo Pull Request requiere mínimo **1 aprobación** de un compañero antes de hacer merge.

## Nomenclatura de Ramas
| Tipo | Formato | Ejemplo |
|------|---------|---------|
| Nueva funcionalidad | `feature/descripcion` | `feature/orekit-hello-world` |
| Corrección de error | `fix/descripcion` | `fix/orekit-carga-datos` |
| Documentación | `docs/descripcion` | `docs/actualizar-readme` |

## Formato de Commits
Todo commit debe seguir este formato obligatorio:

    [MÓDULO] descripción breve

Ejemplos:
- [FÍSICA] Agregar modelo de fuerza de tercer cuerpo de la Luna
- [UI] Crear ventana principal JavaFX
- [DOCS] Actualizar README con integrantes

## Pull Requests
- Los PR deben revisarse en un plazo máximo de **24 horas**.
- El autor del PR no puede aprobar su propio PR.
- Los PR que lleven más de 3 días abiertos serán escalados al Comandante.

## Etiquetas de Versión
- `v0.1-fase3` al completar la Fase 3
- `v1.0-final` en la entrega final