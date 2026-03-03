const productGrid = document.getElementById('productGrid');
const storeStatus = document.getElementById('storeStatus');
const meBadge = document.getElementById('meBadge');
const searchInput = document.getElementById('searchInput');

let allProducts = [];

function setStatus(message, kind = 'light') {
  storeStatus.className = `alert alert-${kind} border`;
  storeStatus.textContent = message;
}

async function requestJson(url, options = {}) {
  const res = await fetch(url, options);
  const text = await res.text();
  if (!res.ok) throw new Error(text || `HTTP ${res.status}`);
  return text ? JSON.parse(text) : null;
}

async function loadMe() {
  const me = await requestJson('/api/v1/me/profile');
  meBadge.textContent = `${me.name} (${me.email})`;
}

function ratingFor(id) {
  const score = 3.9 + ((id % 12) / 10);
  return Math.min(5, Number(score.toFixed(1)));
}

function stars(value) {
  const full = Math.floor(value);
  const half = value % 1 >= 0.5 ? 1 : 0;
  return '★'.repeat(full) + (half ? '☆' : '') + '☆'.repeat(5 - full - half);
}

function imageOrFallback(url) {
  if (url && url.trim().length > 0) return url;
  return 'https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=1200';
}

async function loadProducts() {
  try {
    allProducts = await requestJson('/api/v1/me/products');
    renderProducts(allProducts);
    setStatus(`Loaded ${allProducts.length} products.`, 'success');
  } catch (err) {
    setStatus(err.message, 'danger');
  }
}

function renderProducts(products) {
  productGrid.innerHTML = '';

  if (!products.length) {
    setStatus('No products match your search.', 'warning');
    return;
  }

  for (const p of products) {
    const rating = ratingFor(p.id || 1);

    const col = document.createElement('div');
    col.className = 'col-12 col-sm-6 col-lg-4 col-xxl-3';
    col.innerHTML = `
      <article class="product-card">
        <div class="product-image-wrap">
          <img src="${imageOrFallback(p.imageUrl)}" alt="${p.name}" class="product-image" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=1200'">
        </div>
        <div class="product-body">
          <div class="d-flex justify-content-between align-items-start gap-2 mb-1">
            <h3 class="product-title">${p.name}</h3>
            <span class="stock-chip">Stock ${p.stock}</span>
          </div>

          <div class="rating-stars mb-1">${stars(rating)} <span class="text-dark">${rating}</span></div>
          <p class="text-secondary small mb-2">${p.description || ''}</p>

          <div class="product-price"><small>$</small>${Number(p.price).toFixed(2)}</div>

          <div class="d-flex gap-2 mt-2">
            <input type="number" min="1" value="1" class="form-control form-control-sm qty-input">
            <button class="btn btn-warning btn-sm add-btn flex-grow-1">Add to Cart</button>
          </div>
        </div>
      </article>`;

    const qtyInput = col.querySelector('.qty-input');
    const addBtn = col.querySelector('.add-btn');

    addBtn.addEventListener('click', async () => {
      try {
        await requestJson('/api/v1/me/cart/items', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ productId: p.id, quantity: Number(qtyInput.value) })
        });
        setStatus(`Added ${qtyInput.value} x ${p.name} to cart.`, 'success');
      } catch (err) {
        setStatus(err.message, 'danger');
      }
    });

    productGrid.appendChild(col);
  }
}

function applySearch() {
  const q = searchInput.value.trim().toLowerCase();
  if (!q) {
    renderProducts(allProducts);
    return;
  }

  const filtered = allProducts.filter(p =>
    p.name.toLowerCase().includes(q) ||
    (p.description || '').toLowerCase().includes(q)
  );
  renderProducts(filtered);
}

searchInput.addEventListener('input', applySearch);
document.getElementById('refreshProducts').addEventListener('click', loadProducts);

(async () => {
  try {
    await loadMe();
  } catch (e) {
    meBadge.textContent = 'Unknown user';
  }
  await loadProducts();
})();
