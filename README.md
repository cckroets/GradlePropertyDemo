# GradlePropertyDemo
Demo of implicit task dependency bug in Gradle 6.2 

Repro:
```
$ git clone https://github.com/cckroets/GradlePropertyDemo.git
$ cd GradlePropertyDemo
$ ./gradlew :consume
```
Result:
```
* What went wrong:
Could not determine the dependencies of task ':consume'.
> java.io.FileNotFoundException: /.../GradlePropertyDemo/build/produced.txt (No such file or directory)
```

Expected:
Both `:produce` and `:consume` tasks to execute successfully.

Downgrading to Gradle 6.1 fixes the issue.
