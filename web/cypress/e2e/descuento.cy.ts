// E2E contra la API real (http://localhost:8080) y el front-end real (http://localhost:4173).
describe('Calculadora de descuentos', () => {
  beforeEach(() => cy.visit('/'))

  it('muestra la version de la API desplegada', () => {
    cy.get('[data-cy=version]').should('contain', 'API 1.0.0')
  })

  it('calcula el precio final de 200 Bs con 25 %', () => {
    cy.get('[data-cy=precio]').clear().type('200')
    cy.get('[data-cy=porcentaje]').clear().type('25')
    cy.get('[data-cy=calcular]').click()
    cy.get('[data-cy=resultado]').should('contain', 'Bs 150,00')
  })

  it('redondea la mitad hacia arriba: 33,33 Bs con 10 % -> Bs 30,00', () => {
    cy.get('[data-cy=precio]').clear().type('33.33')
    cy.get('[data-cy=porcentaje]').clear().type('10')
    cy.get('[data-cy=calcular]').click()
    cy.get('[data-cy=resultado]').should('contain', 'Bs 30,00')
  })

  it('rechaza un descuento mayor que 100 sin llamar a la API', () => {
    cy.intercept('POST', '**/api/descuento').as('calcular')
    cy.get('[data-cy=porcentaje]').clear().type('150')
    cy.get('[data-cy=calcular]').click()
    cy.get('[data-cy=error]').should('contain', 'entre 0 y 100')
    cy.get('@calcular.all').should('have.length', 0)
  })

  it('rechaza un precio igual a cero', () => {
    cy.get('[data-cy=precio]').clear().type('0')
    cy.get('[data-cy=calcular]').click()
    cy.get('[data-cy=error]').should('contain', 'mayor que cero')
  })
})
