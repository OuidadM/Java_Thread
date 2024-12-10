Devoir Libre : Java Thread - Orders Processing

Un fichier texte nommé orders.txt, situé dans le répertoire /data, est lu, transformé en JSON, puis stocké dans le répertoire /data/input.
Un thread s'exécute toutes les heures pour effectuer les tâches suivantes :
Lire les fichiers JSON présents dans le répertoire /data/input.
Vérifier si le client correspondant à chaque commande existe.
Si le client existe :
Déplacer le fichier dans le répertoire /data/output.
Ajouter la commande dans la base de données.
Si le client n'existe pas :
Déplacer le fichier dans le répertoire /data/error.
