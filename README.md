# Order Service

Spring Boot order service using MariaDB.

Quick start:

1. Configure MariaDB and create database `orderdb`.
2. Update `src/main/resources/application.properties` with DB credentials.
3. Build and run:

```bash
mvn -q -DskipTests package
java -jar target/order-service-0.0.1-SNAPSHOT.jar
```

Endpoints:
- `POST /order` - create order using payload described in the task.
- `GET /order/{id}` - fetch order by id.
- `PATCH /order/{id}` - update order status with JSON `{ "order_status": "..." }`.
# order