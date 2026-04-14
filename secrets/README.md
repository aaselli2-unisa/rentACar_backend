# Docker Secrets (local)

Put real secret values in this folder using the exact filenames expected by `docker-compose.yml`:

- `DB_PASSWORD`
- `JWT_SECRET`
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`

Rules:
- Keep one value per file.
- No trailing newline when possible.
- Never commit real secret files.
- Only `*.example` files in this folder are versioned.

