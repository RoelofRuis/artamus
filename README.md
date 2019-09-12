Storing small musical ideas and playing around with modularization using Guice.

```
Package overview:
PACKAGE         DESCRIPTION                                 DEPENDENCIES
client          Contains the input and viewing logic        [midi, music, protocol]
midi            Contains midi interaction logic             [resource]
music           Contains the musical Business Logic         []
protocol        Contains an abstract client-server protocol []
protocol-v2     Contains new protocol with better resource  [resource]
                management from the ground up
resource        Contains resource management wrappers       []
server          Contains the application state              [music, protocol]
```