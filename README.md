# data-collector

This test service recieves and provide rarely flushed meter's reports within akka serverless statefull platform.


## Deploying

To deploy service, install the `akkasls` CLI as documented in
[Setting up a local development environment](https://developer.lightbend.com/docs/akka-serverless/setting-up/)
and configure a Docker Registry to upload your docker image to.

You will need to set your `docker.username` as a system property:

```
sbt -Ddocker.username=antonnakonechnyi docker:publish
```

Refer to [Configuring registries](https://developer.lightbend.com/docs/akka-serverless/projects/container-registries.html)
for more information on how to make your docker image available to Akka Serverless.

Finally you can or use the [Akka Serverless Console](https://console.akkaserverless.com)
to create a project and then deploy your service into the project
through the `akkasls` CLI or via the web interface.
