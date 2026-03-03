const cartStatus = document.getElementById('cartStatus');
const cartItemsBody = document.getElementById('cartItemsBody');
const cartTotal = document.getElementById('cartTotal');
const cartGrandTotal = document.getElementById('cartGrandTotal');
const ordersList = document.getElementById('ordersList');
const cartUserBadge = document.getElementById('cartUserBadge');

function status(message, kind = 'secondary') {
  const bg = {
    secondary: '#ffffff',
    success: '#ecfdf3',
    danger: '#fef2f2',
    warning: '#fff7ed'
  }[kind] || '#ffffff';

  const border = {
    secondary: '#dce3ea',
    success: '#b6ebcc',
    danger: '#fecaca',
    warning: '#fed7aa'
  }[kind] || '#dce3ea';

  cartStatus.textContent = message;
  cartStatus.style.background = bg;
  cartStatus.style.border = `1px solid ${border}`;
}

async function requestJson(url, options = {}) {
  const res = await fetch(url, options);
  const text = await res.text();
  if (!res.ok) throw new Error(text || `HTTP ${res.status}`);
  return text ? JSON.parse(text) : null;
}

function renderCart(cart) {
  cartItemsBody.innerHTML = '';

  if (!cart.items.length) {
    const tr = document.createElement('tr');
    tr.innerHTML = `<td colspan="5" class="empty-cart">Your cart is empty.</td>`;
    cartItemsBody.appendChild(tr);
  }

  for (const item of cart.items) {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>
        <div class="item-name">${item.productName}</div>
        <div class="item-meta">Product ID: ${item.productId}</div>
      </td>
      <td class="text-end price-main">$${Number(item.price).toFixed(2)}</td>
      <td class="text-end">${item.quantity}</td>
      <td class="text-end subtotal-main">$${Number(item.lineTotal).toFixed(2)}</td>
      <td class="text-end"><button class="btn btn-sm btn-outline-danger remove-btn">Remove</button></td>
    `;

    tr.querySelector('.remove-btn').addEventListener('click', async () => {
      try {
        const updated = await requestJson(`/api/v1/me/cart/items/${item.productId}`, { method: 'DELETE' });
        renderCart(updated);
        status('Item removed.', 'success');
      } catch (err) {
        status(err.message, 'danger');
      }
    });

    cartItemsBody.appendChild(tr);
  }

  const totalLabel = `$${Number(cart.total).toFixed(2)}`;
  cartTotal.textContent = totalLabel;
  cartGrandTotal.textContent = totalLabel;
}

function renderOrders(orders) {
  ordersList.innerHTML = '';

  if (!orders.length) {
    const li = document.createElement('li');
    li.className = 'list-group-item text-secondary';
    li.textContent = 'No orders yet.';
    ordersList.appendChild(li);
    return;
  }

  for (const o of orders) {
    const li = document.createElement('li');
    li.className = 'list-group-item';
    li.innerHTML = `
      <div class="order-row">
        <span>Order #${o.orderId}</span>
        <strong>$${Number(o.total).toFixed(2)}</strong>
      </div>
      <small class="text-secondary">${new Date(o.createdAt).toLocaleString()}</small>
    `;
    ordersList.appendChild(li);
  }
}

async function loadAll() {
  try {
    const me = await requestJson('/api/v1/me/profile');
    cartUserBadge.textContent = `${me.name} (${me.email})`;

    const cart = await requestJson('/api/v1/me/cart');
    renderCart(cart);

    const orders = await requestJson('/api/v1/me/orders');
    renderOrders(orders);

    status('Cart synced.', 'success');
  } catch (err) {
    status(err.message, 'danger');
  }
}

document.getElementById('clearCartBtn').addEventListener('click', async () => {
  try {
    const cart = await requestJson('/api/v1/me/cart', { method: 'DELETE' });
    renderCart(cart);
    status('Cart cleared.', 'success');
  } catch (err) {
    status(err.message, 'danger');
  }
});

document.getElementById('checkoutBtn').addEventListener('click', async () => {
  try {
    await requestJson('/api/v1/me/orders', { method: 'POST' });
    await loadAll();
    status('Order placed successfully.', 'success');
  } catch (err) {
    status(err.message, 'danger');
  }
});

loadAll();
