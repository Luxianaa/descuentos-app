# descuentos-app — Práctica 8 (COM450)

Calculadora de descuentos con **CI/CD en GitHub Actions**: la API (Spring Boot) se despliega en **Render** y el
front-end (Vite + React) en **Vercel**. Husky y commitlint protegen cada commit antes de que llegue a GitHub.

```
descuentos-app/
├── api/                 Spring Boot 3 · Java 17 · JUnit 5 · JaCoCo · Dockerfile
├── web/                 Vite · React · TypeScript · Vitest · ESLint · Prettier · Cypress
├── scripts/             verificar-config.sh · resumen-pruebas.py · smoke.sh
├── .husky/              pre-commit · commit-msg · pre-push
├── .github/workflows/   ci.yml (reutilizable) · pr.yml (CI + preview) · cd.yml (CI + despliegue)
└── commitlint.config.mjs
```

## Ejecutar en local

```bash
npm install                         # en la raíz: instala Husky, lint-staged y commitlint (y activa los hooks)
npm --prefix web ci                 # dependencias del front-end (incluye el binario de Cypress)

mvn -B -f api/pom.xml verify        # API: compila, prueba y exige 80 % de cobertura
npm --prefix web run test           # front-end: pruebas con Vitest
npm --prefix web run lint           # análisis estático
```

Para ver la aplicación y correr Cypress hacen falta tres terminales:

```bash
java -jar api/target/api.jar                         # 1) API en http://localhost:8080
npm --prefix web run build && npm --prefix web run preview   # 2) web en http://localhost:4173
npm --prefix web run e2e                             # 3) Cypress contra ambas
```

## Secretos y variables del repositorio (GitHub → Settings → Secrets and variables → Actions)

| Nombre | Tipo | Valor | Lo usa |
|---|---|---|---|
| `VERCEL_TOKEN` | Secret | Token de tu cuenta de Vercel | `pr.yml`, `cd.yml` |
| `VERCEL_ORG_ID` | Secret | `orgId` de `.vercel/project.json` | `pr.yml`, `cd.yml` |
| `VERCEL_PROJECT_ID` | Secret | `projectId` de `.vercel/project.json` | `pr.yml`, `cd.yml` |
| `RENDER_DEPLOY_HOOK` | Secret | URL del Deploy Hook del servicio de Render | `cd.yml` |
| `API_URL` | Variable | `https://TU-API.onrender.com` (sin `/` final) | `pr.yml`, `cd.yml` |
| `WEB_URL` | Variable | `https://TU-PROYECTO.vercel.app` (dominio de producción) | `cd.yml` |

Ambientes (Settings → Environments): `preview`, `api-production`, `web-production`.

## Variables de entorno de la API (en Render)

| Variable | Para qué | Ejemplo |
|---|---|---|
| `CORS_ORIGINS` | Orígenes del front-end que pueden llamar a la API (admite `*`) | `https://TU-PROYECTO*.vercel.app` |
| `PORT` | Lo define Render solo | — |
| `RENDER_GIT_COMMIT` | Lo define Render solo; `/api/info` lo devuelve | — |
