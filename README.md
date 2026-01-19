# Generatable
Generatable - Intellij IDEA Plugin

Plugin added some actions to Generate menu:

    Generate inner Builder class
    Generate All: getters, equals and hashCode, toString, Constructor, Builder

See also:

    FuGen - You can create custom generate-actions by templates on the fly!
    https://plugins.jetbrains.com/plugin/7188

### Jetbrains Marketplace
[https://plugins.jetbrains.com/plugin/7733-generatable](https://plugins.jetbrains.com/plugin/7733-generatable)

### Build / Install
1. Run buildPlugin task
```
./gradlew :buildPlugin
```
2. Copy output-file from `build/distributions/Generatable-1.0-SNAPSHOT.zip`

3. Install your custom plugin from disk

```
IDEA -> Settings -> Plugins -> (gear) -> Install plugin from disk
Navigate to Generatable-1.0-SNAPSHOT.zip
```
