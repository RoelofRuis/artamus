Artamus music server

```
Package overview:
PACKAGE             DESCRIPTION                                 DEPENDENCIES
blackboard          Contains 'blackboard' control structures    []
client              Contains the input and viewing logic        [midi, music, protocol, pubsub, resource, server]
midi                Contains midi interaction logic             [resource]
music               Contains the musical Business Logic         []
protocol            Contains an abstract client-server protocol [pubsub, resource, transport]
pubsub              Contains publish-subscribe logic            []
resource            Contains resource management wrappers       []
server              Contains the application state              [blackboard, music, protocol, pubsub]
transport           Contains transport layer logic              [resource]
```