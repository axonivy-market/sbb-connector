# SBB Connector

Axon Ivy's SBB Connector helps you accelerate process automation initiatives by integrating the [Swiss Mobility API](https://developer.sbb.ch/apis/b2p/information) provided by SBB. This connector offers a REST client that allows you to retrieve timetables and fare details. Additionally, you can create and manage bookings of Swiss public transport through the API.

Note that credentials are required to gain access to any features of the API. Although they are provided free of charge by SBB, the purpose of their API is to generate sales turnover through it. For more information about access, features and capabilities, visit the info page of the [Swiss Mobility API](https://developer.sbb.ch/apis/b2p/information).

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