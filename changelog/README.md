# Changelog Generation

## Overview

This directory contains the source files and tooling for generating `ChangeLog.html`, which is bundled with the Samurai Graph application.

## Files

| File | Description |
|------|-------------|
| `product.xml` | Source XML containing all release notes (edit this file to update the changelog) |
| `ChangeLog.xsl` | XSLT stylesheet that transforms `product.xml` into HTML |

## Build

`ChangeLog.html` is automatically generated during the Maven build (`generate-resources` phase):

```bash
mvn generate-resources
```

The output is placed at `target/classes/ChangeLog.html` and included in the packaged JAR.

## Updating the Changelog

To add a new release, edit `product.xml` and insert a new `<release>` entry at the top:

```xml
<product>
  <release>
    <releaseinfo>
      <releasedesc>Samurai Graph X.Y.Z</releasedesc>
      <releasedate>YYYY-MM-DD</releasedate>
      <changelog>
        <itemize>
          <item>Feature or fix description.</item>
        </itemize>
      </changelog>
    </releaseinfo>
  </release>
  <!-- ... existing releases ... -->
</product>
```

Then run `mvn generate-resources` to regenerate `ChangeLog.html`.

