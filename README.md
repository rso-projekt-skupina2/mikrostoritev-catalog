# RSO: Image metadata microservice

## Prerequisites

Postgres
```bash
docker run -d --name pg-image-metadata -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=image-metadata -p 5432:5432 postgres:12
```
Etcd
```bash
docker run -d -p 2379:2379 --name etcd --volume=/tmp/etcd-data:/etcd-data quay.io/coreos/etcd:latest /usr/local/bin/etcd --name my-etcd-1  --data-dir /etcd-data  --listen-client-urls http:/
/0.0.0.0:2379  --advertise-client-urls http://0.0.0.0:2379 --listen-peer-urls http://0.0.0.0:2380  --initial-advertise-peer-urls http://0.0.0.0:2380 --initial-cluster my-etcd-1=http://0.0.0.0:2380 --initial-cl
uster-token my-etcd-token  --initial-cluster-state new  --auto-compaction-retention 1  -cors="*"
```
Delete all connections to database:
```sql
SELECT
    pg_terminate_backend(pg_stat_activity.pid)
FROM
    pg_stat_activity
WHERE
        pg_stat_activity.datname = 'deo5eqi17pku88'
  AND pid <> pg_backend_pid();
```
