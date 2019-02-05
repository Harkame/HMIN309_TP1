# HMIN309_TP1
Event manager, use Android Wear

# Contenu : 
 + Base de donnée SQLite
 + création , suppression, modification et recherche d'évènements
 + reconnaissance vocal permettant d'effectuer des actions sur l'application (passage d'un fragment à l'autre..)
 + Géolocalisation
 + Notification sur le téléphone et sur la montre
  + notification qui permet de lancer une activité sur la montre(utilisation des capteurs)
  
# Installation : 
  
Pour l'émulateur de la montre il faut saisir la commande suivante dans le repertoire 
```Users/votre-compte/AppData/Local/Android/Sdk/platform-tools```

Puis il faudra taper la commande : 
```adb -d forward tcp:5601 tcp:5601```  

Pour le reste il suffit de récupérer le projet, de le build puis de l'exécuter
