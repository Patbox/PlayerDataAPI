# Player Data API
Micro library for storing additional data for players, that isn't stored with main entity NBT.

## Usage:
Add it to your dependencies like this:

```
repositories {
	maven { url 'https://maven.nucleoid.xyz' }
}

dependencies {
	modImplementation include("eu.pb4:player-data-api:[TAG]")
}
```

For ```[TAG]```/player data api version I recommend you checking [this maven](https://maven.nucleoid.xyz/eu/pb4/player-data-api/)

After that just use static methods from `PlayerDataApi`. 
