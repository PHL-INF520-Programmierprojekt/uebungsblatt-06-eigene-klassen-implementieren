# Übungsblatt: Eigene Java-Klassen implementieren
[Link to English version](./README_en.md)

In diesem Übungsblatt lernen Sie, Ihre eigenen Java-Klassen (und Programme) von Grund auf zu schreiben.

## Übung: Soziale Medien Plattform (Social Media Platform)

Ihre Aufgabe ist es, eine soziale Medienplattform zu entwerfen (`Social Media Platform`), auf der Nutzer&ast;innen Profile erstellen, Nachrichten posten und anderen Nutzer&ast;innen folgen können.
Die Klassen sollen im Paket `de.phl.programmingprojects.socialmedia` implementiert werden.

### Aufgaben

1. Erstellen Sie eine `User`-Klasse mit den folgenden Eigenschaften:
    * `id` (int) - eine eindeutige Kennung für jed&ast;n Nutzer&ast;in
    * `username` (String) - der gewählte Benutzername
    * `followers` (Set von User) - ein Set von Nutzer&ast;innen, die diesem&ast;r Nutzer&ast;in folgen
    * `following` (Set von User) - ein Set von Nutzer&ast;innen, denen dieser&ast;m Nutzer&ast;in folgt
    * `posts` (Liste von Post) - eine Liste von Beiträgen

2. Erstellen Sie eine `Post`-Klasse mit den folgenden Eigenschaften:
    * `id` (int) - eine eindeutige Kennung für jeden Beitrag.
      * _Hinweis:_ Erstellen Sie die ID im Konstruktor mit einer zusätzlichen statischen Variable zum Zählen.
    * `text` (String) - der Text des Beitrags
    * `author` (User) - der/die Nutzer&ast;in, der/die den Beitrag verfasst hat
    * `timestamp` (Date) - der Zeitpunkt, zu dem der Beitrag erstellt wurde

3. Fügen Sie der `User`-Klasse eine Operation namens `createPost(final String text)` hinzu, die ein neues `Post`-Objekt erstellt und es zur Liste der Beiträge für den/die Nutzer&ast;in hinzufügt. Verwenden Sie das aktuelle Datum.

4. Fügen Sie der `User`-Klasse eine Operation namens `follow(final User user)` hinzu, die den/die angegebenen Nutzer*in zur Liste der Nutzer&ast;innen hinzufügt, denen diese&ast;r Nutzer&ast;in folgt. Denken Sie auch daran, den/die gefolgten Nutzer&ast;in zu aktualisieren, indem Sie diese&ast;n Nutzer&at;in zum Set der Follower hinzufügen.

5. Fügen Sie der `User`-Klasse eine Operation namens `getTimeline()` hinzu, die eine `Liste` aller Beiträge zurückgibt, die von Nutzer&ast;innen verfasst wurden, denen diese&at;r Nutzer&ast;in folgt.

6. Erstellen Sie eine `SocialMediaPlatform`-Klasse mit der folgenden Eigenschaft:
    * `users` (`Set` von User) - ein Set aller Nutzer&ast;innen auf der Plattform

7. Fügen Sie der `SocialMediaPlatform`-Klasse eine Operation namens `User createUser(final String username)` hinzu, die ein neues `User`-Objekt mit dem angegebenen Benutzernamen erstellt und es zum Set der Nutzer&ast;innen auf der Plattform hinzufügt. Der/die erstellte Nutzer&ast;in wird zurückgegeben.

8. Fügen Sie der `User`-Klasse eine Operation namens `getPostById(final int id)` hinzu, die das `Post`-Objekt mit der angegebenen ID zurückgibt, oder eine `NoSuchElementException` wirft, wenn kein solcher Beitrag existiert.

9. Fügen Sie der `SocialMediaPlatform`-Klasse eine Operation namens `getUserById(final int id)` hinzu, die das `User`-Objekt mit der angegebenen ID zurückgibt, oder eine `NoSuchElementException` wirft, wenn kein&ast;e solche&ast;r Nutzer&ast;in existiert.

10. Fügen Sie der `SocialMediaPlatform`-Klasse eine Operation namens `getMostFollowedUser()` hinzu, die den/die Nutzer&ast;in mit den meisten Followern zurückgibt.

