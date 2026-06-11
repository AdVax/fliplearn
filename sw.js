/* ============================================================
   FlipLearn Service Worker v2 — Full Offline Support
   ============================================================ */

const CACHE = 'fliplearn-v2';

const PRECACHE = [
  './',
  './index.html',
  './manifest.json',
  './icon.svg',
  './icon-maskable.svg',
];

// ── Install: pre-cache app shell ──────────────────────────
self.addEventListener('install', e => {
  e.waitUntil(
    caches.open(CACHE)
      .then(c => c.addAll(PRECACHE))
      .then(() => self.skipWaiting())
      .catch(err => console.warn('[SW] pre-cache failed:', err))
  );
});

// ── Activate: delete old caches ───────────────────────────
self.addEventListener('activate', e => {
  e.waitUntil(
    caches.keys()
      .then(keys => Promise.all(
        keys.filter(k => k !== CACHE).map(k => caches.delete(k))
      ))
      .then(() => self.clients.claim())
  );
});

// ── Fetch ─────────────────────────────────────────────────
self.addEventListener('fetch', e => {
  if (e.request.method !== 'GET') return;

  const url = new URL(e.request.url);
  const sameOrigin   = url.origin === self.location.origin;
  const isGFontsCss  = url.hostname === 'fonts.googleapis.com';
  const isGFontsFile = url.hostname === 'fonts.gstatic.com';

  // ── Google Fonts CSS → Network first, cache fallback ──
  if (isGFontsCss) {
    e.respondWith(
      fetch(e.request)
        .then(res => {
          const clone = res.clone();
          caches.open(CACHE).then(c => c.put(e.request, clone));
          return res;
        })
        .catch(() => caches.match(e.request))
    );
    return;
  }

  // ── Google Fonts files (.woff2) → Cache first ─────────
  if (isGFontsFile) {
    e.respondWith(
      caches.match(e.request).then(cached => {
        if (cached) return cached;
        return fetch(e.request).then(res => {
          const clone = res.clone();
          caches.open(CACHE).then(c => c.put(e.request, clone));
          return res;
        });
      })
    );
    return;
  }

  // ── App shell → Cache first, network fallback ─────────
  if (sameOrigin) {
    e.respondWith(
      caches.match(e.request).then(cached => {
        if (cached) return cached;
        return fetch(e.request).then(res => {
          if (res && res.status === 200) {
            const clone = res.clone();
            caches.open(CACHE).then(c => c.put(e.request, clone));
          }
          return res;
        }).catch(() => {
          // Offline fallback: return index.html for navigation
          if (e.request.mode === 'navigate') {
            return caches.match('./index.html');
          }
        });
      })
    );
  }
});
  
