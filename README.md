Artamus music server

```
Package overview:
PACKAGE             DESCRIPTION                                 DEPENDENCIES
client              Contains the input and viewing logic        [midi, music, protocol]
midi                Contains midi interaction logic             [resource]
music               Contains the musical Business Logic         []
music.interpret     Contains interpretation algorithms          [music.symbolic]
music.math          Contains basic music math operations        []
music.symbolic      Contains the music data structures          []
protocol            Contains an abstract client-server protocol [resource]
pubsub              Contains publish-subscribe logic            []
resource            Contains resource management wrappers       []
server              Contains the application state              [pubsub, music, protocol]
transport           Contains transport layer logic              [resource]
```