11. Fügen Sie der `SocialMediaPlatform`-Klasse eine Operation namens `getMostActiveUser()` hinzu, die den/die Nutzer&ast;in mit den meisten Beiträgen zurückgibt.

12. Implementieren Sie die `main`-Operation in der `Main`-Klasse, die:
    * ein neues `SocialMediaPlatform`-Objekt erstellt
    * mindestens 5 `User`-Objekte erstellt und sie zur Plattform hinzufügt
    * lässt jede&ast;n Nutzer&ast;in mindestens 3 Beiträge erstellen
    * lässt jede&ast;n Nutzer&ast;in mindestens 2 anderen Nutzer&ast;innen folgen
    * ruft die `getTimeline`-Operation für jede&ast;n Nutzer&ast;in auf und gibt das Ergebnis aus, indem durch die resultierenden Beiträge iteriert und der/die Nutzer&ast;in und der Text jedes Beitrags ausgegeben wird.
    * ruft die `getMostFollowedUser`-Operation auf und gibt den Namen des/der Nutzer&ast;in aus
    * ruft die `getMostActiveUser`-Operation auf und gibt den Namen des/der Nutzer&at;in aus

## Übung: Autovermietungssystem (Car Rental System)

Ihre Aufgabe ist es, ein Autovermietungssystem für eine kleine Autovermietung zu implementieren. Das System sollte es Kund&ast;innen ermöglichen, Autos zu mieten und zurückzugeben, sowie zu verfolgen, welche Autos zur Vermietung verfügbar sind.

### Aufgaben

1. Implementieren Sie eine `Car`-Klasse mit den folgenden Eigenschaften:

    - `make` (String): die Marke des Autos (z.B. "Toyota", "Honda", "Ford")
    - `model` (String): das Modell des Autos (z.B. "Camry", "Accord", "Focus")
    - `year` (int): das Jahr, in dem das Auto hergestellt wurde
    - `rented` (boolean): ob das Auto derzeit vermietet ist oder nicht (initial auf `false` gesetzt)

2. Implementieren Sie eine `Customer`-Klasse mit den folgenden Eigenschaften:

    - `name` (String): der Name der/des Kund&ast;in
    - `rentedCar` (Optional\<Car\>): das Auto, das der/die Kund&ast;in derzeit gemietet hat (initial auf `Optional.empty()` gesetzt)

3. Implementieren Sie eine `CarRentalSystem`-Klasse mit den folgenden Operationen:

    - `addCar(final Car car)`: fügt ein neues Auto zum System hinzu
    - `rentCar(final Car car, final Customer customer)`: vermietet das angegebene Auto an den/die angegebene&ast;n Kund&ast;in (wenn das Auto verfügbar ist)
    - `returnCar(final Customer customer)`: gibt das von dem/der gegebenen Kund&ast;in gemietete Auto zurück (d.h., setzt das `rented`-Attribut des Autos auf `false` und setzt das `rentedCar`-Attribut des/der Kund&ast;in auf `Optional.empty()`)
    - `getAvailableCars()`: gibt eine Liste aller verfügbaren Autos zurück (d.h., Autos mit `rented` auf `false` gesetzt)
    - `getRentedCars()`: gibt eine Liste aller vermieteten Autos zurück (d.h., Autos mit `rented` auf `true` gesetzt)

4. Implementieren Sie eine `String toString()`-Operation in der `Car`-Klasse, die eine String-Darstellung eines Autos zurückgibt, d.h., alle Attribute in einem gemeinsamen String zusammenfügt und zurückgibt.

5. Implementieren Sie die `main`-Operation in der `Main`-Klasse:

    - Erstellen Sie ein neues `CarRentalSystem`-Objekt
    - Fügen Sie einige Autos zum System hinzu
    - Erstellen Sie einige Kund&ast;innen und lassen Sie sie Autos mieten und zurückgeben mit den `CarRentalSystem`-Operationen
    - Geben Sie die verfügbaren und vermieteten Autos nach jeder Vermietung und Rückgabe aus, um sicherzustellen, dass das System korrekt funktioniert.