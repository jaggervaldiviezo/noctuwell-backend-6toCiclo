# Noctuwell - Backend (API) Plataforma de Bienestar para Horarios Nocturnos

API desarrollada con **Spring Boot** para soportar la plataforma Noctuwell, encargada de la lógica de negocio, persistencia de datos y exposición de endpoints para el frontend.

---

## Descripción del Proyecto

**Noctuwell Backend** es el componente servidor de la plataforma web Noctuwell. Provee servicios REST para gestionar las operaciones principales del sistema y entregar datos al frontend.

> **Nota:** Este repositorio contiene únicamente el backend/API. El frontend (Angular) se encuentra en un repositorio separado.

---

## Funcionalidades del Sistema (más importantes)

- **API REST** para consumo desde el frontend.
- **Gestión de usuarios y perfiles** (pacientes / especialistas).
- **Gestión de citas** (creación, actualización, cancelación y estados).
- **Gestión de diagnósticos y seguimiento**.
- **Gestión de historias clínicas**.
- **Mensajería vinculada a citas** (persistencia y consulta de historial).
- **Soporte para reportes** (endpoints para estadísticas y agregaciones).
- **Seguridad y autenticación** mediante **JWT Token**.

> El sistema puede incluir más endpoints y módulos, pero los listados representan las funcionalidades principales.

---

## Arquitectura (Resumen)

- **Spring Boot** como framework principal.
- Estructura por capas:
  - **Controllers** (endpoints REST)
  - **DTOs** (objetos de transferencia para Requests/Responses)
  - **Service Interface** (contratos / firmas de servicios)
  - **Service Implements** (implementación de lógica de negocio)
  - **Repositories** (acceso a datos)
  - **Entities/Models** (modelo de dominio)
  - **Exceptions** (manejo de errores, validaciones y respuestas controladas)
- Configuración por `application.properties` / `application.yml`.
- **Security** con JWT para protección de endpoints.

---

## Requisitos Técnicos

### Software Necesario
- **Git**
- **Java 17** (recomendado)
- **Maven** (o Gradle, según el proyecto)
- **Postman** (para pruebas de endpoints)

---

## Configuración

Configura los valores en `src/main/resources/application.properties` (o `.yml`), por ejemplo:
- `server.port`
- Conexión a base de datos (URL, usuario, contraseña)
- Configuración de seguridad (secret/expiración de JWT, etc.)

--- 

## Licencia Proyecto desarrollado con fines académicos. 

--- 

**Curso:** Arquitectura de Aplicaciones Web                       
**Grupo:** 4                   
**Ciclo:** 2025-2                  
**Desarrollado durante:** Ago 2025 - Dic 2025
