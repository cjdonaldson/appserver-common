# appserverCommon

### pre-commit
```
ln -s pre-push.sh .git/hooks/pre-push
```

### [mill setup](http://www.lihaoyi.com/mill/)
```
touch build.sc
# edit build.sc as per mill docs per project needs
```

### [mill config](https://com-lihaoyi.github.io/mill/mill/Configuring_Mill.html)

### tasks
```
mill inpspect __  # double underscore
mill inpspect appserverCommon
mill resolve _
mill resolve __
mill clean
mill __.compile
mill __.test
mill __.scoverage.htmlReport
mill __.reformat
mill __.publishLocal
mill all __.{compile,run}
mill -w __.compile
mill -w __.test
mill appserverCommon.reformat
mill appserverCommon.scoverage.compile
mill appserverCommon.scoverage.htmlReport

# zsh makes these more difficult as the param must be wraped in `"`
mill [-w] appserverCommon[2.12].compile
mill [-w] appserverCommon[2.12].run
mill [-w] appserverCommon[2.12].runBackground
mill [-w] appserverCommon[2.12].launcher
mill [-w] appserverCommon[2.12].jar
mill [-w] appserverCommon[2.12].assembly
mill -i appserverCommon[2.13].console
mill -i appserverCommon[2.13].repl
```

### [mysql docs](https://dev.mysql.com/doc/refman/5.7/en)
- notes:
