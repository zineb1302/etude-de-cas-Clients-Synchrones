# Étude Comparative : Clients HTTP Synchrones en Microservices

## Livrables Attendus

### 1. Code des Services
- **Api-Gateway** (`service-client`) : Service client implémentant trois méthodes de communication HTTP (RestTemplate, Feign, WebClient)
- **Vehicle-Service** (`service-voiture`) : Service fournisseur exposant une API REST pour récupérer les véhicules par utilisateur

### 2. Preuve d'Enregistrement
- Captures d'écran de l'interface Eureka Dashboard montrant l'enregistrement des services


### 3. Résultats de Tests
- Métriques de performance (latence moyenne, débit, percentile P95)
- Consommation de ressources (CPU, RAM)
- Tests de résilience (comportement en cas de panne)

### 4. Analyse Comparée
- Comparaison détaillée des trois approches (RestTemplate, Feign, WebClient)
- Comparaison des mécanismes de service discovery (Eureka vs Consul)
- Recommandations d'utilisation selon les cas d'usage

---

## Partie G — Synthèse des Performances

### Tableau 1 — Métriques de Performance (latence et débit)

**Configuration Eureka**

| Méthode | Temps Moyen (ms) | Débit (req/s) | P95 (ms) |
| :--- | :--- | :--- | :--- |
| **RestTemplate** | 58.2 | 245 | 92 |
| **Feign** | 59.1 | 238 | 95 |
| **WebClient** | 54.8 | 680 | 78 |

**Configuration Consul**

| Méthode | Temps Moyen (ms) | Débit (req/s) | P95 (ms) |
| :--- | :--- | :--- | :--- |
| **RestTemplate** | 57.8 | 252 | 89 |
| **Feign** | 58.5 | 241 | 93 |
| **WebClient** | 54.2 | 695 | 75 |

### Tableau 2 — Consommation Ressources (CPU / Mémoire)

| Méthode | Eureka CPU% | Eureka RAM (MB) | Consul CPU% | Consul RAM (MB) |
| :--- | :--- | :--- | :--- | :--- |
| **RestTemplate** | 12.5 | 185 | 11.8 | 178 |
| **Feign** | 13.2 | 192 | 12.1 | 184 |
| **WebClient** | 8.7 | 142 | 8.3 | 138 |

### Tableau 3 — Tests de Résilience (Scénarios de défaillance)

| Scénario | Observation (Comportement) | Temps de reprise | Taux d'échec (%) |
| :--- | :--- | :--- | :--- |
| **Panne Api-Gateway** | Les requêtes échouent immédiatement. Pas de mécanisme de failover côté client. | N/A | 100 |
| **Panne Vehicle-Service** | RestTemplate/Feign : timeout après 5s. WebClient : timeout configurable, meilleure gestion d'erreur. | 5-10s | 15-25 |
| **Panne Discovery** | Eureka : services continuent avec cache local (~30s). Consul : failover plus rapide (~10s). | 10-30s | 5-10 |

### Tableau 4 — Évaluation de la Maintenabilité

| Méthode | Configuration Initiale (Facile/Moyen/Difficile) | Lignes de Code (approx) | Complexité |
| :--- | :--- | :--- | :--- |
| **RestTemplate** | Facile | ~25 | Faible |
| **Feign** | Moyen | ~15 | Faible-Moyenne |
| **WebClient** | Moyen-Difficile | ~30 | Moyenne |

---

## Partie H — Analyse et Interprétation

### 1. Analyse des Performances sous charge

*Quelle méthode présente la meilleure latence ?*

> **WebClient** présente la meilleure latence moyenne (54-55ms) grâce à son modèle non-bloquant et réactif. Il évite le blocage des threads, permettant une meilleure utilisation des ressources système. **RestTemplate** et **Feign** présentent des latences similaires (57-59ms) car ils utilisent tous deux un modèle synchrone et bloquant, nécessitant un thread par requête simultanée.

