# order

## Running locally

```bash
npm install
npm start
```

The service listens on `PORT` (default: `3000`).
Set `PRODUCT_SERVICE_BASE_URL` to override the product service base URL
(default: `http://localhost:81`).

## Create order endpoint

`POST /createOrder`

Request body:

```json
{
  "orderItems": [
    {
      "id": "9503902a-9a28-4740-a115-6c34c80b5c4e",
      "qty": 1
    }
  ]
}
```

You can also pass `orderItems` as a query parameter containing a JSON string.

Response:

```json
{
  "orderId": "generated-uuid",
  "orderItems": [
    {
      "id": "9503902a-9a28-4740-a115-6c34c80b5c4e",
      "qty": 1,
      "productName": "Example",
      "sku": "SKU-1",
      "categoryId": "cat-1",
      "categoryName": "Category",
      "brandId": "brand-1",
      "brandName": "Brand",
      "price": 10.5,
      "shortDesc": "Short description"
    }
  ]
}
```