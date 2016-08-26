# lobos
*A Sobol sequence generator for Scala and Javascript*

*lobos* provides Sobol low-discrepancy quasirandom sequences which, when used for integration, can cover a domain evenly with as little as a few points and will continue to uniformly fill the space as more points are added.

For efficiency, *lobos* employs the recursive variant of the gray code optimization proposed by *Antonov-Saleev*.  Initialization values supporting up to 21,201 dimensions are provided courtesy of Stephen Joe and Frances Kuo (found [here](http://web.maths.unsw.edu.au/~fkuo/sobol)).

Below are examples of 2-dimensional points drawn from Sobol and Uniform generators respectively. See the full animated visualization [here](http://wsiegenthaler.github.io/lobos/web-example.html).
<p align="center">
  <img src="http://wsiegenthaler.github.io/lobos/sobol.png" alt="Sobol" width="49%">
  <img src="http://wsiegenthaler.github.io/lobos/uniform.png" alt="Uniform" width="49%">
</p>


## Usage

Each n-dimensional point in the sequence contains values between [0,1).  The sequence constructor optionally allows for user-provided initialization parameters as well as a way to cap sequence resolution to improve performance when the number of points needed is known in advance (see `params` and `resolution` options).


#### Scala

Add *lobos* to your sbt dependencies:
```scala
libraryDependencies += "com.github.wsiegenthaler" % "lobos_2.11" % "0.9.22"

resolvers += "releases" at "https://oss.sonatype.org/content/repositories/releases"
```

To obtain the first 10 points from a 3-dimensional sequence:
```scala
import lobos.Sobol
import lobos.params.NewJoeKuo21k // initialization params

val sequence = new Sobol(dims=3)
val points = sequence.take(10)
```

#### Javascript

*lobos* is available via npm and should work with webpack/browserify:
```shell
npm install --save lobos
```

For legacy web projects, a standalone version of *lobos* can be built by following the Javascript build procedure and using `js/dist/lobos-standalone.js`. This version is also included with the npm module (see `node_modules/lobos/js/dist`).

To draw from the sequence:
```javascript
var lobos = require('lobos') // unecessary for standalone version

var dims = 3
var options = { params: 'new-joe-kuo-6.21201', resolution: 32 } // *optional*
var sequence = new lobos.Sobol(dims, options)
var points = sequence.take(10)
```

To conserve resources in browser environments, the javascript version of *lobos* defaults to the `new-joe-kuo-6.1000` initialization params which only support up to 1000 dimensions. Specify `new-joe-kuo-6.21201` for more.


## Building

#### Scala
```shell
sbt package 
```

#### Javascript
```shell
gulp build
```

See `js/dist/` for build output:
* `lobos.js` - main module
* `lobos-params.js` - parameter module
* `lobos-standalone.js` - self-contained web-ready with 'lobos' exported as global


## References

* Joe, Stephen, and Frances Y. Kuo. ["Notes on Generating Sobol Sequences."](http://web.maths.unsw.edu.au/~fkuo/sobol/joe-kuo-notes.pdf) (n.d.): n. pag. Aug. 2008. Web.

* "[Sobol Sequence.](http://en.wikipedia.org/wiki/Sobol_sequence)" Wikipedia. Wikimedia Foundation, n.d. Web. 25 Feb. 2015.

## License

Everything in this repo is BSD License unless otherwise specified

lobos (c) 2015 Weston Siegenthaler
