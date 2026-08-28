# Insignia Digital

Código de la presencia web de Insignia Digital. Este repositorio contiene el
sitio estático y la API del formulario; la infraestructura de ejecución vive
separada en `../insigniadigital-docker`.

## Estructura

```text
site/          Export estático saneado y recursos propios del formulario.
contact-api/   API Spring Boot 3.5.16 / Java 21 para entrega por SMTP.
```

No contiene WordPress ejecutable, PHP, base de datos, Docker, Nginx ni
credenciales. El estado interno de JetBackup y los plugins de respaldo sin uso
fueron excluidos antes de incorporar el export.

## Formulario

La portada conserva el formulario visual original, pero su envío ya no depende
de `wp-admin/admin-ajax.php`. `site/assets/contact-form.js` intercepta el envío y
consume `POST /api/contact` con los campos nombre, apellidos, correo y mensaje.

La API valida el contenido, usa un honeypot invisible, limita intentos por IP y
envía el mensaje a `atencion@insigniadigital.com.mx`. No persiste información.

En un entorno publicado, el sitio y la API deben compartir origen y la ruta
`/api/contact` debe llegar a `contact-api`. Esa configuración pertenece al repo
de infraestructura y no se duplica aquí.

## Configuración del backend

Copia `.env.example` a `.env` y proporciona las credenciales reales del buzón.
Spring lee estas variables desde el entorno del proceso:

```bash
set -a
source .env
set +a
cd contact-api
mvn spring-boot:run
```

Sin credenciales, la aplicación puede iniciar y pasar sus healthchecks, pero un
envío real responderá `502 unavailable` de forma segura.

## Pruebas

```bash
cd contact-api
mvn test
```

La suite cubre entrega SMTP, validación, honeypot, rate limiting y manejo seguro
de fallos del proveedor de correo.

## Favicon

Los archivos `favicon.ico` y `favicon.gif` encontrados en el respaldo original
estaban vacíos. Este repositorio usa `favicon.png`, copiado del logotipo oficial
`Logo-ID-edited-300x300.png`; también conserva los iconos PNG y Apple Touch
declarados por WordPress.
