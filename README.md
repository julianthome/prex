# prex

Approximate Regular Expression Matching for Java. prex determines
how far a string *s* is from matching a regular expression *r*,
*i.e.* how many insertions, deletions and substitutions are
on *s* at least required (minimum cost) such that the resulting
string *s'* is acceptable by *r*.

# Status
[![Build Status](https://travis-ci.org/julianthome/prex.svg?branch=master)](https://travis-ci.org/julianthome/prex.svg?branch=master)  [![codecov](https://codecov.io/gh/julianthome/prex/branch/master/graph/badge.svg)](https://codecov.io/gh/julianthome/prex)


# Approach

The prex algorithm is inspired by the [work of Myers and
Millers](http://www.cs.mun.ca/~harold/Courses/Old/Ling6800.W06/Diary/reg.aprox.pdf)
who provide an algorithm for solving the approximate regular
expression matching problem. However, their approach requires a special pre-computed
data-structure called edit-graph *e* that encodes all possible matches and mismatches
of *s* with respect to
*r* which requires approximately 15xSxR edges and SxR vertices
where S denotes the length of *s* and R is the length of *r*.
Given that data structure, their approach searches a minimal cost path
in *e* with a worst time complexity of
O(SxR). The main strengths
of Myers and Millers approach are the possibility to use real-valued costs for insertion,
deletion and substitution, respectively, and its worst time complexity. The drawback, however,
is that the edit-graph has to be created upfront

Similar to Myers and Millers approach, one can also define real-valued cost
for deletion, insertion and substitution in prex. However, prex is based on the branch and bounds method
(+ extended list) and does not rely on a precomputed edit-graph. Because,
prex is using branch and bounds, in theory it has the same worst time complexity as exhaustive
search. However, our goal is to achieve a reasonable performance in practice by keeping
the algorithm as simple as possible and by incorporating heuristics that improve its scalability.

# Usage

prex can be used as a standalone command line tool and as a library

## Standalone

One could compile prex by running `mvn package` on the command line. The resulting `jar` archive
can be called with `java -jar prex.jar` and provides the following command line options

| Option               | Description                                         |
|----------------------|:----------------------------------------------------|
| -c,--cost \<cost\>   |cost values \<insertion\> \<substitution\> \<deletion\>| |                      |as  double                                           |
| -h                   |  print this message                                 |
| -ic,--ignore-case    |  ignore case of s when matching against r           |
| -r,--regex \<regex\> |  regular expression                                 |
| -s,--string \<string\> |  constant string                                  |
| -t,--print-tree      |  print branch and bounds edit tree                  |


## Library

The following code snippet shows how to use prex after the `jar` file
is imported into your project:

```java
// invoke Prex constructor with regular expression definition,
// r = gcg[abc]+
// default costs for substitution, insertion and deletion are 1.0,
// respectively -- however they can be changed
Prex am = new Prex("gcg[abc]+");
// get cost to make s = GCGa matching r = gcg[abc]+ and
// ignore case
System.out.println(am.evaluateCost("GCGa", true));
// do the same as before, but do not ignore case
System.out.println(am.evaluateCost("GCGa", false));
```

The results of the invocations above look as follows:

```bash
0.0
0.75
```

Please have a look at the test cases and constructors provided by the
`Prex` Java class to get additional information about the usage of prex.



# Licence
Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work except in compliance with the Licence. You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/sites/default/files/eupl1.1.-licence-en_0.pdf

Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the Licence for the specific language governing permissions and limitations under the Licence.
