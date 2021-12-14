# GLAD rest service
This project provides a web-service for indexing and searching LCA meta data, as defined in the [GLAD project](https://www.lifecycleinitiative.org/resources-2/global-lca-data-network-glad/).

A detailed documentation of the REST service is found [here](https://rawgit.com/GreenDelta/glad-rest-service/master/index.html).

## Build from source

#### Dependent modules
In order to build the GLAD rest service application, you will need to install the search-wrapper API and the opensearch implementation of the API, search-wrapper-es.
These are plain Maven projects and can be installed via `mvn install`. See the
[search-wrapper](https://github.com/GreenDelta/search-wrapper) and 
[search-wrapper-os](https://github.com/GreenDelta/search-wrapper-os) and
[search-wrapper-os-rest](https://github.com/GreenDelta/search-wrapper-os-rest) repositories for more
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
Now you can build the glad-rest-service application with `mvn package`, which will create a war-file glad-rest-service-1.0.0.war

#### Configuration
The build war file contains a file /WEB-INF/classes/com/greendelta/search/glad/rest/app.properties.

You will need to specify the correct opensearch configuration in the `search.*` fields, before deploying the application. Also you should replace the api.key value with a randomly generated UUID.

```
api.key: This is used to verify authentication to perform indexing requests on the GLAD rest service
search.cluster: The opensearch cluster (default: opensearch)
search.host: The opensearch host (if installed on the same server: localhost)
search.index: The index used for the application instance (default: glad)
```
The search index will be created on application start, if not already existing.

## Server configuration
In order to install the application a Java Runtime Environment >= 8 and a servlet container (e.g. tomcat 8) needs to be installed.

As server hardware, we recommend to configure opensearch with at least 2GB heap space, the same goes for the servlet container. This is not the minimum requirements, but rather a recommendation for use in production for a moderate amount of simultaneous requests.

#### opensearch
Before deploying the application to the servlet container you will need to set up opensearch. For more information on the opensearch installation, please take a look at the [official documentation](https://opensearch.org/docs/latest/opensearch/install/index/).

##### Deployment
Now you can deploy the application (war-file) on the servlet container.

## Scoring GLAD results
The GLAD search results can be scored (weighted) for relevancy. When scoring is applied, data set scores will be dynamically weighted based on a classification of the document values. The data sets will be classified into five different classes. Each class has a specific weight, with which the default data set score will be multiplied. The higher the resulting score, the more relevant a document is. Six different types of scoring are available, which can be combined as well. 

For all six types below, the documents will be assigned to one of five classes. For all types, the weights for the 5 classes are: 1.0, 0.8, 0.6, 0.4, 0.2

#### Amount deviation
This score uses the value of amountDeviation (x) for an absolute classification

| Class 1 | Class 2 | Class 3 | Class 4 | Class 5
| --- | --- | --- | --- | --- |
| x < 2	| x < 5	| x < 10 | x < 25 | x >= 25 or unknown

#### Completeness
This score uses the value of completeness (x) for an absolute classification

| Class 1 | Class 2 | Class 3 | Class 4 | Class 5
| --- | --- | --- | --- | --- |
| x > 98 | x > 90 | x > 75 | x > 50 | x <= 50 or unknown

#### Representativeness
This score uses the value of representativenessValue (x) for an absolute classification

| Class 1 | Class 2 | Class 3 | Class 4 | Class 5
| --- | --- | --- | --- | --- |
| x <= 5 | x <= 25 | x <= 50 | x <= 100 | x > 100 or unknown

#### Time
This score uses the values of validFrom (x1) and validUntil (x2) and compares them to a user specified value (y). It calculates the time difference of x1,x2 and y (d). 

1)	If y >= x1 and y <= x2 then d = 0
2)	Else d = min(abs(x1-y), abs(x2-y))

| Class 1 | Class 2 | Class 3 | Class 4 | Class 5
| --- | --- | --- | --- | --- |
| d = 0 | d < 3 | d < 6 | d < 10 | d >= 10 or unknown
 
#### Geography
This score uses the values of latitude (x1) and longitude (x2) and compares them to a user specified point value (y1 and y2). It calculates a distance between both points (d1), as well as the latitude difference (d2 = abs(x1, y1))

| Class 1 | Class 2 | Class 3 | Class 4 | Class 5
| --- | --- | --- | --- | --- |
| d1 <= 100 | d1 <= 500 | d1 > 500 and d2 <= 10 | d1 > 500 and d2 <= 15 | (d1 > 500 and d2 > 15) or unknown

#### Technology
This score uses the values of unspscCode (x1) and co2peCode (x2) and compares them to the user specified values for both (y1 and y2). The UNSPSC code consists of 4 groups of 2 digits (totaling 8 digits). When compared, differences are recognized on each group. E.g. 44125521 compared to 44125522 would have a difference value of 1. Since the second group (counting from right) is different. 44125521 compared to 44665521 would have a difference value of 3. The CO2PE code consists of 3 groups, separated by dots. Here the same applies, e.g. 1.1.1 compared to 1.1.2 would have a difference value of 1 and 2.1.1 compared to 1.1.1 would have a difference value of 3.

d1 = difference in UNSPSC code, d2 = difference in CO2PE code

| Class 1 | Class 2 | Class 3 | Class 4 | Class 5
| --- | --- | --- | --- | --- |
| d1 = 0 and d2 = 0 | (d1 = 1 and d2 = 0) or (d1 = 0 and d2 = 1) | (d1 <= 2 and d2 <= 1) or (d1 <= 1 and d2 <= 2) | (d1 = 2 and d2 = 2) | d1 > 2 or d2 > 2 or unknown

