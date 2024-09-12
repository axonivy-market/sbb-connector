# SBB Konnektor

Axon Ivy's SBB Konnektor integriert die  [Swiss Mobility API](https://developer.sbb.ch/apis/b2p/information) der SBB (= Schweizerische Bundesbahnen). Dieser Konnektor nutzt einen REST-Client, der es dir ermöglicht, Fahrpläne und Tarifdetails abzurufen. Zusätzlich kannst du über die API Buchungen für den  öffentlichen Verkehr der Schweiz erstellen und verwalten.

Beachte, dass Zugangsdaten erforderlich sind, um auf die Funktionen der API zuzugreifen. Obwohl diese von der SBB kostenlos zur Verfügung gestellt werden, dient ihre API dazu, Umsätze zu generieren. Weitere Informationen zu Zugang, Funktionen und Möglichkeiten findest du auf der Infoseite der Swiss Mobility API.

## Demo

![Search for Trips Form](images/search-for-trips.png)

![Show Trips](images/trips.png)

## Setup

To use the SBB Connector, add the following variables to your Axon Ivy Project:

```
Variables:
  sbbConnector:
    # The URI of the API to use. For example: `https://b2p-int.api.sbb.ch` (integration) or `https://b2p.api.sbb.ch` (production)
    uri: ''
    # The contract id provided to you by SBB. For Example: `ACP1024`
    contractId: ''
    # The client id provided to you by SBB. For Example: `01234567-89ab-cdef-0123-456789abcdef`
    clientId: ''
    # The client secret provided to you by SBB.
    # [password]
    clientSecret: ''
    # The endpoint for acquiring the bearer token using your client id and secret. For Example: `https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/oauth2/v2.0`
    tokenEndpoint: ''
    # The scope provided to you by SBB.
    scope: ''
```

Any request to the API requires a x-conversation-id header of the current business process. For the already provided subprocesses by the SBB Connector you can either set the customField `conversationId` at the beginning of a process or provide the conversationId as an argument each time you call a subprocess. Take a look at the demo project for an example.
