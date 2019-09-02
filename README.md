Storing small musical ideas and playing around with modularization using Guice.

Project structure overview
```
Without dependencies:
util        - Contains utilities for general use
protocol    - Contains an abstract client-server protocol

Dependent on 'util':
music       - Contains the musical 'Business logic'

Dependent on 'music', 'protocol' and 'util':
client      - Contains the input and viewing logic
server      - Contains the application state
```