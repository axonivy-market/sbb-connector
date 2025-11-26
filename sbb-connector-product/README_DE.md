# SBB Connector

Der SBB Connector von Axon Ivy integriert die von der SBB bereitgestellte [Swiss
Mobility API –
Journey](https://developer-int.sbb.ch/apis/smapi-osdm-journey/information).
Dieser Connector verwendet einen REST-Client, mit dem Sie Fahrpläne und
Tarifdetails abrufen können. Darüber hinaus können Sie über die API Buchungen
für den öffentlichen Nahverkehr in der Schweiz erstellen und verwalten.

Beachten Sie, dass für den Zugriff auf alle Funktionen der API Anmeldedaten
erforderlich sind. Diese werden zwar von der SBB kostenlos zur Verfügung
gestellt, aber der Zweck ihrer API besteht darin, damit Umsatz zu generieren.
Weitere Informationen zu Zugriff, Funktionen und Möglichkeiten finden Sie auf
der Infoseite der [Swiss Mobility API –
Journey](https://developer-int.sbb.ch/apis/smapi-osdm-journey/information).

## Demo

![Formular „Reisen suchen“](images/search-for-trips.png)

![Show Trips](images/trips.png)

## Setup

Um den SBB Connector zu verwenden, fügen Sie Ihrem Axon Ivy-Projekt die
folgenden Variablen hinzu:

```
@variables.yaml@
```

Jede Anfrage an die Journey SBB Swiss Mobility API erfordert einen `Requestor`
Header des aktuellen Geschäftsprozesses. Für die bereits vom SBB Connector
bereitgestellten Teilprozesse können Sie entweder das customField `requestor` zu
Beginn eines Prozesses setzen oder den `Requestor` jedes Mal, wenn Sie einen
Teilprozess aufrufen, als Argument angeben. Ein Beispiel finden Sie im
Demo-Projekt.

> [!Hinweis] Wenn Sie diesen Konnektor noch nicht verwendet haben, können Sie
> diesen Hinweis ignorieren. Ab dieser Version sind die aufrufbaren Prozesse
> `GetLocations` und `GetTrips` veraltet. Weitere Informationen finden Sie auf
> der Infoseite der [Swiss Mobility
> API](https://developer.sbb.ch/apis/b2p/information). Stattdessen haben wir
> zwei alternative aufrufbare Prozesse eingeführt: `GetPlaces` und
> `GetTripsCollection`. Da sich jedoch die Datenklasse geändert hat, müssen Sie
> diese anpassen, um diese aufrufbaren Prozesse verwenden zu können.
