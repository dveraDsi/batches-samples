J'ai un hob qui parse des csv : 
les paramètres sont les suivants: "D:\DSIPILOT_V9\TESTWRITER\IN\MRI Montpellier.csv"

> lit un csv de 4963 lignes (documents)
> vérifie que le fichier dans le csv existe physiquement
> insertion de données en bdd 
> créé un fichier txt avec les noms des fichiers

durée de traitement : 64191 ms 
soit: 77 doc / s

-------------------------------------------------------------------------------------

2025-06-13T16:56:04.397+02:00  INFO 29680 --- [dsipilot-csv-parser] [           main] o.s.batch.core.step.AbstractStep         : Step: [csvStep] executed in 1m4s44ms
2025-06-13T16:56:04.417+02:00  INFO 29680 --- [dsipilot-csv-parser] [           main] o.s.b.c.l.s.TaskExecutorJobLauncher      : Job: [SimpleJob: [name=csvJob]] completed with the following parameters: [{'outputFile':'{value=D:\\DSIPILOT_V9\\TESTWRITER\\IN\MRI Montpellier_out.txt, type=class java.lang.String, identifying=true}','filePath':'{value=D:\DSIPILOT_V9\TESTWRITER\IN\MRI Montpellier.csv, type=class java.lang.String, identifying=true}','timestamp':'{value=Fri Jun 13 16:55:00 CEST 2025, type=class java.util.Date, identifying=true}'}] and the following status: [COMPLETED] in 1m4s91ms
✅ Job Status: COMPLETED
Total time: 64191 ms

------------------------------------------------------------------------------------

j'ai un job qui déplace les fichiers et mets à jour la bdd 
les paramètres sont les suivants: 
storagePath="D:/tomcat/webapps/storage-path"  baseUrl="http://localhost:8080/storage-path" reportPath="D:/DSIPILOT_V9/TESTWRITER/IN/reports"

> je recherche tous les documents en bdd qui n'ont pas été déplacés (2 critères: url, storagePath null) et qui sont présents physiquement
> je les déplace vers la destination en les renommant
> je mets à jour les données de la table documents (url, storagePath)
> j'écris un report avec le nb succès erreur ...


2025-06-13T17:05:02.284+02:00  INFO 16388 --- [dsipilot-file-mover] [           main] o.s.batch.core.step.AbstractStep         : Step: [moveDocumentStep] executed in 25s521ms
2025-06-13T17:05:02.326+02:00  INFO 16388 --- [dsipilot-file-mover] [           main] o.s.b.c.l.s.TaskExecutorJobLauncher      : Job: [SimpleJob: [name=moveDocumentJob]] completed with the following parameters: [{'baseUrl':'{value=http://localhost:8080/storage-path, type=class java.lang.String, identifying=true}','storagePath':'{value=D:/tomcat/webapps/storage-path, type=class java.lang.String, identifying=true}','reportPath':'{value=D:/DSIPILOT_V9/TESTWRITER/IN/reports, type=class java.lang.String, identifying=true}','run.id':'{value=20250613170436, type=class java.lang.String, identifying=true}'}] and the following status: [COMPLETED] in 25s603ms

------------------------------------------------------------------------------------
