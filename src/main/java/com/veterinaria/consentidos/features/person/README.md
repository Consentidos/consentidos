# Person Feature

This feature manages person entities in the Consentidos veterinary application, providing comprehensive CRUD operations for storing and retrieving personal information including identification details.

## Architecture

This feature follows the **Hexagonal Architecture** (Clean Architecture) pattern, ensuring separation of concerns and maintainability:

### Domain Layer (`domain/`)

- **Entity**: `Person.java` - Core business entity with all attributes and business rules
- **Repository**: `PersonRepository.java` - Interface defining data access contracts

### Application Layer (`application/`)

- **Commands**: Data transfer objects for input validation
  - `CreatePersonCommand.java` - Command for creating new persons
  - `UpdatePersonCommand.java` - Command for updating existing persons
  - `PersonDto.java` - Data transfer object for responses
- **Use Cases**: Business logic implementation
  - `CreatePersonUseCase.java` - Handles person creation business rules
  - `GetPersonUseCase.java` - Handles person retrieval operations
  - `UpdatePersonUseCase.java` - Handles person update business rules
  - `DeletePersonUseCase.java` - Handles person deletion operations

### Infrastructure Layer (`infrastructure/`)

- **Persistence**: Data access implementation
  - `PersonJpaRepository.java` - Spring Data JPA repository interface
  - `PersonRepositoryImpl.java` - Repository adapter implementing domain contracts

### Presentation Layer (`presentation/`)

- **Controller**: REST API endpoints
  - `PersonController.java` - HTTP REST controller with full CRUD operations

## Person Entity Properties

| Property       | Type   | Description                                          | Constraints                         |
| -------------- | ------ | ---------------------------------------------------- | ----------------------------------- |
| `id`           | Long   | Auto-generated unique identifier                     | Primary key, auto-increment         |
| `sex`          | String | Gender specification (DB column: `sexo`)             | Required, must be "M" or "F"        |
| `firstName`    | String | Person's first name (DB column: `nombres`)           | Required, max 100 characters        |
| `lastName`     | String | Person's last name (DB column: `apellidos`)          | Required, max 100 characters        |
| `birthDate`    | Date   | Person's birth date (DB column: `fecha_nacimiento`)  | Required, not null                  |
| `city`         | String | Person's city of residence (DB column: `ciudad`)     | Required, max 100 characters        |
| `document`     | String | Identification document number (DB column: `documento`) | Required, unique, max 50 characters |
| `documentType` | String | Type of identification document (DB column: `tipo_documento`) | Required, max 20 characters |

## API Endpoints

### Create Person

- **POST** `/api/persons`
- **Body**: CreatePersonCommand JSON
- **Response**: PersonDto with generated ID

### Get Person by ID

- **GET** `/api/persons/{id}`
- **Response**: PersonDto or 404 Not Found

### Get Person by Document

- **GET** `/api/persons/document/{document}`
- **Response**: PersonDto or 404 Not Found

### Get All Persons (with optional filters)

- **GET** `/api/persons?city={city}&documentType={documentType}`
- **Response**: List of PersonDto

### Update Person

- **PUT** `/api/persons/{id}`
- **Body**: UpdatePersonCommand JSON
- **Response**: Updated PersonDto

### Delete Person

- **DELETE** `/api/persons/{id}`
- **Response**: 204 No Content or 404 Not Found

### Check Person Exists

- **GET** `/api/persons/exists/{document}`
- **Response**: JSON with existence status

### Get Person Count

- **GET** `/api/persons/count`
- **Response**: JSON with total person count

## Example Usage

### Creating a Person

```json
POST /api/persons
{
    "sex": "M",
    "birthDate": "1990-01-15T00:00:00",
    "firstName": "John",
    "lastName": "Doe",
    "city": "Bogotá",
    "document": "1234567890",
    "documentType": "CC"
}
```

### Response

```json
{
  "id": 1,
  "sex": "M",
  "birthDate": "1990-01-15T00:00:00",
  "firstName": "John",
  "lastName": "Doe",
  "city": "Bogotá",
  "document": "1234567890",
  "documentType": "CC"
}
```

## Business Rules

1. **Document Uniqueness**: Each person must have a unique document number
2. **Sex Validation**: Sex must be either "M" (Male) or "F" (Female)
3. **Required Fields**: All fields except ID are required
4. **Document Update**: Document number can be updated but the new value must remain unique across all persons

## Database Schema

The Person entity is mapped to the `persons` table with the following columns:

```sql
CREATE TABLE persons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sexo VARCHAR(1) NOT NULL CHECK (sexo IN ('M', 'F')),
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    fecha_nacimiento DATETIME NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    documento VARCHAR(50) NOT NULL UNIQUE,
    tipo_documento VARCHAR(20) NOT NULL
);
```

## Error Handling

The API provides comprehensive error handling:

- **400 Bad Request**: Validation errors, duplicate documents, and resource-not-found cases surfaced as `IllegalArgumentException` (e.g., updating a non-existent person)
- **404 Not Found**: Person not found when retrieving by ID/document or deleting
- **500 Internal Server Error**: Unexpected system errors

## Future Enhancements

- Add search by full name functionality
- Implement pagination for large datasets
- Add person photo storage capability
- Integrate with external document validation services
- Add audit trail for person data changes
