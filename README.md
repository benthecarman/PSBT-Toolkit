# PSBT-Toolkit

PSBT Toolkit is aimed to give you a nice gui that gives you functionality for PSBT interactions.
There are functions to serve the Creator, Updater, Combiner, Input Finalizer, and Transaction Extractor roles, as well as some utility functions for transactions,
and some generators for PSBTs and Transactions.

## Building from source

The first step in getting setup will be getting the [Java Development Kit](https://www.oracle.com/java/technologies/javase-downloads.html) (JDK) installed on your machine.
PSBT ToolKit works best with Java 8 but should also work with Java 11 and Java 13.

Once java is setup on your machine (try running javac -version), you are ready to download and install the [Scala Build Tool](https://www.scala-sbt.org/download.html) (sbt). 
Note that running sbt for the first time will take a while.

After having these installed you simply need to clone the repo, then run with `sbt run`.
