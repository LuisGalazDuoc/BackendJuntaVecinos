# Backend Junta de Vecinos Zaror

Microservicios Spring Boot para la app Android `JuntaVecinos`

## Ejecutar
Requisito: Docker Desktop iniciado

1. Renombra `.env.example` como `.env` para el levantamiento del backend
2. `docker compose up --build -d`
3. El backend queda en `http://localhost:9090`

- Detener: `docker compose down`. Agregar `-v` **borra la base de datos**.

## Cuentas ficticias iniciales
| Perfil | Correo | Contraseña |
|---|---|---|
| Vecino | vecino@zaror.test | Vecino.2026 |
| Directiva | directiva@zaror.test | Directiva.2026 |
| Tesorería | tesoreria@zaror.test | Tesoreria.2026 |
| Admin | admin@zaror.test | Admin.2026 |

## Servicios
| Servicio | Puerto interno | Base | Responsabilidad |
|---|---|---|---|
| api-gateway | 9090 (publicado) | – | Rutas y validación del JWT |
| dependencia-service | 9091 | bd_dependencia | Dependencias e información de la sede |
| reserva-service | 9092 | bd_reserva | Solicitudes, disponibilidad, estados e historial |
| movimiento-service | 9093 | bd_movimiento | Ingresos, gastos, pagos informativos, informes y categorías |
| auth-service | 9094 | bd_usuario | Registro, login, JWT y usuarios |
| comprobante-service | 9095 | bd_comprobante | Imágenes de respaldo |
| notificacion-service | 9096 | bd_notificacion | Avisos por usuario o perfil |

Liquibase crea las tablas y los datos iniciales; Hibernate solo valida (`ddl-auto=validate`)

## Endpoints
| Método y ruta | Perfiles |
|---|---|
| POST `/auth/login`, `/auth/register` | Público (el registro siempre crea un Vecino) |
| GET `/auth/me` | Todos |
| GET, POST `/usuarios` · PUT `/usuarios/{id}` · PATCH `/usuarios/{id}/activo`, `/contrasena` | Admin |
| GET `/dependencias`, `/dependencias/{id}`, `/{id}/exists`, `/dependencias/sede` | Todos |
| POST `/dependencias` · PUT `/dependencias/{id}`, `/dependencias/sede` · PATCH `/dependencias/{id}/activa` | Admin |
| GET `/reservas/disponibilidad`, `/reservas/estados` | Todos |
| POST `/reservas` · PUT `/reservas/{id}` · GET `/reservas/mias` | Vecino |
| GET `/reservas`, `/reservas/{id}/historial` · PATCH `/reservas/{id}/estado`, `/observacion` | Directiva |
| GET `/reservas/confirmadas` | Directiva, Tesorería |
| GET `/reservas/{id}` | Vecino (propias), Directiva, Tesorería (confirmadas) |
| PUT `/reservas/estados/{codigo}` | Admin |
| GET `/movimientos`, `/movimientos/{id}`, `/movimientos/resumen` | Directiva, Tesorería |
| POST `/movimientos` · DELETE `/movimientos/{id}` (anula) · GET `/movimientos/informe` | Tesorería |
| GET `/movimientos/publico` | Vecino |
| GET, PUT `/movimientos/visibilidad` | Directiva |
| GET `/categorias` | Directiva, Tesorería, Admin |
| POST `/categorias` · PUT `/categorias/{id}` | Admin |
| POST `/comprobantes` | Tesorería |
| GET `/comprobantes`, `/comprobantes/{id}/archivo` | Directiva, Tesorería |
| GET `/notificaciones` · PATCH `/notificaciones/{id}/leida` | Todos |

## Seguridad
- JWT HS256 de 1 hora con el perfil del usuario. Lo validan el Gateway y cada microservicio
- La clave `SECRET` y las de MySQL están en `.env`
- Las rutas `/interno/**` no se publican y exigen un token de servicio de corta duración
- Las contraseñas se guardan con SHA-1, no están en texto plano
- Un cambio de perfil o una desactivación rige en los demás servicios al vencer el token (máximo 1 hora)

## Conexión desde la app
- Emulador: usa `http://10.0.2.2:9090/` (valor por defecto). En Android 17 o superior se debe aceptar el permiso de red local.
- Teléfono por USB: `adb reverse tcp:9090 tcp:9090` y compilar con `-PapiBaseUrl=http://localhost:9090/`.
