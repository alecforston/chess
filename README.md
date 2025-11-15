# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.
## Server Design

[Design Link](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDp6gAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqeogVXot3sgY87nae1t+7GeoKDgcTXS7QD71D+et0cwcfB9EKHx6jHAM-xWfNvcjwXLgNrgNXj2Inf3GFPUvYtF4tS9lgX5wu2fqlq8hpKks9T7CCl56u0EB1mg0HLJcCaUO2KYYPU4ROE4WYTJBnwwDBwLLPB8SIchqH7Fc6AcKYXi+AE0DsIyMQinAEbSHACgwAAMhAWSFNhzBOtQ-rNG0XS9AY6j5GghGKp8kIgr8-yAnRGHwpUwH+hB5aqbB6z6H8OxfNCjwgRJVBIjACDCeKGJCSJBJEmApLvoYu40vuDJMlOKlcrefn3kuwowGKEpujKcplu8SqYCqwYam6RpoBAzAAGa+E2w4OkmvrOl2MA9n224+bZ-rMtMV7QEgABeKAcFGMZxoUYFYcgqYwOmACMhE5qoebzDBRYlvUPh1XqDXNbs9H5XehW2fZsVbt5goFfSMA1Eg2WWE0ZmaRiGk7KSKXqjAACSaBUCaSDrmdAKhby4WrS6MDZSaBiVaB1Q1TN8RzS1bUoLGik6d1JRgGmTiDaMYzDaNBZjBN0BTUDIMLY2jHbQKRUdnZn0WKkf2+W9-JjhOKCnuelE3vjD6Rau6509eW5M4T3krq+G0le2+mlq54oZKogGYEL-2SeBRFGfMpFod8lHUfWivaV1hNibh+HKcRY0mWMKtIfWkKLYxnjeH4-heCg6AxHEiS2-brm+FgYmCmB9QNNIEYCRG7QRt0PTyaoinDMbyFQ5+1kGZH6BWbCmEfaVjn2G7LnCW77lqJ55NUstO2MmA7MXleqtoHOYWFZUj7RWzfPyLKNYm4Ul0ard93IOu0AwFa2BICi66QFHXMp92vb9pt3MA6WtWUdjYMQ-GmvJj1OF9fDQ38ijizo6W00L1ATUtQ2DGvQuBPjy+epvgLFOX-UHAoNwJ5XmXCGt1XlOLrXkUZDMCANAb4c3kOTGOSdhZZzPGLCWUsZ4yxeFcVelRtYwDwgRUY5smJWwCEPGIA80AagEmiGAABxJUGgPbVVLA0MhAdg72CVBHcurdo56VjuBeOaBE7fk9iVeoyAcgUJzC5NEIi1A5xJPnGQhcDzF1Ltw7+l9mainFA3W+2hm4wBHugZKqoO53Qej3WA-dB4tR0WwsexVOwTwqtPGhrosbH3mkvDq0c14wzhgjbMO98x72LBjBUziT443PtYom9l2Z307FtORAUwASNUBiZR+5VEwAyBYVAwCTQIHIUqD0ESeYwCYTmEhORwEcMgfUJJ5SwCwIQEBTh0sagvDGKUtQCwGjjA6ddaQCx+rhGCIEFYYxNjxF1CgdKUFFirDGMkUAappnGTmR0gAcjMsYUJOjINntDXqGDCIdNUF0npSo+kDKGSMsZEzJz6z2HMhZIAln3NmaM9ZmztnYMtixfwHAADsbgnAoCcDECMwQ4DcQAGzwBpvkuYMAijr3EjY1pjRWgdEYcwoGFcjlKg2cZdCmsqnfjadwr47z8WbPQvA1F9kjxyBQBIjEcAaYSKkXnaeBdq5FyZIo1hyFUnhT-moiU0StHxV0W3AxN0jHdxgL3Mx+CpUXzSQg4mpVypT3vo4yUITXHRnBu4lB8BkXeO3rmfxaNAkH31afbBRSBEgJiRquJPKDwMvRMyjpjM5HpNZvClAhS-XqvsiMXp0hdmxMJkLeorLjxMqVA0ppkCWkGXaec-ptRBnDKjYmTxBzdaIwjZc3NuMcG-MsC-RymwHZIASGAKtfYIC1oAFIQHFIGmITy1RIphvwxB6LmSyR6B0lhn9kKEWwAgYAVaoBwAgI5KAFKM1zAudmq5ebk4krhGSgVptYI-BnXOhdS6V0lo3WW2lkTPoACsO1oGZe28U7KUCElzl5e+3Kf7yL5e-JRqrhVClFRo0BwBtEqvbrKruj0FWmMtAPZVViQ3Xy1ZUomgMj6hLcZDE1aCBoWpGla-emMsPzTPktd1LSomN2ADI-GCTvWZqFTXYDUV1GBrivKPp+jUpdsyjlPKeMUN0s+mhhxqL-QACEQwcpwyvPZWszWb0wb4y1BsSOyhDDAFE76cgUeE1R0Nn0JEusHN+x+MB70vqVKdY9lBT3QDWBGljv82PMmwFodEnGtE6j1DAads7MLilyoBlaonSoea8y1Uz-No0QNJTAZ9j6k0AUaZLZp6r02BZPYu6A57M2lsCFu+EBaN6HMRjlhzeXl1qVXSgddObivlp+dbLws660Nva-KRAwZYDAGwNOwgeQCiIuoZJ2hvt-aB2DsYdhDxqkwBGCAbgeBeE2Qi2OVb7ptB6AMCk+j8TDzbcDAgA7XMRUyBfkyQwuSNw7fkGscVj3jQSli2Ax1tilt5sHAl3dpq+vJoy6mrL4ESv7PK0WkY5sgA)

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `ClientMain`                 |
| `mvn -pl server exec:java` | Build and run the server `ClientMain`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
