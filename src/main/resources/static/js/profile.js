const profileStatus = document.getElementById('profileStatus');
const profileUserBadge = document.getElementById('profileUserBadge');
const emailInput = document.getElementById('emailInput');
const nameInput = document.getElementById('nameInput');

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

  profileStatus.textContent = message;
  profileStatus.style.background = bg;
  profileStatus.style.border = `1px solid ${border}`;
}

async function requestJson(url, options = {}) {
  const res = await fetch(url, options);
  const text = await res.text();
  if (!res.ok) throw new Error(text || `HTTP ${res.status}`);
  return text ? JSON.parse(text) : null;
}

async function loadProfile() {
  try {
    const me = await requestJson('/api/v1/me/profile');
    emailInput.value = me.email;
    nameInput.value = me.name || '';
    profileUserBadge.textContent = `${me.name} (${me.email})`;
    setStatus('Profile loaded.', 'success');
  } catch (err) {
    setStatus(err.message, 'danger');
  }
}

document.getElementById('profileForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  try {
    const updated = await requestJson('/api/v1/me/profile', {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: nameInput.value })
    });

    emailInput.value = updated.email;
    nameInput.value = updated.name || '';
    profileUserBadge.textContent = `${updated.name} (${updated.email})`;
    setStatus('Profile updated.', 'success');
  } catch (err) {
    setStatus(err.message, 'danger');
  }
});

document.getElementById('reloadProfileBtn').addEventListener('click', loadProfile);

loadProfile();
