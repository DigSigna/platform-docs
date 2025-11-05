# Criterios de Aceptación Estándar para Microservicios


```gherkin
# Criterios Genéricos para Microservicios

Feature: Health Check
  Scenario: Service health endpoint
    When I call GET /actuator/health
    Then response status should be 200
    And response should contain {"status": "UP"}

Feature: Security Baseline  
  Scenario: Endpoints secured by default
    When I call any endpoint without authentication
    Then response status should be 401 Unauthorized

Feature: Error Handling
  Scenario: Consistent error responses
    When I trigger an error condition
    Then response should follow standard error format
    And should include traceId for debugging