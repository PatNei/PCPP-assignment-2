# Running JUnit tests

* JUnit tests are located in the directory `src/test/java/lecture04/`. This is an important detail, as JUnit tests are the ones we will use throughout the course.

* To run JUnit tests with Gradle, execute `$ gradle cleanTest test --tests <package>.<test_class>` from the root of the project. For instance, `$ gradle cleanTest test --tests lecture04.CounterTest`.
