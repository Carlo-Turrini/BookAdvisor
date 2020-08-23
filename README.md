# BookAvisor Project
Per questo progetto ho sviluppato un'applicazione web utilizzando Spring Boot e Angular. Tra le altre dipendenze rilevanti troviamo quella di un server Redis, necessario per alcuni check di sicurezza.
Il DBMS scelto per l'applicativo è SQLServer, tuttavia, per comodità, l'applicativo può anche essere avviato in modalità test utilizzando un H2, ovvero un database in-memory.

Di seguito verranno fornite alcune indicazioni relative al corretto deployment dell'applicativo per poterlo utilizzare.

## Passi per eseguire BookAdvisor
### 1. Avviare Redis
Come prima cosa bisogna ricordarsi di avviare un'istanza di Redis. Di default l'applicativo suppone che tale istanza sia in ascolto sulla porta 6379 e che l'host sia localhost. Se ciò non dovesse essere vero queste informazioni vanno modificate all'interno della classe RedisUtil che si trova nel package security.redis.
### 2. Eseguire con SQL Server
Per poter eseguire l'applicativo con SQL Server bisognerà prima modificare le impostazioni del dataSource in application.properties di modo che riflettano le impostazioni di connessione al DB locale.
### 3. Eseguire con H2
L'esecuzione con H2 è consigliata se non si vuole creare un DB SQL Server.
L'utilizzo è molto semplice, dopo aver eseguito il build dell'applicativo tramite: mvn clean install, si potrà eseguire il jar così creato con il profilo di test per avviare il server con H2 utilizzando il seguente comando:
java -jar -Dspring.profiles.active=test target/book_advisor-0.0.1-SNAPSHOT.jar
### Nota sugli utenti ADMIN e i DB
Nell'applicativo non è prevista la possibilità per gli utenti di modificare il proprio ruolo (USER o ADMIN). L'amministratore del DB è l'unico a poter fornire agli utenti l'autorità di ADMIN.
Per quanto riguarda H2 essendo un DB in-memory che viene ricreato ogni volta chec si fa partire l'applicativo, è stato creato un file initialData.sql all'interno del package resources con delle istanze con cui popolare il DB, fra cui anche un utente con autorità ADMIN.
### 4. Cambiare luoghi di stoccaggio immagini
Le immagini non vengono salvate all'interno del DB ma in locale di conseguenza bisogna ridefinire la rootDir all'interno della classe FolderService che si trova nel package services.storage
### 5. Angular
Per tutte le informazioni relative ad angular si faccia riferimento alla sezione AngularClient.
### Nota relativa ai test Cypress
Siccome nell'applicativo non è previsto un metodo per cancellare i generi bisogna assicurarsi di fare il restart del server in modalità test ogni volta prima di rieseguire i test e2e.  
### Nota relativa al plugin maven surefire
Ogni tanto capita che a causa della numerosità dei test e della pesantezza dei test di integrazione scritti sfruttando le utility fornite da Spring, il plugin maven surefire fallisca. Nel pom.xml ho configurato i commandLine arguments in modo tale da allocare memoria sufficiente a evitare che questo succeda, almeno sul mio PC. Se dovesse fallire il plugin si provi a modificare tali argomenti nel pom.
### 6. Build con Maven
Per eseguire il build con maven basterà eseguire:

* mvn clean install

Questo comando, grazie alla nostra configurazione del pom si occuperà di installare in locale, non globalmente sul pc, node e npm, necessari per angular. Poi si occuperà di installare le dipendenze di angular tramite **npm install** e di fare il build di angular eseguendo lo script build tramite **npm run build**. Infine sposterà le risorse buildate di angular all'interno di target affinché spring possa fornirle correttamente.
Successivamente maven esguirà le varie fasi del suo default lifecycle come al solito. Il prodotto finale sarà un jar eseguibile.

Notiamo tuttavia che sarà necessario, per il corretto build di angular, avere installato l'angular cli nel proprio sistema.

Notiamo inoltre che se abbiamo già installato node e npm possiamo cancellare dalle execution del maven-frontend-plugin la parte relativa all'installazione degli stessi.
# Angularclient

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 8.3.19.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Cypress](http://www.cypress.io/).
Si noti inoltre che per eseguire correttamente i test utilizzando Cypress bisognerà manualmente avviare il server col profilo di test attivo tramite:

* java -jar -Dspring.profiles.active=test target/book_advisor-0.0.1-SNAPSHOT.jar

* mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=test

In alternativa al posto di ng e2e possiamo eseguire i seguenti comandi:

* cypress:open per far partire cypress runner

* ng serve per far partire un server di sviluppo angular

Notiamo che in ambo i casi il server Spring va comunque avviato manualmente.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).
