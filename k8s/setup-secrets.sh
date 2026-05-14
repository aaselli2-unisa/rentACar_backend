#!/bin/bash

# setup-secrets.sh — Crea K8s Secret dai file ./secrets/

# Questo script va eseguito UNA VOLTA sul server Ubuntu PRIMA di fare deploy.
# Legge i file di secret dalla directory ./secrets/ del progetto e li carica
# in K8s come un Secret chiamato "rentacar-secrets" nel namespace "rentacar".
#
# Perché questo approccio:
#   I secret NON devono stare nei manifest YAML (non committare credenziali su git).
#   Il Secret K8s viene creato direttamente sul server dal contenuto dei file,
#   che esistono solo localmente (./secrets/ è in .gitignore).
#
# Come funziona "--dry-run=client -o yaml | kubectl apply":
#   Invece di "kubectl create secret" (che fallisce se il secret esiste già),
#   questa tecnica genera il manifest in memoria e usa "apply" che fa upsert:
#   crea se non esiste, aggiorna se esiste. Idempotente.
#
# Uso:
#   Sul server Ubuntu, nella directory del progetto:
#   bash k8s/setup-secrets.sh


set -e  # Interrompi lo script al primo errore (non continuare se qualcosa fallisce)

# Percorso alla directory secrets relativo a questo script
SECRETS_DIR="$(dirname "$0")/../secrets"

# Crea il namespace se non esiste già.
# "--dry-run=client -o yaml | apply" è idempotente: non fallisce se esiste.
microk8s kubectl create namespace rentacar --dry-run=client -o yaml | microk8s kubectl apply -f -

# Crea il Secret "rentacar-secrets" con tutti e 7 i file di secret.
# "--from-file=CHIAVE=PERCORSO" legge il contenuto del file e lo salva
# nel Secret con quella chiave. K8s lo cifra in base64 internamente.
#
# Ogni chiave corrisponde a un file che verrà montato in /run/secrets/:
#   DB_PASSWORD         → /run/secrets/DB_PASSWORD  (letto da postgres e spring boot)
#   JWT_SECRET          → /run/secrets/JWT_SECRET   (letto da spring boot per firmare JWT)
#   CLOUDINARY_*        → /run/secrets/CLOUDINARY_* (API per upload immagini auto)
#   MAIL_USERNAME/PASSWORD → /run/secrets/MAIL_*   (credenziali Gmail SMTP)
microk8s kubectl create secret generic rentacar-secrets \
  --namespace=rentacar \
  --from-file=DB_PASSWORD="${SECRETS_DIR}/DB_PASSWORD" \
  --from-file=JWT_SECRET="${SECRETS_DIR}/JWT_SECRET" \
  --from-file=CLOUDINARY_CLOUD_NAME="${SECRETS_DIR}/CLOUDINARY_CLOUD_NAME" \
  --from-file=CLOUDINARY_API_KEY="${SECRETS_DIR}/CLOUDINARY_API_KEY" \
  --from-file=CLOUDINARY_API_SECRET="${SECRETS_DIR}/CLOUDINARY_API_SECRET" \
  --from-file=MAIL_USERNAME="${SECRETS_DIR}/MAIL_USERNAME" \
  --from-file=MAIL_PASSWORD="${SECRETS_DIR}/MAIL_PASSWORD" \
  --dry-run=client -o yaml | microk8s kubectl apply -f -

echo "Secrets creati/aggiornati con successo nel namespace rentacar."