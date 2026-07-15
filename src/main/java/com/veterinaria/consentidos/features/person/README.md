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
  - `PersonQueryBuilder.java` - Builds JPA Criteria predicates for dynamic queries

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
- **Response**: `201 Created` with PersonDto

### Get Person by ID

- **GET** `/api/persons/{id}`
- **Response**: `200 OK` with PersonDto, or `404 Not Found`

### Get All Persons (with optional filters)

- **GET** `/api/persons?city={city}&documentType={documentType}`
- **Response**: `200 OK` with List of PersonDto

### Update Person

- **PUT** `/api/persons/{id}`
- **Body**: UpdatePersonCommand JSON (birthDate is not updatable)
- **Response**: `200 OK` with updated PersonDto, or `404 Not Found`

### Search Persons (paginated, dynamic criteria)

- **POST** `/api/persons/search`
- **Body**: PersonSearchCriteria JSON
- **Response**: `200 OK` with paginated PagedResult

### Delete Person

- **DELETE** `/api/persons/{id}`
- **Response**: `204 No Content`, or `404 Not Found`

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

1. **Document Uniqueness**: Each person must have a unique document number. Attempting to create or update a person with an existing document returns `409 Conflict`.
2. **Sex Validation**: Sex must be either `"M"` (Male) or `"F"` (Female).
3. **Required Fields**: All fields except `id` are required at creation.
4. **birthDate Immutability**: `birthDate` is set at creation and cannot be modified via the update endpoint.
5. **Document Updateability**: `document` can be changed during an update, provided the new value is unique across all persons.

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

- **400 Bad Request**: Validation errors (missing or malformed fields)
- **404 Not Found**: Person not found when retrieving by ID or deleting (`PersonNotFoundException`)
- **409 Conflict**: Duplicate document number on create or update (`PersonAlreadyExistsException`)
- **500 Internal Server Error**: Unexpected system errors

## Future Enhancements

- Add search by full name functionality
- Implement pagination for large datasets
- Add person photo storage capability
- Integrate with external document validation services
- Add audit trail for person data changes
