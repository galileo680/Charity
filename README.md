# Charity Collection Box Management System

A Spring Boot application for managing collection boxes during fundraising events for charity organizations.

## Features

- Create and manage fundraising events
- Register and manage collection boxes
- Assign collection boxes to fundraising events
- Transfer money from collection boxes to event accounts with currency conversion
- Generate financial reports
- Real-time currency exchange rates from external API

## Prerequisites

- Java 21
- Maven 3.8+

## Getting Started

### Building the Application

```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```

The application will start on port 8080 (http://localhost:8080).

## Testing

Run tests with:
```bash
mvn test
```

## API Endpoints
IMPORTANT: Only 3 currencies are available
- USD
- PLN
- EUR
### Fundraising Events

#### Create a Fundraising Event
```
POST /api/fundraising-events
Content-Type: application/json

{
  "name": " Charity One",
  "accountCurrency": "EUR"
}
```

#### Get Financial Report
```
GET /api/fundraising-events/financial-report
```

### Collection Boxes

#### Register a Collection Box
```
POST /api/collection-boxes
Content-Type: application/json

{
  "identifier": "BOX001"
}
```

#### List All Collection Boxes
```
GET /api/collection-boxes
```

#### Unregister a Collection Box
```
DELETE /api/collection-boxes/{identifier}
```

#### Assign Collection Box to Event
```
PUT /api/collection-boxes/{identifier}/assign
Content-Type: application/json

{
  "fundraisingEventId": 1
}
```

#### Add Money to Collection Box
```
POST /api/collection-boxes/{identifier}/money
Content-Type: application/json

{
  "currency": "USD",
  "amount": 100.00
}
```

#### Empty Collection Box (Transfer to Event)
```
POST /api/collection-boxes/{identifier}/empty
```

## External Services

The application uses [exchangerate-api.com](https://www.exchangerate-api.com/) for real-time currency exchange rates. If the API is unavailable, it falls back to hardcoded exchange rates.


## Technologies Used

- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database
- Lombok


## Notes

- Collection boxes must be empty before assignment to a fundraising event
- When a collection box is unregistered, it is automatically emptied and money is lost
- Currency conversion happens automatically when emptying a box

## Author

Bartłomiej Głuchowicz
