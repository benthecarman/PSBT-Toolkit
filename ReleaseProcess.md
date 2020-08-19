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

## Step 4

Create `SHA256SUMS` file

`sha256sum` each file

Sign `SHA256SUMS` file with

```
gpg -u 0AD83877C1F0CD1EE9BD660AD7CC770B81FD22A8 --clear-sign SHA256SUMS
```