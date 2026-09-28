# Runnable end to end with one command, on a machine with nothing installed.
#
# One stage, one JDK. These programs use nothing newer than Java 21, so the
# multi stage version ladder that the Modern Java repo needs would be
# ceremony here.
FROM eclipse-temurin:21-jdk

WORKDIR /demos
COPY src ./src
COPY run.sh ./run.sh
RUN chmod +x run.sh

ENV JAVA_HOME=/opt/java/openjdk

# No arguments runs all three, in the order the video shows them.
CMD ["bash", "-c", "for d in RetryNoKey RetryWithKey StaleCache; do echo \"=== $d ===\"; java src/$d.java; echo; done"]
