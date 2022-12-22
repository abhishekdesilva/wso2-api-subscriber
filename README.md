# wso2-api-revisioner

### Recommendations for building
- Java 1.8+
- Maven 3.5.4+

### Tested APIM Versions
- 4.1.0

### Validations in-place
- Following variables are parameterized so that they can be defined according to the environment
> - HOST --> API Manager hostname
> - TRANSPORT_PORT --> API Manager port
> - CLIENT_KEY --> Client Key of an application capable of generating password grant type access token
> - CLIENT_SECRET -->  Client Secret of an application capable of generating password grant type access token
> - USERNAME --> Admin username
> - PASSWORD --> Admin password
> - API_LIMIT --> This number should be higher than the number of APIs available in the Publisher
> - IDS --> List of Application IDs that the APIs should be subscribed to
> - THROTTLE_POLICY --> REST Throttle Policy that needs to be applied
> - WS_THROTTLE_POLICY --> WebSocket Throttle Policy that needs to be applied
> - WS --> To check whether a certain API is either REST or WebSocket
- The above parameters should be defined in a file named "integration.properties" in a directory named "resources". This directory should reside in the same directory in which the tool is.
> <TOOL_HOME>/resources/integration.properties
- A log file named "subscriber.log" will be generated in the "resources" directory once the tool is executed and completed its task.
- When retrieving all APIs, the DEPRECATED and RETIRED APIs will be skipped as they cannot be published and used again.
- The WebSocket APIs are also supported.

### How to run
- Build the tool
> mvn clean install
- Copy the built wso2-api-subscriber-tool-0.0.1-SNAPSHOT.jar to a separate place and let's call that <TOOL_HOME>
- Copy the "resources" directory available in the project to <TOOL_HOME>
- Change the parameters according to your environment
- Run the tool
> java -jar wso2-api-subscriber-tool-0.0.1-SNAPSHOT.jar
- After the process is complete, the subscriber.log file will be available in the <TOOL_HOME>/resources directory