## USUARIOS

- Descripción: "Todo sobre tus usuarios"

## Rutas

### /users

#### GET

- Tags:
  - Usuarios
- Resumen: devuelve una lista de usuarios
- Descripción: devuelve una lista de usuarios disponibles
- Operation ID: getUsers
- Respuestas:
  - 200:
    - Descripción: OK
    - Content:
      - application/json:
        - Esquema:
          - Tipo: array
          - Elementos:
            - $ref: '#/components/schemas/User'
  - 500:
    - Descripción: respuesta de error
    - Content:
      - application/json:
        - Esquema:
          - $ref: '#/components/schemas/ErrorResponse'

#### POST

- Tags:
  - Usuarios
- Resumen: agrega un nuevo usuario mediante la carga de un archivo ('csv', 'json')
- Descripción: se crea un nuevo usuario
- Operation ID: createUsers
- Request body:
  - Descripción: datos de usuario necesarios para crear un usuario
  - Obligatorio: true
  - Content:
    - application/json:
      - Esquema:
        - $ref: '#/components/schemas/User'
- Respuestas:
  - 200:
    - Descripción: OK
    - Content:
      - application/json:
        - Esquema:
          - $ref: '#/components/schemas/User'
  - 409:
    - Descripción: error
    - Content:
      - application/json:
        - Esquema:
          - $ref: '#/components/schemas/ErrorResponse'

### /users/{id}

#### GET

- Tags:
  - Usuarios
- Resumen: devuelve un usuario
- Descripción: devuelve los detalles de un usuario dado su ID
- Operation ID: getUser
- Parámetros:
  - Nombre: id
  - En: path
  - Obligatorio: true
  - Esquema:
    - Tipo: integer
    - Formato: int64
- Respuestas:
  - 200:
    - Descripción: OK
    - Content:
      - application/json:
        - Esquema:
          - $ref: '#/components/schemas/User'
  - 404:
    - Descripción: error de no encontrado
    - Content:
      - application/json:
        - Esquema:
          - $ref: '#/components/schemas/ErrorResponse'
  - 500:
    - Descripción: error del servidor
    - Content:
      - application/json:
        - Esquema:
          - $ref: '#/components/schemas/ErrorResponse'

#### POST

- Tags:
  - Usuarios
- Parámetros:
  - Nombre:
