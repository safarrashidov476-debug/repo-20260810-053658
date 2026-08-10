# SSH Manager — Android Ilova

Serverni SSH orqali boshqarish uchun oddiy Android ilova.

## Imkoniyatlar

- Server IP, port, username va parol kiritish
- SSH orqali ulanish
- Buyruqlarni bajarish (`ls`, `df -h`, `systemctl`, `docker` va h.k.)
- Natijani real vaqtda ko‘rish

## GitHub orqali APK qurish

1. Bu repone GitHub’ga yuklang (yangi repository yarating).
2. **Actions** bo‘limiga o‘ting.
3. **Build APK** workflow’ni ishga tushiring (yoki `main` branchga push qiling).
4. Ish tugagach **Artifacts** bo‘limidan `SSHManager-debug` ni yuklab oling.
5. Telefoningizga o‘rnating (Unknown sources ruxsatini bering).

### Manual ishga tushirish
- Repository → Actions → Build APK → Run workflow

## Lokal qurish (Android Studio)

1. Android Studio’da oching.
2. Sync Project with Gradle Files
3. Run → app (yoki Build → Build Bundle(s) / APK(s) → Build APK(s))

## Eslatma

- Faqat debug APK chiqadi (imzolangan release uchun keystore kerak).
- Productionda `StrictHostKeyChecking` ni yoqing va parollarni xavfsiz saqlang.
