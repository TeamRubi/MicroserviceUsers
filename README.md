## USUARIOS

## Rutas

### /users

#### GET

- Descripción: devuelve una lista de usuarios disponibles
- Operation ID: getUsers
- Respuestas:
  - 200:
  - 404:
  - 500:

#### POST
- Descripción: Crea un usuario a partir de un cuerpo 
- Operation ID: createUsers
- Respuestas:
  - 201:
  - 409:
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
  - 201:
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

#### PUT
- Tags:
  - Usuarios
- Resumen: edita un usuario
- Descripción: permite editar un usuario 
- Operation ID: editUser
- Parámetros:
  - Nombre: id
  - En: path
  - Obligatorio: true
- Respuestas:
  - 200:
  - 404:
  - 500:

#### DELETE
- Tags:
  - Usuarios
- Resumen: elimina un usuario
- Descripción: permite eliminar un usuario a traves de un ID
- Operation ID: deleteUser
- Parámetros:
  - Nombre: id
  - En: path
  - Obligatorio: true
- Respuestas:
  - 204:
  - 404:
  - 500:

### /users/name/{name}

#### GET
- Tags:
  - Usuarios
- Resumen: devuelve usuarios
- Descripción: devuelve los detalles de un usuario o varios dado su nombre
- Operation ID: getUsersbyName
- Parámetros:
  - Nombre: name
  - En: path
  - Obligatorio: true
- Respuestas:
  - 200:
  - 404:
  - 500:

### /users/email/{email}

#### GET
- Tags:
  - Usuarios
- Resumen: devuelve un usuario
- Descripción: devuelve los detalles de un usuario dado su email
- Operation ID: getUserbyEmail
- Parámetros:
  - Nombre: email
  - En: path
  - Obligatorio: true
- Respuestas:
  - 200:
  - 404:
  - 500: