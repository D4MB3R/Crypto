You need to provide the absolute path of the folder which holds the CSV files in target/application.properties.
The application can be run with: java -jar target/Crypto-0.0.1-SNAPSHOT.jar (or mvnw spring-boot:run).
As there are test failures, I built the app with: mvnw package -DskipTests
The docker image can be built with running the following command inside the Crypto folder: docker build -t crypto-recommender .