# Zip Maven Plugin

This is a simple, but really efficient, maven plugin to create a ZIP archive.
It can be used to create an attached zip artifact, or to package a zip with the
help of 

```xml
<extensions>true</extensions>
```

in plugin definition.

#### Why another zip plugin? 
I just don't like other plugins.

On top of that, I've always wanted to develop a Maven plugin for the community 
that has always provided me with great solutions ... and so here we are, 
a maven plugin for creating zip artifacts. 

#### Why not assembly?
Because assembly can't provide zip packaging type.

# Usage
Plugin documentation is available [here](https://wolfetti.github.io/zip-maven-plugin)