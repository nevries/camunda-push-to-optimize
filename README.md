# camunda-push-to-optimize
Summer HackDays 2020 project: PoC pushing event via pulsar to optimize (instead of Rest import)

## Pulsar

Start from docker-compose.yml. Access:
- [`http://localhost:8088`](http://localhost:8088) service bus
- [`http://localhost:8089`](http://localhost:8089) dashboard

## App

Camunda application, Spring Boot based, with simulator generating data.

Pulsar connection via Java client, for testing activate profiles

  `consumer, producer`

to see it in action.