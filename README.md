Artamus is a music analysis client/server application

```
Package overview:
PACKAGE             DESCRIPTION                                 DEPENDENCIES
client              Contains the input and viewing logic              [midi, music, patching, protocol, pubsub, server]
midi                Contains midi interaction logic                   []
music               Contains the music domain                         []
patching            Contains logic for patching data streams together []
protocol            Contains an abstract client-server protocol       []
pubsub              Contains publish-subscribe logic                  []
server              Contains the application state                    [music, protocol, pubsub, storage]
storage             Contains a lightweight memory/file storage        []
```

The server application requires `Lilypond` which can be downloaded at:
http://lilypond.org/download.html


#### Keyboard reference

```
                 ^                   midi
                 |  pc   step   ^2   oct4
.----------------|
|    .===========|  0    0      1    60
>----|XXXXXXXXXXX|  1           2    61
|    >===========|  2    1      4    62
>----|XXXXXXXXXXX|  3           8    63
|    `===========|  4    2      16   64
>----------------|
|    .===========|  5    3      32   65
>----|XXXXXXXXXXX|  6                66
|    >===========|  7    4           67
>----|XXXXXXXXXXX|  8                68
|    >===========|  9    5           69
>----|XXXXXXXXXXX|  10               70
|    `===========|  11   6           71
>----------------|
|                |  0    0           72
`----------------|
                 |
                 v
```