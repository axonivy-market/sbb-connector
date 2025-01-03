# SBB Connector

Axon Ivy's SBB Connector integrates the [Swiss Mobility API - Journey](https://developer-int.sbb.ch/apis/smapi-osdm-journey/information) provided by SBB. This connector uses a REST client that allows you to retrieve timetables and fare details. Additionally, you can create and manage bookings of Swiss public transport through the API.

Note that credentials are required to gain access to any features of the API. Although they are provided free of charge by SBB, the purpose of their API is to generate sales turnover through it. For more information about access, features and capabilities, visit the info page of the [Swiss Mobility API - Journey](https://developer-int.sbb.ch/apis/smapi-osdm-journey/information).

## Demo

![Search for Trips Form](images/search-for-trips.png)

![Show Trips](images/trips.png)

## Setup

To use the SBB Connector, add the following variables to your Axon Ivy Project:

```
@variables.yaml@
```

Any request to the Journey SBB Swiss Mobility API requires a `Requestor` header of the current business process. For the already provided subprocesses by the SBB Connector you can either set the customField `requestor` at the beginning of a process or provide the `Requestor` as an argument each time you call a subprocess. Take a look at the demo project for an example.

> [!Note]
> If you have not used this connector yet, you can ignore this note.
> From this version, `GetLocations` and `GetTrips` callable processes are deprecated.
> You can visit the info page of the [Swiss Mobility API](https://developer.sbb.ch/apis/b2p/information) to get more information.
> Instead, We have introduced two alternative `GetPlaces` and `GetTripsCollection` callable processes.
> However, the data class are changed, you need to adapt it to use these callable processes.