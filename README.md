Artamus is a music analysis client/server application

##### Packages
The application is separated into several packages to keep concerns separated
- **Client** Contains the client application with the input and viewing logic
- **Server** Contains the application state and does the heavy lifting
- **Common** Contains common packages
- **Storage** Contains a lightweight memory/file storage (which might be split off entirely into a separate package eventually)

```
Dependency overview:
PACKAGE      DEPENDENCIES
client       [common.domain, common.midi, common.patching, common.pubsub, network]
server       [common.domain,                               common.pubsub, network, storage]
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