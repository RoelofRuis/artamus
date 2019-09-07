Storing small musical ideas and playing around with modularization using Guice.

```
Package overview:
PACKAGE         DESCRIPTION                                 DEPENDENCIES
client          Contains the input and viewing logic        [midi, music, protocol]
midi            Contains midi interaction logic             []
music           Contains the musical Business Logic         []
protocol        Contains an abstract client-server protocol []
server          Contains the application state              [music, protocol]
```