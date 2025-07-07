FROM gradle:8.12.1-jdk21

WORKDIR .

COPY . .

RUN gradle installDist

CMD ./build/install/app/bin/app