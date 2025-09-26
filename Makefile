app:
	./gradlew bootRun

clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew installDist

test:
	./gradlew test

check-java-deps:
	./gradlew dependencyUpdates -Drevision=release

sonar:
	./gradlew sonar

.PHONY: build