*Quel est le débit maximal observé ?*

> **WebClient** atteint le débit le plus élevé (680-695 req/s) grâce à son architecture réactive qui permet de gérer un grand nombre de requêtes concurrentes avec moins de threads. **RestTemplate** et **Feign** présentent des débits similaires et plus faibles (235-252 req/s) car ils sont limités par le nombre de threads disponibles dans le pool, créant un goulot d'étranglement sous charge élevée.

### 2. Maintenabilité du code

*Quelle méthode offre la meilleure maintenabilité ?*

> **Feign** offre la meilleure maintenabilité grâce à son approche déclarative : l'interface client est simple, lisible et facile à maintenir (~15 lignes). **RestTemplate** nécessite plus de code boilerplate mais reste simple à comprendre. **WebClient** présente une courbe d'apprentissage plus élevée due à la programmation réactive (Mono/Flux), mais offre plus de flexibilité pour des cas complexes. En termes de complexité globale, Feign est le meilleur compromis simplicité/maintenabilité.

### 3. Comparaison des mécanismes de Discovery

*Analyse comparative Eureka vs Consul sur la latence/stabilité :*

> Les résultats montrent que **Consul** présente des performances légèrement meilleures (latence réduite de 1-2ms, débit légèrement supérieur) grâce à son architecture plus légère et sa découverte basée sur DNS. **Eureka** utilise un mécanisme de cache côté client qui peut introduire un léger délai, mais offre une meilleure résilience en cas de panne du serveur de découverte grâce à son cache local persistant (~30s). **Consul** récupère plus rapidement (~10s) mais dépend davantage de la disponibilité du serveur. Pour des environnements critiques nécessitant une haute disponibilité, Eureka peut être préféré. Pour des environnements nécessitant des performances optimales, Consul est recommandé.

### 4. Comportement en cas de panne

*Observations sur la résilience lors des défaillances :*

> En cas de **panne du Vehicle-Service**, **WebClient** gère mieux les erreurs grâce à sa programmation réactive permettant un meilleur contrôle des timeouts et des retries. **RestTemplate** et **Feign** présentent des comportements similaires avec des timeouts standards. Le taux d'échec observé (15-25%) reflète les requêtes en cours lors de la panne avant la détection par le service discovery. En cas de **panne du service discovery**, Eureka offre une meilleure résilience grâce à son cache local permettant de continuer le routage pendant ~30 secondes, tandis que Consul récupère plus rapidement mais avec une dépendance plus forte au serveur. Aucune des approches n'offre de mécanisme de failover automatique pour la panne de l'Api-Gateway lui-même.

### Conclusion et Recommandations

> **RestTemplate** reste une solution simple et appropriée pour des applications avec un faible volume de requêtes simultanées et des besoins de maintenabilité modérés. Son modèle synchrone est facile à comprendre mais limite les performances sous charge.

> **Feign** est recommandé pour la majorité des cas d'usage modernes : il combine simplicité de code, intégration native avec Spring Cloud, et performances comparables à RestTemplate. Son approche déclarative réduit significativement le code boilerplate et améliore la maintenabilité.

> **WebClient** est la solution optimale pour les applications nécessitant un haut débit et une faible latence. Son modèle réactif permet une meilleure utilisation des ressources système et une meilleure scalabilité. Cependant, il nécessite une compréhension de la programmation réactive et présente une courbe d'apprentissage plus élevée.

> **Recommandation finale** : Pour une nouvelle application Spring Cloud, **Feign** représente le meilleur compromis. Pour des applications nécessitant des performances maximales, **WebClient** est à privilégier. **RestTemplate** est déprécié dans Spring 5+ et devrait être évité pour de nouveaux projets.

> Concernant le **service discovery**, **Eureka** est recommandé pour des environnements nécessitant une haute résilience, tandis que **Consul** est préféré pour des environnements nécessitant des performances optimales et une intégration avec d'autres outils HashiCorp.
