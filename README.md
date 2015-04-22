xunit-converter-task
====================

Thoughtworks Go plugin to convert test reports to XUnit format.

*Supported Formats:*

* Nose
* JSUnit

*Usage:*

* Download jar from releases & place it in `<go-server-location>/plugins/external` & restart Go Server.
* Select 'XUnit Converter' in Admin -> Pipeline Configuration -> Stage Configuration -> Job Configuration -> Task listing drop-down
* Select 'type' of original test report, 'input directory' containing original reports & 'output directory' where the plugin should write the XUnit format reports into
* Make sure you select 'RunIf Conditions' to 'Any', so the plugin converts reports even if tests fail.
* Add the ouput directory of plugin as 'Test Artifact' in Artifacts tab

*Development:*

```
git clone https://github.com/srinivasupadhya/xunit-converter
cd xunit-converter
mvn clean install -DskipTests
cd ..
git clone https://github.com/srinivasupadhya/xunit-converter-task
cd xunit-converter-task
mvn clean install -DskipTests
```
