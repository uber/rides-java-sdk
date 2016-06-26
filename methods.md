# Methods provided by ``com.uber.sdk.rides.client.services.RidesService``

| HTTP Method 	| Endpoint                          	| Auth Method            	| Required Scope                                   	| Method                            	|
|-------------	|-----------------------------------	|------------------------	|-------------------------------------------------	|-----------------------------------	|
| GET         	| /v1/products                      	| OAuth or server_token 	|                                                 	| getProducts        	    |
| GET         	| /v1/products/{product_id}         	| OAuth or server_token 	|                                                 	| getProduct             |
| PUT         	| /v1/sandbox/products/{product_id}         	| OAuth or server_token   | (Sandbox mode)                                   	| updateSandboxProduct             |
| GET         	| /v1/estimates/price               	| OAuth or server_token 	|                                                 	| getPriceEstimates        	    |
| GET         	| /v1/estimates/time                	| OAuth or server_token 	|                                                 	| getPickupTimeEstimate       	    |
| GET         	| /v1.2/history                     	| OAuth                  	| history or history_lite                          	| getUserActivity             |
| GET         	| /v1/me                            	| OAuth                  	| profile                                         	| getUserProfile             |
| POST        	| /v1/requests                      	| OAuth                  	| request (privileged)                            	| requestRide             |
| GET         	| /v1/requests/current              	| OAuth                  	| request (privileged) or all_trips (privileged)  	| getCurrentRide               	|
| DELETE      	| /v1/requests/current              	| OAuth                  	| request (privileged)                            	| cancelCurrentRide            	|
| POST        	| /v1/requests/estimate             	| OAuth                  	| request (privileged)                            	| estimateRide             |
| GET         	| /v1/requests/{request_id}         	| OAuth                  	| request (privileged)                            	| getRideDetails             |
| PATCH       	| /v1/requests/{request_id}         	| OAuth                  	| request (privileged)                            	| updateRide             |
| PUT         	| /v1/sandbox/requests/{request_id}         	| OAuth                  	| request (privileged & Sandbox mode )            	| updateSandboxRide             |
| DELETE      	| /v1/requests/{request_id}         	| OAuth                  	| request (privileged)                            	| cancelRide             |
| DELETE      	| /v1/requests/current               	| OAuth                  	| request (privileged)                            	| cancelCurrentRide             |
| GET         	| /v1/requests/{request_id}/map     	| OAuth                  	| request (privileged)                            	| getRideMap             |
| GET         	| /v1/places/{place_id}             	| OAuth                  	| places                                          	| getPlace           	|
| PUT         	| /v1/places/{place_id}             	| OAuth                  	| places                                          	| setPlace           	|
| GET         	| v1/payment-methods                	| OAuth                  	| request (privileged)                            	| getPaymentMethods           	|
