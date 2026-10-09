import { defineConfig } from 'cypress'

export default defineConfig({
  e2e: {
    // "npm run preview" sirve el build en el puerto 4173 (ver package.json)
    baseUrl: 'http://localhost:4173',
    supportFile: false,
    video: true,
    defaultCommandTimeout: 8000,
  },
})
