# RSO: Image metadata microservice

## Prerequisites

Postgres
```bash
docker run -d --name pg-image-metadata -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=image-metadata -p 5432:5432 postgres:12
```
Consul
```bash
docker run -d --name consul -p 8500:8500 -p 8600:8600 consul
```
