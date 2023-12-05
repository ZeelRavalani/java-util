java-util
=========
<!--[![Build Status](https://travis-ci.org/jdereg/java-util.svg?branch=master)](https://travis-ci.org/jdereg/java-util) -->
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cedarsoftware/java-util/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cedarsoftware/java-util)
[![Javadoc](https://javadoc.io/badge/com.cedarsoftware/java-util.svg)](http://www.javadoc.io/doc/com.cedarsoftware/java-util)

Rare, hard-to-find utilities that are thoroughly tested (> 98% code coverage via JUnit tests).
Available on [Maven Central](https://central.sonatype.com/search?q=java-util&namespace=com.cedarsoftware). 
This library has <b>no dependencies</b> on other libraries for runtime.
The`.jar`file is only`152K.`
Works with`JDK 1.8`through`JDK 21`.
The classes in the`.jar`file are version 52 (`JDK 1.8`).

---
To include in your project:
##### Gradle
```
implementation 'com.cedarsoftware:java-util:2.5.0'
```

##### Maven
```
<dependency>
  <groupId>com.cedarsoftware</groupId>
  <artifactId>java-util</artifactId>
  <version>2.5.0</version>
</dependency>
```
---

Since Java 1.5, you can statically import classes.  Using this technique with many of the classes below, it makes their methods directly accessible in your source code, keeping your source code smaller and easier to read.  For example:

```
import static com.cedarsoftware.util.Converter.*;
```
will permit you to write:
```
...
Calendar cal = convertToCalendar("2019/11/17");
Date date = convertToDate("November 17th, 2019 4:45pm");
TimeStamp stamp = convertToTimeStamp(cal);
AtomicLong atomicLong = convertToAtomicLong("123128300")
String s = convertToString(atomicLong)
...
```

Included in java-util:
* **ArrayUtilities** - Useful utilities for working with Java's arrays `[]`
* **ByteUtilities** - Useful routines for converting `byte[]` to HEX character `[]` and visa-versa.
* **ClassUtilities** - Useful utilities for Class work. For example, call `computeInheritanceDistance(source, destination)` to get the inheritance distance (number of super class steps to make it from source to destination. It will return the distance as an integer.  If there is no inheritance relationship between the two,
then -1 is returned.  The primitives and primitive wrappers return 0 distance as if they are the
same class.
* **Sets**
  * **CompactSet** - Small memory footprint `Set` that expands to a `HashSet` when `size() > compactSize()`.
  * **CompactLinkedSet** - Small memory footprint `Set` that expands to a `LinkedHashSet` when `size() > compactSize()`.
  * **CompactCILinkedSet** - Small memory footprint `Set` that expands to a case-insensitive `LinkedHashSet` when `size() > compactSize()`.
  * **CompactCIHashSet** - Small memory footprint `Set` that expands to a case-insensitive `HashSet` when `size() > compactSize()`.
  * **CaseInsensitiveSet** - `Set` that ignores case for `Strings` contained within.  
* **Maps**  
  * **CompactMap** - Small memory footprint `Map` that expands to a `HashMap` when `size() > compactSize()` entries.
  * **CompactLinkedMap** - Small memory footprint `Map` that expands to a `LinkedHashMap` when `size() > compactSize()` entries.
  * **CompactCILinkedMap** - Small memory footprint `Map` that expands to a case-insensitive `LinkedHashMap` when `size() > compactSize()` entries.
  * **CompactCIHashMap** - Small memory footprint `Map` that expands to a case-insensitive `HashMap` when `size() > compactSize()` entries.      
  * **CaseInsensitiveMap** - `Map` that ignores case when `Strings` are used as keys.
  * **LRUCache** - Thread safe LRUCache that implements the full Map API and supports a maximum capacity.  Once max capacity is reached, placing another item in the cache will cause the eviction of the item that was the least recently used (LRU).
  * **TrackingMap** - `Map` class that tracks when the keys are accessed via `.get()` or `.containsKey()`. Provided by @seankellner
* **Converter** - Convert from one instance to another.  For example, `convert("45.3", BigDecimal.class)` will convert the `String` to a `BigDecimal`.  Works for all primitives, primitive wrappers, `Date`, `java.sql.Date`, `String`, `BigDecimal`, `BigInteger`, `AtomicBoolean`, `AtomicLong`, etc.  The method is very generous on what it allows to be converted.  For example, a `Calendar` instance can be input for a `Date` or `Long`.  Examine source to see all possibilities.
* **DateUtilities** - Robust date String parser that handles date/time, date, time, time/date, string name months or numeric months, skips comma, etc. English month names only (plus common month name abbreviations), time with/without seconds or milliseconds, `y/m/d` and `m/d/y` ordering as well.
* **DeepEquals** - Compare two object graphs and return 'true' if they are equivalent, 'false' otherwise.  This will handle cycles in the graph, and will call an `equals()` method on an object if it has one, otherwise it will do a field-by-field equivalency check for non-transient fields.  Has options to turn on/off using `.equals()` methods that may exist on classes.
* **IO**
  * **FastReader** - Works like `BufferedReader` and `PushbackReader` without the synchronization.  Tracks `line` and `col` by watching for `0x0a,` which can be useful when reading text/json/xml files. You can `.pushback()` a character read, which is very useful in parsers.  
  * **FastWriter** - Works like `BufferedWriter` without the synchronization.  
  * **FastByteArrayInputStream** - Unlike the JDK `ByteArrayInputStream`, `FastByteArrayInputStream` is not `synchronized.`
  * **FastByteArrayOutputStream** - Unlike the JDK `ByteArrayOutputStream`, `FastByteArrayOutputStream` is not `synchronized.`
  * **IOUtilities** - Handy methods for simplifying I/O including such niceties as properly setting up the input stream for HttpUrlConnections based on their specified encoding.  Single line `.close()` method that handles exceptions for you.
* **EncryptionUtilities** - Makes it easy to compute MD5, SHA-1, SHA-256, SHA-512 checksums for `Strings`, `byte[]`, as well as making it easy to AES-128 encrypt `Strings` and `byte[]`'s.
* **Executor** - One line call to execute operating system commands.  `Executor executor = new Executor(); executor.exec('ls -l');`  Call `executor.getOut()` to fetch the output, `executor.getError()` retrieve error output.  If a -1 is returned, there was an error.
* **GraphComparator** - Compare any two Java object graphs. It generates a `List` of `Delta` objects which describe the difference between the two Object graphs.  This Delta list can be played back, such that `List deltas = GraphComparator.delta(source, target); GraphComparator.applyDelta(source, deltas)` will bring source up to match target.  See JUnit test cases for example usage.  This is a completely thorough graph difference (and apply delta), including support for `Array`, `Collection`, `Map`, and object field differences.
* **MathUtilities** - Handy mathematical algorithms to make your code smaller.  For example, minimum of array of values.
* **ReflectionUtils** - Simple one-liners for many common reflection tasks.  Speedy reflection calls due to Method caching.
* **StringUtilities** - Helpful methods that make simple work of common `String` related tasks.
* **SystemUtilities** - A Helpful utility methods for working with external entities like the OS, environment variables, and system properties.
* **Traverser** - Pass any Java object to this Utility class, it will call your passed in anonymous method for each object it encounters while traversing the complete graph.  It handles cycles within the graph. Permits you to perform generalized actions on all objects within an object graph.
* **UniqueIdGenerator** - Generates unique Java long value, that can be deterministically unique across up to 100 servers in a cluster (if configured with an environment variable), the ids are monotonically increasing, and can generate the ids at a rate of about 10 million per second.  Because the current time to the millisecond is embedded in the id, one can back-calculate when the id was generated.

See [changelog.md](/changelog.md) for revision history.

---
### Sponsors
[![Alt text](https://www.yourkit.com/images/yklogo.png "YourKit")](https://www.yourkit.com/.net/profiler/index.jsp)

YourKit supports open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of <a href="https://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a>
and <a href="https://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>,
innovative and intelligent tools for profiling Java and .NET applications.

<a href="https://www.jetbrains.com/idea/"><img alt="Intellij IDEA from JetBrains" src="https://s-media-cache-ak0.pinimg.com/236x/bd/f4/90/bdf49052dd79aa1e1fc2270a02ba783c.jpg" data-canonical-src="https://s-media-cache-ak0.pinimg.com/236x/bd/f4/90/bdf49052dd79aa1e1fc2270a02ba783c.jpg" width="100" height="100" /></a>
**Intellij IDEA**<hr>


By: John DeRegnaucourt and Kenny Partlow
