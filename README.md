# Pastebin

A simple pastebin service built with Spring Boot that allows users to perform CRUD operations on text blocks with automatic expiration.

## Features

- Create pastes with custom expiration times
- Retrieve pastes by unique short ID
- Update existing pastes
- Automatic deletion of expired pastes
- RESTful API design

## Technologies Used

- Java 21
- Gradle
- Spring Boot
- Spring Data JPA
- PostgreSQL
- AWS S3
- Redis
- Docker
- Docker Compose
- Nginx

## Running the Application

This application uses Docker Compose with Nginx as a reverse proxy.

### Prerequisites

- JDK 21 or higher
- Gradle
- PostgreSQL
- AWS Account (for S3)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/pastebin.git
cd pastebin
```

2. Configure database in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pastebin
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Configure AWS credentials:
```properties
aws.accessKeyId=your_access_key
aws.secretKey=your_secret_key
aws.s3.bucket=your_bucket_name
```

4. Build the project:
```bash
./gradlew build
```

5. Run the application:
```bash
./gradlew bootRun
```

Access the application on `http://localhost:80`

## API Endpoints

### Create a Paste
```http
POST /pastebin
Content-Type: application/json

{
  "content": "Your text here",
  "expirationMinutes": 60
}
```

**Response:** `201 Created`
```json
{
  "id": "abc123",
  "content": "Your text here",
  "createdAt": "2025-10-31T10:15:00Z",
  "updatedAt": "2025-10-31T10:15:00Z",
  "expiresAt": "2025-10-31T11:15:00Z"
}
```

### Get a Paste
```http
GET /pastebin/{id}
```

**Response:** `200 OK`

### Update a Paste
```http
PUT /pastebin/{id}
Content-Type: application/json

{
  "content": "Updated text"
}
```

**Response:** `200 OK`

### Delete a Paste
```http
DELETE /pastebin/{id}
```

**Response:** `204 No Content`

## Error Handling

- `400 Bad Request` - Invalid input (empty content or invalid expiration)
- `404 Not Found` - Paste doesn't exist
- `500 Internal Server Error` - Server error
