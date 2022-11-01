# UniversityTracker

This program was made for the Data Structure subject of the Informatics Engineering degree.

Its goal is, given a JSON map of a Location (specifically a University) with correctly identified rooms, a list of
known people with their respective identifiers and a people movement log (for example, when people scan their cards when
they enter a room), for each movement having the identifier of the person and room associated with the time of
ocurrence, track the movements and contacts of people with many possible queries. It makes use of the
associated [Data Structures Library](https://github.com/daviddmd/DataStructures), which comes pre-packaged with this
program.

For its execution, it needs Java >= 14 and a terminal emulator with UTF-8 support. It can be run directly from the shell
with `java -jar UniversityTracker.jar` (if a JAR already exists) on the same directory of the `files/` directory (with
the required files) or with Gradle by running `./gradlew -q --console plain run`. To build the JARs run `gradle jar`
or `gradle build` and to run the tests, `gradle test`.