Storing small musical ideas and playing around with modularization using Guice.

```
Package overview:
PACKAGE             DESCRIPTION                                 DEPENDENCIES
client              Contains the input and viewing logic        [midi, music, protocol]
midi                Contains midi interaction logic             [resource]
music               Contains the musical Business Logic         []
music.interpret     Contains interpretation algorithms          [music.symbolic]
music.math          Contains basic music math operations        []
music.symbolic      Contains the music data structures          []
music.write         Contains lilypond file text format writing  [music.symbolic]
protocol            Contains an abstract client-server protocol [pubsub]
pubsub              Contains publish-subscribe logic            []
resource            Contains resource management wrappers       []
server              Contains the application state              [pubsub, music, protocol]

WIP
transport           Contains transport layer logic              [resource]
```