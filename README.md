# Player Data API
Micro library for storing additional data for players, that isn't stored with main entity NBT.

## Usage:
Add it to your dependencies like this:

```
repositories {
	maven { url 'https://maven.nucleoid.xyz' }
}

dependencies {
	modImplementation include("eu.pb4:sidebar-api:[TAG]")
}
```

After that just use static methods from `PlayerDataApi`. 