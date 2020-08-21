# adverity-homework

### Used technology stuck:
* Programming language: Kotlin
* Web request handling: [Ktor](https://ktor.io)
* Database: H2 for test/local and Postgrtes(hosted on open shift) for deployed application
* Database access: [Kotlin exposed](https://github.com/JetBrains/Exposed) + 
some self developed additions [kotlin entities](https://github.com/guky-dhl/exposed-entities) 
* Json: [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)

### Running locally
```Execute main method from Application.kt  it will start listening on localhost with port 8383```

### Accessing globally 
Status url:``` http://homework-adverity-homework.apps.ca-central-1.starter.openshift-online.com/ping```

Post request: ``` http://homework-adverity-homework.apps.ca-central-1.starter.openshift-online.com/marketing-data ```

### Delivery process (CI/CD)
* Processes triggered on each push to github 
* Build by github actions: [ci.yml](https://github.com/guky-dhl/adverity-homework/blob/master/.github/workflows/ci.yml)
    * Build jar by gradle and ShodowJar plugin
    * Build docker image from [Dockerfile](https://github.com/guky-dhl/adverity-homework/blob/master/Dockerfile)
    and push image to docker hub
    * Import docker image to open shift with an automatic rollout to nodes on new images(atm single node)
    
## Api description
Api is based on single endpoint ```POST /marketing-data ```

Request:
```Kotlin 
data class MarketingDataRequest(
    val dimensions: Set<Field<*>>,
    val filters: Set<Filter<*>> = setOf(),
    val groupBy: Set<Field<*>> = setOf()
)
```

Response:
```Kotlin 
data class MarketingDataResponse(val result: List<Set<SimpleField<*>>>)
```
Where simple field is structure of field values
```Kotlin 
class SimpleField<T> : Field<T>() {        
        abstract val value: T?
}
```

## Example request from task definition:
### Total Clicks for a given Datasource for a given Date range:
Request
```json
{
    "dimensions": [
        {
            "type": "AggregateField",
            "field": {
                "type": "DecimalField",
                "columnName": "clicks"
            }
        }
    ],
    "filters": [
        {
            "type": "StringFilter",
            "operator": "EQ",
            "first": {
                "columnName": "dataSource"
            },
            "second": {
                "value": "Twitter Ads"
            }
        },
        {
            "type": "DateFilter",
            "operator": "BETWEEN",
            "first": {
                "columnName": "at"
            },
            "second": {
                "value": "2019-11-12"
            },
            "max": {
                "value": "2019-11-14"
            }
        }
    ]
}
```
Response:
```json
{
    "result": [
        [
            {
                "type": "DecimalField",
                "value": "25784.0000"
            }
        ]
    ]
}
```
---
### Click-through Rate (CTR) per Datasource and Campaign:
Request
```json
{
    "dimensions": [
        {
            "type": "CalculatedField",
            "calculationType": "TIMES",
            "first": {
                "type": "CalculatedField",
                "calculationType": "DIVIDE",
                "first": {
                    "type": "AggregateField",
                    "field": {
                        "type": "DecimalField",
                        "columnName": "clicks"
                    }
                },
                "second": {
                    "type": "AggregateField",
                    "field": {
                        "type": "DecimalField",
                        "columnName": "impressions"
                    }
                }
            },
            "second": {
                "type": "DecimalField",
                "value": "100"
            }
        }
    ],
    "filters": [
        {
            "type": "StringFilter",
            "operator": "EQ",
            "first": {
                "columnName": "dataSource"
            },
            "second": {
                "value": "Twitter Ads"
            }
        },
        {
            "type": "StringFilter",
            "operator": "EQ",
            "first": {
                "columnName": "campaignName"
            },
            "second": {
                "value": "Adventmarkt Touristik"
            }
        }
    ]
}
```
Response:
```json
{
    "result": [
        [
            {
                "type": "DecimalField",
                "value": "1.2554"
            }
        ]
    ]
}
```
---

### Impressions over time (daily):
Request
```json
{
    "dimensions": [
        {
            "type": "AggregateField",
            "field": {
                "type": "DecimalField",
                "columnName": "impressions"
            }
        },
        {
            "type": "DateField",
            "columnName": "at"
        }
    ],
    "groupBy": [
        {
            "type": "DateField",
            "columnName": "at"
        }
    ]
}
```
Response(Result contains 410 rows. Manually cut down for example for full example please refer to [MarketingStatisticControllerShould](https://github.com/guky-dhl/adverity-homework/blob/master/src/test/kotlin/homework/api/MarketingStatisticControllerShould.kt)):
```json
{
    "result": [
        [
            {
                "type": "DecimalField",
                "value": "81395.0000"
            },
            {
                "type": "DateField",
                "value": "2019-01-01"
            }
        ],
        [
            {
                "type": "DecimalField",
                "value": "112810.0000"
            },
            {
                "type": "DateField",
                "value": "2019-01-02"
            }
        ],
        [
            {
                "type": "DecimalField",
                "value": "100903.0000"
            },
            {
                "type": "DateField",
                "value": "2019-01-03"
            }
        ]
    ]
}
        
```
---
