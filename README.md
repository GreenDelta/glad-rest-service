# GLAD rest service
This project provides a web-service for indexing and searching LCA meta data, as defined in the [GLAD project](https://www.lifecycleinitiative.org/resources-2/global-lca-data-network-glad/).

## Build from source

#### Dependent modules
In order to build the GLAD rest service application, you will need to install the search-wrapper API and the elasticsearch implementation of the API, search-wrapper-es.
These are plain Maven projects and can be installed via `mvn install`. See the
[search-wrapper](https://github.com/GreenDelta/search-wrapper) and 
[search-wrapper-es](https://github.com/GreenDelta/search-wrapper-es) repositories for more
information.

#### Get the source code of the application
We recommend that to use Git to manage the source code but you can also download
the source code as a [zip file](https://github.com/GreenDelta/glad-rest-service/archive/master.zip).
Create a development directory (the path should not contain whitespaces):

```bash
mkdir glad
cd glad
```

and get the source code:

```bash
git clone https://github.com/GreenDelta/glad-rest-service.git
```

#### Build
Now you can build the glad-rest-service application with `mvn install`, which will create a war-file glad-rest-service-1.0.0.war

#### Configuration
The build war file contains a file /WEB-INF/classes/com/greendelta/search/glad/rest/app.properties.

You will need to specify the correct elasticsearch configuration in the `search.*` fields, before deploying the application. Also you should replace the api.key value with a randomly generated UUID.

```
api.key: This is used to verify authentication to perform indexing requests on the GLAD rest service
search.cluster: The elasticsearch cluster (default: elasticsearch)
search.host: The elasticsearch host (if installed on the same server: localhost)
search.index: The index used for the application instance (default: glad)
```
The search index will be created on application start, if not already existing.

## Server configuration
In order to install the application a Java Runtime Environment >= 8 and a servlet container (e.g. tomcat 8) needs to be installed.

As server hardware, we recommend to configure elasticsearch with at least 2GB heap space, the same goes for the servlet container. This is not the minimum requirements, but rather a recommendation for use in production for a moderate amount of simultaneous requests.

#### Elasticsearch
Before deploying the application to the servlet container you will need to set up elasticsearch. For more information on the elasticsearch installation, please take a look at the [official documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/setup.html).

##### Deployment
Now you can deploy the application (war-file) on the servlet container.
