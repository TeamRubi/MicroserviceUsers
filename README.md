## USUARIOS

## Rutas

### /users

<<<<<<< HEAD
<<<<<<< HEAD
### Getuserbyid
- Create and endpoint that shows a user by passing the id

### GetuserbyName
- Create and endpoint that shows a user by passing the id

### DeleteUserbyid
- Create and endpoint that deletes user by passing the id
=======
=======
>>>>>>> 9eab7c3572293f1665ba3248b5e3228be92bb4ea
#### GET

- Descripción: devuelve una lista de usuarios disponibles
- Operation ID: getUsers
- Respuestas:
  - 200:
  - 500:

### /users/import
<<<<<<< HEAD
>>>>>>> 36a289ecfd06cb179911b3144f3dd082d0a00474
=======
>>>>>>> 9eab7c3572293f1665ba3248b5e3228be92bb4ea

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

<<<<<<< HEAD
<<<<<<< HEAD
### Getuserbyid
- Testing the endpoint to verify its functionality **Service and Integration Test**

### Getuserbyid
- Testing the endpoint to verify its functionality **Service and Integration Test**

### DeleteUserbyid
- Testing the endpoint to verify its functionality **Service and Integration Test**

=======
=======
>>>>>>> 9eab7c3572293f1665ba3248b5e3228be92bb4ea
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
<<<<<<< HEAD
>>>>>>> 36a289ecfd06cb179911b3144f3dd082d0a00474
=======
>>>>>>> 9eab7c3572293f1665ba3248b5e3228be92bb4ea
