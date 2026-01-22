const express = require("express");
const crypto = require("crypto");

const app = express();
app.use(express.json());

const PORT = Number.parseInt(process.env.PORT, 10) || 3000;
const PRODUCT_SERVICE_BASE_URL =
  process.env.PRODUCT_SERVICE_BASE_URL || "http://localhost:81";

const UUID_REGEX =
  /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

function validateOrderItems(orderItems) {
  if (!Array.isArray(orderItems)) {
    return { ok: false, message: "orderItems must be an array" };
  }

  if (orderItems.length === 0) {
    return { ok: false, message: "orderItems must not be empty" };
  }

  for (let i = 0; i < orderItems.length; i += 1) {
    const item = orderItems[i];
    if (!item || typeof item !== "object") {
      return { ok: false, message: `orderItems[${i}] must be an object` };
    }

    if (typeof item.id !== "string" || !UUID_REGEX.test(item.id)) {
      return { ok: false, message: `orderItems[${i}].id must be a UUID` };
    }

    if (!Number.isInteger(item.qty) || item.qty <= 0) {
      return {
        ok: false,
        message: `orderItems[${i}].qty must be a positive integer`,
      };
    }
  }

  return { ok: true };
}

function normalizeProduct(product) {
  return {
    productName: product.productName ?? product.name ?? null,
    sku: product.sku ?? null,
    categoryId: product.categoryId ?? product.category?.id ?? null,
    categoryName: product.categoryName ?? product.category?.name ?? null,
    brandId: product.brandId ?? product.brand?.id ?? null,
    brandName: product.brandName ?? product.brand?.name ?? null,
    price: product.price ?? null,
    shortDesc: product.shortDesc ?? product.shortDescription ?? null,
  };
}

async function fetchProduct(productId) {
  const response = await fetch(
    `${PRODUCT_SERVICE_BASE_URL}/products/${productId}`
  );

  const contentType = response.headers.get("content-type") || "";
  const payload = contentType.includes("application/json")
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const error = new Error(
      `Product service responded with status ${response.status}`
    );
    error.status = response.status;
    error.payload = payload;
    throw error;
  }

  if (!payload || typeof payload !== "object") {
    const error = new Error("Product service returned invalid JSON");
    error.status = 502;
    throw error;
  }

  return payload.data ?? payload;
}

app.post("/createOrder", async (req, res) => {
  const { orderItems } = req.body ?? {};
  const validation = validateOrderItems(orderItems);

  if (!validation.ok) {
    return res.status(400).json({ error: validation.message });
  }

  try {
    const enrichedItems = await Promise.all(
      orderItems.map(async (item) => {
        const product = await fetchProduct(item.id);
        return {
          ...item,
          ...normalizeProduct(product),
        };
      })
    );

    const orderId = crypto.randomUUID();
    return res.status(201).json({ orderId, orderItems: enrichedItems });
  } catch (error) {
    const status =
      typeof error.status === "number" && error.status === 404 ? 400 : 502;

    return res.status(status).json({
      error: "Unable to enrich order items",
      details: error.message,
    });
  }
});

app.get("/", (_req, res) => {
  res.json({ status: "ok" });
});

app.listen(PORT, () => {
  console.log(`Order service listening on port ${PORT}`);
});
