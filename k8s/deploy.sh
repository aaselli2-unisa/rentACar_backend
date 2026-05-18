#!/bin/bash

# deploy.sh — Applica tutti i manifest K8s sul server Ubuntu

# Questo script va eseguito sul server Ubuntu dopo aver:
#   1. Buildato e pushato le immagini
#   2. Creato i secret (setup-secrets.sh sul server)
#
# "kubectl apply" è idempotente: crea le risorse se non esistono,
# le aggiorna se esistono già.
#
# Ordine di apply:
#   I file sono applicati in ordine numerico (00, 01, 02...) per rispettare
#   le dipendenze: namespace prima di tutto, configmap prima dei deployment, ecc.
#   questo ordine
#   evita errori di riferimento a risorse non ancora create.
#
# Uso:
#   bash k8s/deploy.sh


set -e  # Interrompi al primo errore

K8S_DIR="$(dirname "$0")"  # Directory dove stanno i manifest (stessa di questo script)

echo "=== Applying namespace ==="
microk8s kubectl apply -f "${K8S_DIR}/00-namespace.yaml"
# Deve essere primo: tutti gli altri oggetti appartengono al namespace "rentacar"

echo "=== Applying configmap ==="
microk8s kubectl apply -f "${K8S_DIR}/01-configmap.yaml"
# ConfigMap prima dei Deployment: i Deployment referenziano il ConfigMap via envFrom

echo "=== Applying postgres (PVC + Deployment + Service) ==="
microk8s kubectl apply -f "${K8S_DIR}/02-postgres.yaml"
# PVC, Deployment e Service postgres. Il pod Spring Boot aspetta postgres
# tramite initContainer, quindi l'ordine qui è importante.

echo "=== Applying app (Deployment + Service) ==="
microk8s kubectl apply -f "${K8S_DIR}/03-app.yaml"
# Spring Boot Deployment. L'initContainer aspetta postgres:5432 prima di avviare.

echo "=== Applying frontend (Deployment + Service) ==="
microk8s kubectl apply -f "${K8S_DIR}/04-frontend.yaml"
# nginx frontend Deployment. Si avvia subito, le chiamate API aspettano Spring Boot.

echo "=== Applying ingress ==="
microk8s kubectl apply -f "${K8S_DIR}/05-ingress.yaml"
# Ingress per ultimo: il Service rentacar-frontend deve esistere già.

echo "=== Applying network policies ==="
microk8s kubectl apply -f "${K8S_DIR}/06-networkpolicy.yaml"
# Firewall tra pod: postgres accessibile solo da app, app solo da frontend.

echo ""
echo "=== Deploy completato. Stato pod: ==="
# -w (watch): aggiorna in tempo reale
# Mostra la progressione: Pending -> ContainerCreating -> Running
microk8s kubectl get pods -n rentacar -w
