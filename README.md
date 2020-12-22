# PSBT-Toolkit

[![Build Status](https://github.com/benthecarman/PSBT-Toolkit/workflows/Compile%20Check/badge.svg)](https://github.com/benthecarman/PSBT-Toolkit/actions)

PSBT Toolkit aims to give you a nice gui that gives you functionality for PSBT interactions.
There are functions to serve the Creator, Updater, Combiner, Input Finalizer, and Transaction Extractor roles, as well as some utility functions for transactions,
and some generators for PSBTs and Transactions.

## Building from source

To get started you will need Java, Scala, and some other nice tools installed, luckily the Scala team has an easy setup process!

Simply follow the instructions in [this short blog](https://www.scala-lang.org/2020/06/29/one-click-install.html) to get started.

After having these installed you simply need to clone the repo, then run with `sbt run`.

### macOS install
```
brew install scala
brew install sbt
```
