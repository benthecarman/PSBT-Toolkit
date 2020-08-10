# Release Process

## Step 1

`sbt assembly`

## Step 2

```
cat stub.sh target/scala-2.13/psbt-toolkit-assembly-0.1.1.jar > psbt-toolkit && chmod +x psbt-toolkit
```

## Step 3

```
Use Launch4j on a windows machine to create psbt-toolkit.exe
```
