# SBB Anschluss

#Axon Efeuhat SBB Anschluss integriert den [schweizerische Mobilität API -
#Journey](https://developer-int.sbb.ch/apis/smapi-osdm-journey/information)
versehen mal SBB. Dieser Anschluss benutzt einen REST Kunden jener erlaubt du zu
#wiedergewinnen Fahrpläne und ergehen Details. Außerdem, du kannst schaffen und
fertigbringen bookings von schweizerisch Öffentlichkeit Transport durch den API.

Beachten dass #Berechtigungsnachweis sind bedürft zu gewinnen zugreifen zu
irgendwelchen Charakterzügen von den API. Wobei sind versehen sie gratis mal
SBB, der Zweck von ihr API ist zu generieren Verkäufe Umsatz durch ihm. Für
#mehr Auskunft von zugreifen, Charakterzüge und Fähigkeiten, besuchen die info
Seite von die [schweizerische Mobilität API -
#Journey](https://developer-int.sbb.ch/apis/smapi-osdm-journey/information).

## Demo

![#Suchen Reisen Formen](images/search-for-trips.png)

![Zeigen Reisen](images/trips.png)

## Einrichtung

Zu benutzen das SBB Anschluss, füg zu die folgenden Variablen zu euren #Axon
Efeu Projiziert:

```
@variables.yaml@
```

Irgendwelche Bitte zu der Reise SBB schweizerische Mobilität API bedürft eine
`Requestor` Kopfball von dem gängigen Geschäft Arbeitsgang. Für das schon
versehen subprocesses mal das SBB Anschluss kannst du #jeder setzen den
customField `requestor` #anfangs einen Arbeitsgang oder versehen den `Requestor`
da einen Streit jede Zeit rufst du eine subprocess. Nimm ein ansehen das Demo
Projekt für ein Beispiel.

> [!Beachten] Ob dir hast nicht diesen Anschluss benutzt noch, du kannst diese
> Note überhören. Von dieser Version, `GetLocations` und `GetTrips` callable
> Arbeitsgänge sind #schlechtmachen. Du kannst besuchen das info Seite von die
> [schweizerische Mobilität API](https://developer.sbb.ch/apis/b2p/information)
> zu bekommen #mehr Auskunft. Stattdessen, Wir hereingebracht haben zwei
> Alternative `GetPlaces` und `GetTripsCollection` callable Arbeitsgänge. Indes,
> #der #Daten #eingruppieren ist gewechselt, du brauchst zu adaptieren ihm zu
> benutzen diese callable Arbeitsgänge.
