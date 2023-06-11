# GitLab Crawler
Projet utilisant l'API GraphQL de GitLab, pour aggréger des infos sur les projets.


## Configuration
### Accès API
- Créer un token d'accès GitLab, avec les droits en lecture sur l'API
- utiliser ce token dans le fichier `config.properties` :
  - `gitlab.privateToken=<gitlab token>`


### IntelliJ
- Vérifier que la structure du projet est bien configurée
- Acéder au menu : `File | Project Structure | Project Settings | Modules`
  - Java : src/main/java
  - Ressources : src/main/ressources


## Todo
- [x] Socle de base avec requête à l'API
- [x] Scan des projets Java (pom.xml)
- [ ] Scan des projets PHP/Drupal
- [ ] Scan des projets C#
- [ ] Scan des projets NPM
- [ ] Analyse et reporting des résultats


## Exemple de retour
```
[mar. avr. 19 21:51:00 CEST 2022] INFO: Search file in project : project/name1
[mar. avr. 19 21:51:04 CEST 2022] INFO: Search file in project : project/name2
[mar. avr. 19 21:51:05 CEST 2022] INFO: Processing project with POM : project/name1
[mar. avr. 19 21:51:06 CEST 2022] INFO: Result : {org.apache.commons=2, org.assertj=1, org.mapstruct=2...}  
```
