# DMaap Message Router


### CONTENTS OF THIS FILE


 * Introduction
 * Installation

INTRODUCTION
------------

DMaaP Message Router is a reliable, high-volume pub/sub messaging service with a RESTful HTTP API that is delivered as a web service using AJSC framework. The service is built over Apache Kafka.

Service Goals
----------------

- Pub/sub messaging metaphor to broaden data processing opportunities.
- A single solution for most event distribution needs to simplify our solutions.  
- Horizontal scalability: Add servers to the cluster to add capacity. (Our initial installations are expected to handle at least 100,000 1KB msgs/sec per data center.)  
- Durability: Hardware failure in the cluster should not impact service, and messages should never be lost.  
- Durability: Consumers should not lose messages if they experience downtime.
- High throughput: Consumers must be able to distribute topic load across multiple systems.  
- Easy integration via RESTful HTTP API
- Supports DME2 (Direct Message Engine) for routing messages to nearest end point.  
- Supports AAF authentication and authorization.  

Message Order Guarantee
--------------------

Most messaging applications require some level of delivery order guarantee. To achieve strict ordering across messages in a topic (that is, each consumer sees each message on a topic in the order that the message was originally queued to the topic), each message must run through a single coordinating system. This limits scaling potential. The Apache Kafka system takes a more relaxed approach. Instead of guaranteeing strict ordering of messages in a topic, it introduces the idea of topic partitions. A partition is a group of related messages in the topic. Kafka's ordering guarantee is limited to a partition within a topic. This "partial-ordering guarantee" turns out to be adequate for nearly all of our use cases. (When it's not, the publisher can use a single partition to get the same effect as a having a fully ordered topic.)




INSTALLATION
------------

The quick way of running Message Router locally is downloading the docker image and running it. The Docker image contains Message router configured with Apache Kafka.

$docker pull attos/dmaap

$docker run attos/dmaap

If you want to build from source 
  1. Install and Configure Java 1.8
  2. Install and configure Apache Maven(3.x)
  1. Install and Configure Apache kafka 2.9.2-0.8.1.1
  2. Start zoo keeper followed by kafka
  3. Go to the <base directory>/"Msgrtr" folder and use below commands  
          $ mvn clean package install
  4. Go to the <base directory>/"dmaap" folder and use below commands  
            $ mvn clean compile package install  
            $ mvn -P runAjsc -XX:MaxPermSize=1024m  




