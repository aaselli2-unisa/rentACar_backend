#!/bin/bash
# =============================================================================
# build-push.sh — Build immagini Docker e push su Docker Hub
# =============================================================================
# Prerequisito — login Docker Hub:
#   docker login -u ben03030303
#
# Uso:
#   bash k8s/build-push.sh          # push con tag :latest
#   bash k8s/build-push.sh v1.2.3   # push con tag specifico
# =============================================================================

set -e  # Interrompi al primo errore

# Directory del backend (cartella dove sta questo script + ..)
BACKEND_DIR="$(dirname "$0")/.."

# Directory del frontend (cartella sorella al backend)
FRONTEND_DIR="${BACKEND_DIR}/../rent-a-car-frontend-project"

# Registry Docker Hub
REGISTRY="ben03030303"

# Tag da usare: primo argomento passato allo script, default "latest"
TAG="${1:-latest}"

echo "=== Building backend image ==="
# Builda l'immagine Spring Boot usando il Dockerfile nella root del backend.
docker build -t "${REGISTRY}/rentacar-backend:${TAG}" "${BACKEND_DIR}"

echo "=== Building frontend image ==="
# Builda l'immagine React+nginx usando il Dockerfile nella root del frontend.
# L'immagine contiene solo i file React compilati e nginx di base.
# La configurazione nginx (proxy_pass, security headers, ecc.) NON è nell'immagine:
# viene iniettata a runtime dal ConfigMap nginx-conf (04b-nginx-config.yaml)
# montato come volume in /etc/nginx/conf.d/default.conf.
# Risultato: immagine build React ottimizzata.
docker build -t "${REGISTRY}/rentacar-frontend:${TAG}" "${FRONTEND_DIR}"

echo "=== Pushing images to Docker Hub ==="
# Push entrambe le immagini su Docker Hub.
# Dopo il push, il server Ubuntu può fare "docker pull" (o microk8s pull)
docker push "${REGISTRY}/rentacar-backend:${TAG}"
docker push "${REGISTRY}/rentacar-frontend:${TAG}"

echo "=== Done ==="
echo "Images pushed:"
echo "  ${REGISTRY}/rentacar-backend:${TAG}"
echo "  ${REGISTRY}/rentacar-frontend:${TAG}"
echo ""
echo "Restart dei pod per usare le nuove immagini:"
echo "  microk8s kubectl rollout restart deployment/rentacar-app -n rentacar"
echo "  microk8s kubectl rollout restart deployment/rentacar-frontend -n rentacar"
