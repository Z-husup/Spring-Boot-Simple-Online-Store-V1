const ordersStatus = document.getElementById('ordersStatus');
const ordersList = document.getElementById('ordersList');
const ordersUserBadge = document.getElementById('ordersUserBadge');
const ordersCount = document.getElementById('ordersCount');
const ordersSpent = document.getElementById('ordersSpent');

function setStatus(message, kind = 'secondary') {
  const bg = {
    secondary: '#ffffff',
    success: '#ecfdf3',
    danger: '#fef2f2'
  }[kind] || '#ffffff';

  const border = {
    secondary: '#dce3ea',
    success: '#b6ebcc',
    danger: '#fecaca'
  }[kind] || '#dce3ea';

  ordersStatus.textContent = message;
  ordersStatus.style.background = bg;
  ordersStatus.style.border = `1px solid ${border}`;
}

async function requestJson(url, options = {}) {
  const res = await fetch(url, options);
  const text = await res.text();
  if (!res.ok) throw new Error(text || `HTTP ${res.status}`);
  return text ? JSON.parse(text) : null;
}

function renderOrders(orders) {
  ordersList.innerHTML = '';
  ordersCount.textContent = String(orders.length);

  const spent = orders.reduce((sum, o) => sum + Number(o.total || 0), 0);
  ordersSpent.textContent = `$${spent.toFixed(2)}`;

  if (!orders.length) {
    const empty = document.createElement('div');
    empty.className = 'empty-orders';
    empty.textContent = 'No orders yet. Start shopping and place your first order.';
    ordersList.appendChild(empty);
    return;
  }

  for (const order of orders) {
    const card = document.createElement('article');
    card.className = 'order-card';
    card.innerHTML = `
      <div class="order-top">
        <span class="order-id">Order #${order.orderId}</span>
        <span class="order-total">$${Number(order.total).toFixed(2)}</span>
      </div>
      <div class="order-date">Placed on ${new Date(order.createdAt).toLocaleString()}</div>
    `;
    ordersList.appendChild(card);
  }
}

async function loadOrders() {
  try {
    const me = await requestJson('/api/v1/me/profile');
    ordersUserBadge.textContent = `${me.name} (${me.email})`;

    const orders = await requestJson('/api/v1/me/orders');
    renderOrders(orders);
    setStatus(`Loaded ${orders.length} orders.`, 'success');
  } catch (err) {
    setStatus(err.message, 'danger');
  }
}

document.getElementById('refreshOrdersBtn').addEventListener('click', loadOrders);
loadOrders();
