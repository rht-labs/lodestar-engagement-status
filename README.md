# LodeStar Engagement Status

This project manages engagement status data for LodeStar.

The API is document via swagger and is available at `/q/swagger-ui`

----

## Configuration

The following environment variables are available:

### Logging
| Name | Default | Description|
|------|---------|------------|
| ENGAGEMENT_API_URL | http://git-api:8080 | The url to get engagement data |
| GITLAB_API_URL | https://acmegit.com | The url to Gitlab |
| GITLAB_TOKEN | t | The Access Token for Gitlab |
| LODESTAR_LOGGING | DEBUG | Logging to the base source package |

## Deployment

See the deployment [readme](./deployment) for information on deploying to a OpenShift environment

## Running the application locally

### Local Dev

You can run your application in dev mode that enables live coding using:

```
export GITLAB_API_URL=https://gitlab.com/ 
export GITLAB_TOKEN=token
export ENGAGEMENT_API_URL=https://git-api.test.com 
mvn quarkus:dev
```

In dev mode the application uses [Testcontainers](https://www.testcontainers.org/) that automatically spins up a postgresql container so there is no need to configure a database. Docker is needed.


> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

### Testing

Tests also leverage [Testcontainers](https://www.testcontainers.org/) and will automatically spin up a posgresql container.

```
mvn test
```

for continuous testing use

```
mvn quarkus:test
```