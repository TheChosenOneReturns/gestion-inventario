# Sistema de Gestión de Inventario

Este es un proyecto full-stack de un sistema de gestión de inventario, desarrollado con un backend en Java y un frontend en React. La aplicación está diseñada para ser desplegada en AWS y cuenta con un pipeline de CI/CD automatizado con GitHub Actions.

## Funcionalidades

La aplicación permite a los usuarios:

*   **Autenticación de usuarios:** Sistema de login seguro para acceder al sistema.
*   **Gestión de productos:** Crear, leer, actualizar y eliminar productos del inventario.
*   **Dashboard interactivo:** Visualizar datos del inventario a través de gráficos y estadísticas.
*   **API RESTful:** El frontend se comunica con el backend a través de una API RESTful bien definida.

## Tecnologías Utilizadas

### Backend

*   **Lenguaje:** Java 17
*   **Framework:** Spring Boot
*   **Gestor de dependencias:** Maven
*   **Base de datos:** MySQL
*   **Autenticación:** Spring Security con JSON Web Tokens (JWT)
*   **Contenerización:** Docker

### Frontend

*   **Librería:** React
*   **Entorno de desarrollo:** Vite
*   **Lenguaje:** JavaScript
*   **Gestor de paquetes:** npm
*   **Peticiones HTTP:** Axios
*   **Gráficos:** Chart.js

## Arquitectura en AWS

La aplicación está diseñada para ser desplegada en la nube de AWS, utilizando los siguientes servicios:

*   **Backend:**
    *   **Amazon ECR (Elastic Container Registry):** El backend se empaqueta en una imagen de Docker y se almacena en un repositorio de ECR.
    *   **Amazon EC2 (Elastic Compute Cloud):** La imagen de Docker se despliega en una instancia de EC2, donde se ejecuta la aplicación de Spring Boot.
*   **Frontend:**
    *   **Amazon S3 (Simple Storage Service):** Los archivos estáticos del frontend (generados con `npm run build`) se alojan en un bucket de S3.
    *   **Amazon CloudFront:** Se utiliza como CDN para distribuir el contenido del frontend de manera eficiente y con baja latencia a los usuarios finales.

## Workflows de CI/CD con GitHub Actions

El repositorio está configurado con tres workflows de GitHub Actions para automatizar los procesos de integración continua y despliegue continuo:

### 1. `backend-deploy.yml`

*   **Trigger:** Se activa con cada `push` a la rama `main` que contenga cambios en la carpeta `gestion-inventario-backend`.
*   **Proceso:**
    1.  Construye la aplicación de Java con Maven.
    2.  Crea una imagen de Docker del backend.
    3.  Sube la imagen de Docker a Amazon ECR.
    4.  Se conecta a la instancia de EC2 a través de SSH y despliega la nueva versión de la aplicación, reiniciando el contenedor de Docker.

### 2. `frontend-deploy.yml`

*   **Trigger:** Se activa con cada `push` a la rama `main` que contenga cambios en la carpeta `gestion-inventario-frontend`.
*   **Proceso:**
    1.  Instala las dependencias de Node.js.
    2.  Construye la aplicación de React con Vite.
    3.  Sube los archivos estáticos generados a un bucket de Amazon S3.
    4.  Invalida la caché de Amazon CloudFront para que los usuarios reciban la última versión del frontend.

### 3. `quality-check.yml`

*   **Trigger:** Se activa con cada `pull request` dirigido a la rama `main`.
*   **Proceso:**
    *   **Pruebas del Backend:** Ejecuta las pruebas unitarias de la aplicación de Spring Boot.
    *   **Linting del Frontend:** Analiza el código del frontend en busca de errores de estilo y sintaxis.
    *   **Análisis de Dependencias:** Utiliza Trivy para escanear las dependencias del proyecto en busca de vulnerabilidades conocidas.
    *   **Análisis de Secretos:** Utiliza TruffleHog para detectar si se ha expuesto alguna credencial o secreto en el código.

## Guía de Instalación Local

### Prerrequisitos

*   Java 17
*   Maven
*   Node.js (v18 o superior)
*   npm
*   Docker
*   MySQL

### Configuración del Backend

1.  **Base de Datos:**
    *   Crea una base de datos en MySQL llamada `gestionbasedatos`.
    *   Importa el esquema de la base de datos desde el archivo `gestion-inventario-backend/bd.sql`.
2.  **Variables de Entorno:**
    *   Crea un archivo `application.properties` en `gestion-inventario-backend/src/main/resources/`.
    *   Añade las siguientes variables de entorno al archivo:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/gestionbasedatos
        spring.datasource.username=<tu_usuario_de_mysql>
        spring.datasource.password=<tu_contraseña_de_mysql>
        jwt.secret=<tu_secreto_jwt>
        ```

### Ejecución del Backend

1.  Navega al directorio `gestion-inventario-backend`.
2.  Ejecuta el siguiente comando para iniciar la aplicación:
    ```bash
    ./mvnw spring-boot:run
    ```

### Configuración del Frontend

1.  **Variables de Entorno:**
    *   Crea un archivo `.env` en la raíz del directorio `gestion-inventario-frontend`.
    *   Añade la siguiente variable de entorno:
        ```
        VITE_API_URL=http://localhost:8080
        ```

### Ejecución del Frontend

1.  Navega al directorio `gestion-inventario-frontend`.
2.  Instala las dependencias:
    ```bash
    npm install
    ```
3.  Inicia el servidor de desarrollo:
    ```bash
    npm run dev
    ```

## API Endpoints

A continuación se muestra una lista de los principales endpoints de la API:

*   **Autenticación:**
    *   `POST /auth/login`: Inicia sesión y obtiene un token JWT.
    *   `POST /auth/register`: Registra un nuevo usuario.
*   **Productos:**
    *   `GET /productos`: Obtiene la lista de todos los productos.
    *   `GET /productos/{id}`: Obtiene un producto por su ID.
    *   `POST /productos`: Crea un nuevo producto.
    *   `PUT /productos/{id}`: Actualiza un producto existente.
    *   `DELETE /productos/{id}`: Elimina un producto.
*   **Clientes:**
    *   `GET /clientes`: Obtiene la lista de todos los clientes.
    *   `GET /clientes/{id}`: Obtiene un cliente por su ID.

## Esquema de la Base de Datos

La base de datos `gestionbasedatos` contiene las siguientes tablas principales:

*   `admin_user`: Almacena los usuarios administradores.
*   `client`: Contiene la información de los clientes.
*   `product`: Guarda los detalles de los productos en el inventario.
*   `sale`: Registra las ventas realizadas.
*   `sale_item`: Almacena los productos incluidos en cada venta.
*   `provider`: Guarda la información de los proveedores.
*   `expense`: Registra los gastos.

Para más detalles, consulta el archivo `gestion-inventario-backend/bd.sql`.
