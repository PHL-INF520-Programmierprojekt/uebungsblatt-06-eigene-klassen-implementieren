# Java-Tests ausführen

Dieses Projekt verwendet Java 21, JUnit 5 und Mockito 5. Öffnen Sie den Projektordner in VS Code. Installieren Sie die empfohlene Erweiterung **Extension Pack for Java** von Microsoft. Im Dev Container ist sie bereits vorgesehen. Nach Änderungen an der Containerkonfiguration den Container neu erstellen.

1. Warten Sie, bis VS Code das Gradle-Projekt importiert hat.
2. Öffnen Sie die Ansicht **Testing** (Reagenzglas) und starten Sie die Tests dort oder über die Test-Schaltflächen im Java-Editor. Die Konfiguration **INF520** wird automatisch verwendet.
3. Vor dem Teststart stellt die Aufgabe **INF520: Tests vorbereiten** den Mockito-Agenten bereit. Bei einem Fehler dieser Aufgabe zuerst deren Terminalausgabe prüfen.
4. Zum Debuggen verwenden Sie **Debug Test** in derselben Ansicht.

Alternativ im Terminal unter Linux/macOS `./gradlew test`, unter Windows `.\gradlew.bat test` ausführen. Den Bericht finden Sie danach unter `build/reports/tests/test/index.html`.

Der Mockito-Agent wird automatisch heruntergeladen und unter `build/test-agent/` bereitgestellt; er wird nicht ins Repository eingecheckt. Keine absoluten Pfade zu Ihrem Benutzerverzeichnis eintragen. Die Oracle-Java-Erweiterung ist für dieses Projekt nicht erforderlich; mehrere gleichzeitig aktive Java-Testanbieter können unterschiedliche Startbefehle anzeigen.

Bei unbearbeiteten Aufgaben dürfen Tests fehlschlagen. Eine fachliche Fehlermeldung unterscheidet sich von einer defekten Testumgebung. Schauen Sie daher auf die konkrete Meldung, nicht nur auf die rote Markierung. Die Testdateien sind nur zu ändern, wenn die Aufgabenstellung dies ausdrücklich verlangt.

Java-Quellen, Tests und erfasste Ausgaben verwenden ausdrücklich UTF-8. Deutsche Umlaute müssen nicht entfernt werden. Speichern Sie Quelldateien in VS Code mit der Kodierung UTF-8. Unter Java 21 werden Gradle-Versionen ab 8.5 unterstützt; diese Kursvorlage verwendet Gradle 8.7. Öffnen Sie einzelne Übungsprojekte als eigenen Workspace, damit Testpfade zum Projektordner passen.

Außerhalb des Dev Containers müssen sowohl der Java-Projektimport als auch das Terminal ein JDK 21 verwenden. Setzen Sie dazu `JAVA_HOME` auf Ihr installiertes JDK 21 und nehmen Sie dessen `bin`-Ordner in `PATH` auf. Starten Sie VS Code danach neu. Prüfen Sie im Terminal `java -version` und `./gradlew --version` (Windows: `.\gradlew.bat --version`). Eine Editor-Einstellung wie `java.import.gradle.java.home` allein ändert das Terminal-Java nicht.

Nach einem Update der Gradle-Abhängigkeiten in VS Code **Java: Reload Projects** ausführen. Engine, API und Launcher sind über die JUnit-BOM 5.14.4 aufeinander abgestimmt. Die Vorlage wurde mit Test Runner for Java 0.46.0 im normalen und im Debug-Testlauf geprüft.

Die Vorbereitungsaufgabe lädt ausschließlich den Mockito-Agenten; sie kompiliert keine Aufgaben oder Tests. Compilerfehler können weiterhin den Java-Sprachserver bzw. betroffene Tests behindern. Unter Windows startet die Aufgabe ausdrücklich über `cmd.exe` und funktioniert dadurch auch bei Git Bash als Standardterminal.

**Projektpfad unter Windows:** Verwenden Sie möglichst einen Pfad ohne Umlaute oder andere Sonderzeichen, beispielsweise `C:\Java\INF520`. In Pfaden mit Umlauten wurden Fehler beim Start der Gradle-Test-JVM beobachtet. Leerzeichen sind zulässig. Diese Einschränkung betrifft den Ordnerpfad, nicht Umlaute in Java-Quelltexten oder Ausgaben.

**Compilerfehler in anderen Aufgaben:** Die Java-Erweiterung kann unabhängig von der Vorbereitungsaufgabe „Build failed, do you want to continue?“ anzeigen. In der geprüften VS-Code-Konfiguration konnten nicht betroffene Tests nach **Continue** ausgeführt werden. Nutzen Sie dies nur für bereits kompilierbare Teilaufgaben; beheben Sie vor einem vollständigen Gradle-Lauf alle Compilerfehler. Ein Fortsetzen repariert keine fehlerhaften Klassen.
