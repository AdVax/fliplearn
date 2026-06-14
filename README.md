# FlipLearn — PWA + Android APK

تطبيق بطاقات تعلم المفردات الإنجليزية، متاح كـ PWA وكتطبيق Android.

---

## هيكل المستودع

```
fliplearn/                     ← جذر المستودع (GitHub repo)
├── .github/
│   └── workflows/
│       └── build-apk.yml     ← ✅ CI/CD: يبني APK تلقائياً عند كل push
│
├── android/                  ← مشروع Android الكامل
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── assets/       ← (فارغ) يُملأ تلقائياً بملفات PWA في CI
│   │   │   ├── java/com/fliplearn/app/
│   │   │   │   └── MainActivity.java
│   │   │   ├── res/
│   │   │   │   ├── values/styles.xml
│   │   │   │   ├── values-v28/styles.xml  (دعم الشق/Notch)
│   │   │   │   └── values-v30/styles.xml  (Android 11+)
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle
│   ├── build.gradle
│   ├── settings.gradle
│   └── gradle.properties
│
├── index.html                ← ✅ ملفات PWA
├── manifest.json
├── sw.js
├── icon.svg
└── icon-maskable.svg
```

---

## كيفية البناء عبر GitHub Actions

### Debug APK (مجاني، بدون توقيع)

1. ارفع هذه الملفات إلى GitHub repository
2. انتظر حتى تنتهي الـ workflow (2-5 دقائق)
3. اذهب إلى: **Actions → Build FlipLearn APK → آخر تشغيل → Artifacts**
4. حمّل `FlipLearn-Debug-Build-X.zip` واستخرج منه الـ APK

### Release APK (موقّع، لـ Google Play)

راجع التعليقات في `.github/workflows/build-apk.yml`.

---

## الإصدارات المستخدمة

| المكوّن               | الإصدار   |
|-----------------------|-----------|
| Android Gradle Plugin | 8.5.2     |
| Gradle                | 8.7       |
| Java (JDK)            | 17 (LTS)  |
| compileSdk            | 35 (Android 15) |
| targetSdk             | 35        |
| minSdk                | 24 (Android 7.0) |
| Build Tools           | 35.0.0    |

---

## ملاحظات تقنية

- **localStorage**: يعمل بالكامل داخل WebView (البيانات محفوظة محلياً على الجهاز)
- **Service Worker**: لا يعمل داخل WebView (متوقع) — التطبيق لا يعتمد عليه للعمل
- **خطوط Google**: تُحمَّل من الإنترنت؛ عند عدم الاتصال يستخدم التطبيق الخطوط الاحتياطية (Tajawal, Arial)
- **الوضع الكامل (Fullscreen)**: مدعوم على API 24+ مع دعم الشقوق (Notch) على API 28+
