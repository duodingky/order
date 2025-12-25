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
