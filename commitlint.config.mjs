// Conventional Commits: tipo(alcance): descripcion   ->   feat(api): agrega descuento por cantidad
export default {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'subject-case': [0],
    'header-max-length': [2, 'always', 100],
  },
}
