# Configuration Reference

Ce document décrit toutes les propriétés de configuration disponibles dans Modulythe Core Framework.

## Table des matières

- [Sécurité - Resource Server](#sécurité---resource-server)
- [Sécurité - Client OAuth2](#sécurité---client-oauth2)
- [Gestion des erreurs](#gestion-des-erreurs)
- [Client REST](#client-rest)

---

## Sécurité - Resource Server

Configuration de l'authentification entrante pour votre API (Resource Server).

| Propriété                              | Type     | Défaut         | Description                                                    |
|----------------------------------------|----------|----------------|----------------------------------------------------------------|
| `modulythe.security.mode`              | `String` | `jwt`          | Mode d'authentification : `jwt` ou `opaque`                    |
| `modulythe.security.introspection-uri` | `String` | -              | URI d'introspection du token (requis si mode = `opaque`)       |
| `modulythe.security.client-id`         | `String` | -              | Client ID pour l'introspection (requis si mode = `opaque`)     |
| `modulythe.security.client-secret`     | `String` | -              | Client Secret pour l'introspection (requis si mode = `opaque`) |
| `modulythe.security.token-endpoint`    | `String` | `/oauth/token` | Endpoint pour l'échange de token                               |

### Exemple - Mode JWT (défaut)

```yaml
modulythe:
    security:
        mode: jwt

spring:
    security:
        oauth2:
            resourceserver:
                jwt:
                    issuer-uri: https://auth.example.com/realms/my-realm
```

### Exemple - Mode Opaque Token

```yaml
modulythe:
    security:
        mode: opaque
        introspection-uri: https://auth.example.com/oauth/introspect
        client-id: my-service
        client-secret: ${INTROSPECTION_SECRET}
```

---

## Sécurité - Client OAuth2

Configuration pour les appels sortants vers d'autres services (Client Credentials Flow).

| Propriété                                            | Type      | Défaut        | Description                                            |
|------------------------------------------------------|-----------|---------------|--------------------------------------------------------|
| `modulythe.security.client.enabled`                  | `boolean` | `false`       | Active/désactive l'intercepteur d'authentification     |
| `modulythe.security.client.client-id`                | `String`  | -             | Identifiant du client OAuth2                           |
| `modulythe.security.client.audience-url`             | `String`  | -             | URL de l'audience pour le JWT assertion                |
| `modulythe.security.client.key-alias`                | `String`  | -             | Alias de la clé privée dans le keystore                |
| `modulythe.security.client.trust-store-path`         | `String`  | -             | Chemin vers le keystore (JKS)                          |
| `modulythe.security.client.key-store-password`       | `String`  | -             | Mot de passe du keystore                               |
| `modulythe.security.client.scope`                    | `String`  | `read write`  | Scopes demandés pour l'access token                    |
| `modulythe.security.client.expire-time-in-seconds`   | `int`     | `300`         | Durée de validité du JWT assertion (secondes)          |
| `modulythe.security.client.issued-at-offset-seconds` | `int`     | `-30`         | Offset pour le claim "iat" (clock skew)                |
| `modulythe.security.client.caller-id-key`            | `String`  | `X-Caller-ID` | Header pour identifier l'appelant                      |
| `modulythe.security.client.caller-id-value`          | `String`  | -             | Valeur du Caller ID                                    |
| `modulythe.security.client.refresh-rate`             | `long`    | `3000000`     | Intervalle de rafraîchissement du token (ms) - ~50 min |
| `modulythe.security.client.max-retries`              | `int`     | `5`           | Nombre max de tentatives en cas d'échec                |
| `modulythe.security.client.initial-backoff-ms`       | `long`    | `1000`        | Délai initial entre les tentatives (ms)                |
| `modulythe.security.client.max-backoff-ms`           | `long`    | `60000`       | Délai maximum entre les tentatives (ms)                |

### Exemple complet

```yaml
modulythe:
    security:
        client:
            enabled: true
            client-id: my-service
            audience-url: https://auth.example.com/oauth/token
            key-alias: my-service-key
            trust-store-path: classpath:keystore.jks
            key-store-password: ${KEYSTORE_PASSWORD}
            scope: api:read api:write
            expire-time-in-seconds: 300
            issued-at-offset-seconds: -30
            caller-id-key: X-Caller-ID
            caller-id-value: my-service-v1
            refresh-rate: 2400000  # 40 minutes
            max-retries: 3
            initial-backoff-ms: 2000
            max-backoff-ms: 30000
```

---

## Gestion des erreurs

Configuration du comportement des handlers d'exception.

| Propriété                            | Type      | Défaut  | Description                                        |
|--------------------------------------|-----------|---------|----------------------------------------------------|
| `modulythe.exception.expose-details` | `boolean` | `false` | Expose les messages d'erreur détaillés aux clients |

### Comportement

- **`false` (production)** : Les messages d'erreur sont remplacés par un message générique avec un ID de référence.
  L'erreur complète est loggée côté serveur.
- **`true` (développement)** : Les messages d'erreur bruts sont renvoyés au client.

### Exemple

```yaml
# Production
modulythe:
    exception:
        expose-details: false

# Développement
modulythe:
    exception:
        expose-details: true
```

### Format de réponse d'erreur

```json
{
  "timestamp": "2024-01-25T10:30:00",
  "status": 500,
  "error": "Unexpected Error",
  "message": "An unexpected error occurred. Please contact support with reference ID: A1B2C3D4",
  "path": "/api/users"
}
```

---

## Client REST

Configuration du client REST technique.

| Propriété                        | Type      | Défaut | Description                                   |
|----------------------------------|-----------|--------|-----------------------------------------------|
| `modulythe.rest.ssrf-protection` | `boolean` | `true` | Active la protection contre les attaques SSRF |

### Protection SSRF

Quand activée, la protection SSRF bloque les requêtes vers :

- `localhost`, `127.0.0.1`, `0.0.0.0`
- Adresses IPv6 loopback (`::1`)
- Adresses IP privées (10.x.x.x, 172.16.x.x, 192.168.x.x)
- Endpoints de métadonnées cloud (169.254.169.254, metadata.google.internal)

### Exemple

```yaml
# Production (défaut)
modulythe:
    rest:
        ssrf-protection: true

# Désactiver pour les appels internes de confiance (non recommandé)
modulythe:
    rest:
        ssrf-protection: false
```

---

## Configuration complète - Exemple

```yaml
modulythe:
    # Resource Server (authentification entrante)
    security:
        mode: jwt
        token-endpoint: https://auth.example.com/oauth/token

        # Client OAuth2 (appels sortants)
        client:
            enabled: true
            client-id: my-service
            audience-url: https://auth.example.com/oauth/token
            key-alias: my-service-key
            trust-store-path: classpath:keystore.jks
            key-store-password: ${KEYSTORE_PASSWORD}
            scope: api:read api:write
            refresh-rate: 2400000
            max-retries: 3

    # Gestion des erreurs
    exception:
        expose-details: false

    # Client REST
    rest:
        ssrf-protection: true

# Configuration Spring Security standard
spring:
    security:
        oauth2:
            resourceserver:
                jwt:
                    issuer-uri: https://auth.example.com/realms/my-realm
```

---

## Variables d'environnement recommandées

Pour les secrets, utilisez des variables d'environnement :

```bash
# Keystore
export KEYSTORE_PASSWORD=changeit

# Introspection (si mode opaque)
export INTROSPECTION_SECRET=my-secret

# Autres secrets
export CLIENT_SECRET=my-client-secret
```

Référencez-les dans votre configuration :

```yaml
modulythe:
    security:
        client:
            key-store-password: ${KEYSTORE_PASSWORD}
        client-secret: ${INTROSPECTION_SECRET}
```
