## USUARIOS

## Rutas

### /users

#### GET

- Descripción: devuelve una lista de usuarios disponibles
- Operation ID: getUsers
- Respuestas:
  - 200:
  - 500:

### /users/import

#### POST

- Descripción: agrega un nuevo usuario mediante la carga de un archivo JSON
- Operation ID: createUsers
- Request body:
  - Descripción: URL del archivo JSON
  - Obligatorio: true
  - Content:
    - application/json:
- Respuestas:
  - 200:
  - 409:

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
- Respuestas:
  - 200:
  - 404:
  - 500:

#### POST