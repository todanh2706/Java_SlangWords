# Java Project: Slang Words

### Brief description

The desktop application was built using JavaFX and Maven, allows user to manage, search and learn the slangs through out a friendly user interface.

### How to run? (For dev)

The bash command to run the project in this state is (suppose that you are being at the root dir of the project)

```
$ cd slang-words-app; mvn clean javafx:run
```

### How to run? (For casual user)

The bash command to run the project in this state is (suppose that you are being at the root dir of the project)

```
$ cd slang-words-app/target; java -jar slang-dictionary-app-1.0-SNAPSHOT.jar
```

### Application overview

-   Welcome page:
-   Main page:
-   Quiz:
-   Admin:

### Prominent feature

-   Slang of the day: randomly show slang in the center of the welcome page (randomly change once per day).
-   Slang management:
    -   Add new slang. If the slang is already exists then ask user to override or duplicate.
    -   Edit the slangs.
    -   Delete the slangs.
-   Quiz:
    -   Guess the slang/guess the definition.
-   Admin:
    -   Reset the .ser file and reload from the original text file.
-   Searching history.
-   Saving mechanism: save the changes for any adjustment.

### Tech stack

-   Language: Java 11+.
-   GUI: JavaFX.
-   Manager and builder: Apache Maven.
-   Packing tool: `maven-shade-plugin`.
