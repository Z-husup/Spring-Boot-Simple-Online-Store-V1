const statusEl = document.getElementById('adminStatus');

function setStatus(message, kind = 'secondary') {
  statusEl.className = `alert alert-${kind} mb-0`;
  statusEl.textContent = message;
}

async function postJson(url, body, method = 'POST') {
  const res = await fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  });
  const text = await res.text();
  if (!res.ok) throw new Error(text || `HTTP ${res.status}`);
  return text ? JSON.parse(text) : null;
}

document.getElementById('createProductForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const f = e.target;
  try {
    const product = await postJson('/api/v1/products', {
      name: f.name.value,
      description: f.description.value,
      imageUrl: f.imageUrl.value,
      price: Number(f.price.value),
      stock: Number(f.stock.value)
    });
    setStatus(`Created product #${product.id}`, 'success');
    f.reset();
  } catch (err) {
    setStatus(err.message, 'danger');
  }
});

document.getElementById('restockForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const f = e.target;
  try {
    const productId = Number(f.productId.value);
    await postJson(`/api/v1/products/${productId}/stock`, { quantity: Number(f.quantity.value) }, 'PATCH');
    setStatus(`Restocked product #${productId}`, 'success');
  } catch (err) {
    setStatus(err.message, 'danger');
  }
});

document.getElementById('changePriceForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const f = e.target;
  try {
    const productId = Number(f.productId.value);
    await postJson(`/api/v1/products/${productId}/price`, { price: Number(f.price.value) }, 'PATCH');
    setStatus(`Changed price for product #${productId}`, 'success');
  } catch (err) {
    setStatus(err.message, 'danger');
  }
});
