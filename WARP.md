# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Architecture

This is a modular, domain-driven Spring Boot application built with Maven. The project is divided into four main
modules:

* `domain-core`: Contains the core domain logic and business objects.
* `application-core`: Implements the application logic and use cases.
* `infrastructure-core`: Provides the technical infrastructure, such as database access and messaging.
* `presentation-core`: Exposes the application's functionality through a REST API.

## Commands

### Build

To build the entire project, run the following command from the root directory:

```bash
./mvnw clean install
```

### Testing

To run the unit and integration tests, use the following command:

```bash
./mvnw test
```

To run a single test, you can use the `-Dtest` parameter:

```bash
./mvnw test -Dtest=MyTestClass#myTestMethod
```
