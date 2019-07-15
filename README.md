# qudiniApp-booking-widget-data-generator

## Project motivation:

This project is heavily based on the code already produced to populate the qudini app, the main changes are refactor of 
possible deprecated methods, addiction on basic logging instead of STDOUT and possible runner customizations.   

The primary intend of this project is decoupling this code from the test project and this way to act has a standalone
with the possibility of allowing further config customization and extension. 

Being a standalone allows the most generic QudiniApp configurations to be run only once and not by test suite, which 
increases the test batteries run time.  

It can also be used by any development team to populate at least the basic data on qudini app.

## Tasks

first _release candidate_

-  Be able to create merchant(s), venue(s), queue(s) and product(s)

- [x] Based on static data (standalone)
- [x] Based on direct method calls (library)

- Be able to delete each artifact created
- [ ] Based on static (standalone)
- [ ] Based on direct method calls (library)

--##--
- [x] Be able to link products and queues with basic information 
- [x] Be able to create a simple (almost static information) booking widget
- [ ] Be able to perform a dirty (quick) delete - archive merchant without deleting remaining artifacts
- [x] Add csv expected content (header field name and observations)
- [ ] Add simple example for some library methods

## Prerequisites:
- Java 8
- Maven 3.5+

## _Schema_ of the CSV files 
At the moment there are the following csv files in the project:
- merchants.csv
- venues.csv
- queues.csv
- products.csv
- bookingwidgets.csv

##### Merchants CSV file:
merchant csv file header fields and observations


| Field name                | Observations                                                          |
| ---------------------     |   -----------------------------------------------------------------   |
| **name**                  | The merchant name                                                     |
| **maxVenues**             | Max venues allowed to the merchant                                    |
| **templateKey**           |                                                                       |
| **contractStatusKey**     | Type of contract                                                      |
| **industrySectorKey**     | Industry sector                                                       |
| **sizeCategoryKey**       |                                                                       |
| **salesRegionKey**        |                                                                       |
| **countryKey**            |                                                                       |
| **timezoneKey**           |                                                                       |
| **languageKey**           |                                                                       |
| **billingTypeKey**        |                                                                       |
| **salesAssigneeKey**      |                                                                       |
| **reportWalkoutThreshold**|

##### Venues CSV file:
venues csv file header fields and observations


|Field name                 |Observations                                                           |
| ---------------------     | ---------------------------------------------------------------       |
| **merchantName**          |                                                                       |
| **venueName**             |                                                                       |

##### Queues CSV file:
queues csv file header fields and observations


|Field name                 |Observations                                                           |
|---------------------------|-----------------------------------------------------------------------|
|**merchantName**           |                                                                       |
|**venueName**              |                                                                       |
|**name**                   | The queue name                                                        |
|**averageServeTime**       |                                                                       |

##### Products CSV file:
products csv file header fields and observations


|Field name                 |Observations                                                           |
|---------------------------|-----------------------------------------------------------------------|
|**merchantName**           |                                                                       |
|**productName**            |                                                                       |
|**averageServeTimeMinutes**|                                                                       |
|**queues**                 | (a)                                                                   |
|**bookingFor**             | (b)                                                                   | 

(a) List of queues that will be linked to the product. The entries in this field must be separated by a **SPACE** (e.g. queueName1 <space> queueName2). Although in direct API calls it seems the rule of a venue not being able to have the same product in two or more of its queues is enforced, when using the data generator and changing th contents of this file have this into account. 

(b)  List that may contain 1 or 2 values: "Booking" and/or "Queues". Separated as mentionad in (a)

##### BookingWidgets CSV file:
bookingwidgets csv file header fields and observations


|Field name                 |Observations                                                           |
|---------------------------|-----------------------------------------------------------------------|
|merchantName               |                                                                       |
|title                      | The title of the booking widget                                       |
|venueNames                 | (c )                                                                  |
|productsNames              | (d)                                                                   |

(c ) List of venues that will be linked to the booking widget. The entries in this field must be separated by a **SPACE** (e.g. venueName1 <space> venueName2)

(d) List of products that will be linked to the booking widget. The entries in this field must be separated by a **SPACE** (e.g. productName1 <space> productName2)

## Commands:
- To run the standalone version using maven:
```sh
$ mvn clean install exec:java -Dcom.qudini.ApplicationEnv=<env>
```