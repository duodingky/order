# Order API

Simple Spring Boot REST API (no database).

Build and run:

```bash
mvn package
java -jar target/order-0.0.1-SNAPSHOT.jar
```

Endpoints:
- GET /order/{id} — fetch order by id
- POST /order — create order (JSON body, server assigns id)

Example POST body:

```json
{
  "product": "Widget",
  "quantity": 3,
  "price": 19.99
}
```

Using MariaDB
--------------

1. Start MariaDB and create database (or let the connector create it):

```sql
CREATE DATABASE IF NOT EXISTS orderdb;
CREATE USER 'order'@'%' IDENTIFIED BY 'changeme';
GRANT ALL ON orderdb.* TO 'order'@'%';
```

2. Update `src/main/resources/application.properties` with DB credentials.

3. Build and run the app; Hibernate will create/update tables automatically.

Sample create order payload (full FF schema):

```json
{
  "shippingDetails": {
    "firstName": "John",
    "lastName": "Doe",
    "address1": "123 Main St",
    "city": "Springfield",
    "country": "USA",
    "zipCode": "12345"
  },
  "shippingTotal": 5.0,
  "payment": "card",
  "items": [
    { "sku": "SKU1", "unitPrice": 10.0, "productName": "Widget A", "qty": 2 },
    { "sku": "SKU2", "unitPrice": 5.0, "productName": "Widget B", "qty": 1 }
  ]
}
```

Sample curl:

```bash
curl -X POST -H "Content-Type: application/json" -d @order.json http://localhost:8080/order
curl http://localhost:8080/order/1
```

