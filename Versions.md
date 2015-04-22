## v1 ##

Initial import of the project

### [r5](https://code.google.com/p/savitzky-golay-filter/source/detail?r=5) ###

  * Fixed MeanValuePadder bug resulting in IndexOutOfBoundsException

## v1\_1 ##

  * Added DataFilter interface which adds ability to filter data points based on some algorithm. Currently package contains one implementation - based on [Ramer-Douglas-Peucker algorithm](http://en.wikipedia.org/wiki/Ramer-Douglas-Peucker_algorithm)

## v1\_2 ##

  * Now bundled with commons-math 2.0 . Build contains full debug information, previously omitted. Source code underwent small re-organization

### [r25](https://code.google.com/p/savitzky-golay-filter/source/detail?r=25) ###

  * Changed partial sum type from float to double which will lead to improved accuracy when applying filter to large numbers

## SGFilterVis 2 ##

  * Few annoying bugs have been fixed - most notably invalid refreshing of selected point
  * Zooming has been redesigned
  * Now uses Liquid LAF (https://liquidlnf.dev.java.net/)