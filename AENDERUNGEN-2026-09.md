# Änderungen vom 27. September 2026

Die Testumgebung verwendet Java 21, JUnit 5.14.4 und Mockito 5.20.0. Mockito wird über einen expliziten Agenten gestartet. Compiler, Testausgaben und Editor verwenden UTF-8; erfasste Zeilenenden werden vereinheitlicht. Das Microsoft Extension Pack for Java und die Konfiguration `INF520` bilden den gemeinsamen Testweg. Die nicht mehr verfügbare GitHub-Classroom-Erweiterung wurde entfernt. Einrichtung und Testaufrufe stehen in [TESTEN.md](TESTEN.md).

Der Vertrag von follow nennt Selbstfolgen, erneutes Folgen und null ausdrücklich. Die Tests prüfen die konkrete Ausnahmeursache. Aufgabe 12 akzeptiert mindestens fünf Nutzer und beobachtet initialisierte Objekte mit tatsächlichem Zustand. IDs werden nicht mehr als feste Werte vorausgesetzt.

## Prüfung und Einsatz

Die Java-21-Konfiguration wurde lokal einschließlich Testkompilierung geprüft. Der gemeinsame Testweg wurde in VS Code 1.132.0 mit Test Runner for Java 0.46.0 über Run Tests und Debug Tests geprüft. Fachliche Änderungen wurden gegen private Referenzimplementierungen und gezielt fehlerhafte Varianten geprüft. Musterlösungen wurden nicht in dieses Aufgabenrepository übernommen.

Noch unbearbeitete Aufgaben können fachlich rote Tests ergeben. Bei bestehenden Codespaces ist nach der Konfigurationsänderung ein Container-Rebuild erforderlich. Bereits ausgegebene Abgaben erhalten Änderungen nicht automatisch; vor einer Übernahme muss ihr Bearbeitungsstand berücksichtigt werden.
