# Sistema de Parqueadero (SENA - GA7-220501096-AA3-EV02)

Aplicación web en Java (servlets + JSP + JDBC) con MySQL para gestionar:

- **HU-01** Registro de ingreso de vehículos (`/ingreso`)
- **HU-02** Registro de salida de vehículos (`/salida`)
- **HU-03** Cálculo y registro del cobro, con comprobante (`/cobro`)
- **HU-04** Apartado de espacios (`/apartado`)

## Requisitos
- JDK 11 o superior
- Apache Tomcat **9** (usa `javax.servlet`; Tomcat 10+ NO es compatible con este código)
- MySQL 5.7 o 8.x
- Maven (incluido en NetBeans, Eclipse e IntelliJ)

## Puesta en marcha
1. Ejecute `database/parqueadero.sql` en MySQL (crea la base `parqueadero` con sus tablas y datos de ejemplo).
2. Abra `src/main/java/com/parqueadero/util/Conexion.java` y ajuste `USUARIO` y `CLAVE` de MySQL.
3. Importe el proyecto como **proyecto Maven** en su IDE.
4. Configure Tomcat 9 y despliegue el proyecto (contexto `/Parqueadero`).
5. Abra http://localhost:8080/Parqueadero/

## Estructura
```
src/main/java/com/parqueadero/
  util/      Conexion, Validador, Tarifa, Fechas, ReglaNegocioException
  model/     Espacio, IngresoActivo
  dao/       EspacioDAO, IngresoDAO, CobroDAO, ApartadoDAO
  servlet/   InicioServlet, IngresoServlet, SalidaServlet, CobroServlet, ApartadoServlet
src/main/webapp/   JSP (inicio, ingreso, salida, cobro, comprobante, apartado), css, WEB-INF
database/parqueadero.sql
```

## Reglas de validación
- Placa: obligatoria, solo letras y números, 5 a 6 caracteres.
- Nombre del cliente: solo letras y espacios, 3 a 50 caracteres.
- Valor recibido: numérico, mayor a cero, hasta 8 enteros y 2 decimales, mayor o igual al valor a pagar.
- Fechas: formato estricto (no admite fechas inexistentes como 2026-02-30); la salida no puede ser anterior al ingreso ni futura; el apartado no puede ser en el pasado.
- Cobro: tarifa por hora o fracción, mínimo 1 hora.
