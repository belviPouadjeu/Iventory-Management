# Notes

## Pourquoi utiliser `JOIN FETCH` ?
- Sans `JOIN FETCH`, JPA charge par défaut les relations `@ManyToOne` en `EAGER` ou `LAZY`.

- En `LAZY`, cela fait une requête supplémentaire par catégorie pour récupérer l'entreprise (N+1 problème).

- Avec `JOIN FETCH`, la requête récupère tout en une seule fois, ce qui est plus performant.

## 🔚 Conclusion

| Critère              | Mapping manuel            | `.skip(...)` avec ModelMapper     |
|----------------------|---------------------------|-----------------------------------|
| Lisibilité           | ✅ Très clair              | ❌ Moins explicite                |
| Contrôle local       | ✅ Oui, par méthode        | ❌ Non, global                    |
| Débogage facile      | ✅ Oui                     | ❌ Complexe si erreur            |
| Maintenance          | ✅ Robuste dans le temps   | ❌ Fragile si les DTO évoluent   |
| Configuration unique | ❌ Plus de code            | ✅ Moins de code                 |


## 🔄 En résumé

| Cas                                                                 | `skip()` est recommandé ? | Pourquoi                                                                 |
|---------------------------------------------------------------------|----------------------------|--------------------------------------------------------------------------|
| Mapping d’un DTO avec des relations JPA (comme entreprise, category) | ✅ Oui                     | Pour éviter les conflits et garder la main sur l’assignation             |
| Mapping simple sans ambiguïté                                       | ❌ Non                     | Le mapping automatique suffit                                            |
| Mapping où ModelMapper fait de mauvais choix ou lève des erreurs   | ✅ Oui                     | Pour éviter des erreurs comme celle que tu avais plus haut              |


à automatiser le prix au moment de la commande dans prixUnitaire si tu choisis de le garder ?


## For production
docker-compose --env-file .env.prod up -d


