<h1><img src="documentation/image/img_1.png" alt="Modulythe logo" width="75" style="vertical-align: middle"> modulythe-core</h1>   

*(`mod-u-lith`)*

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/DevDeNiro/modulythe-core)
[![License](https://img.shields.io/badge/license-apache2-blue)](https://github.com/DevDeNiro/modulythe-core/blob/main/LICENSE)
[![Version](https://img.shields.io/badge/version-0.0.1--SNAPSHOT-informational)](https://github.com/DevDeNiro/modulythe-core)

> A collection of code abstractions for building modular, domain-driven applications on the JVM.

### 🚧 WORK IN PROGRESS 🚧

This project provides a solid foundation developed from experience across various Spring Boot projects.
It aims to provide a solid foundation for building modular, domain-driven applications on the JVM.

## Key Features

- Provides multiple abstractions for the domain layer, inspired by Domain-Driven Design
  (DDD) [ddd.img](documentation/schemas/domain-model.puml) principles.
- Offers utility objects for common tasks like filtering and pagination.
- Includes default configurations for testing environments.

## Inspiration

This project is inspired by the work of Chris Richardson and incorporates some abstraction ideas
from [eventuate-tram-core](https://github.com/eventuate-tram/eventuate-tram-core).

## Technology Stack

Modulythe is built with Spring Boot and is designed to be reusable in any JVM-based project (including Kotlin, Ktor),
though it is primarily tailored for Spring Boot environments.

The main technologies and libraries used are:

- Java 21 (minimum)
- Spring Boot
- JPA
- Kafka
- Resilience4j
- Cucumber

## Getting Started

The easiest way to run this service during development is to use Docker Compose.
*(Further instructions to be added).*

## Contributing

Contributions are welcome!