# lobos
*A Sobol sequence generator for Scala*

*lobos* provides Sobol low-discrepancy quasirandom sequences which, when used for integration, can cover a domain evenly with as little as a few points and will continue to uniformly fill the space as more points are added.

For efficiency, *lobos* employs the recursive variant of the gray code optimization proposed by *Antonov-Saleev*.  Initialization values supporting up to 21,201 dimensions are provided courtesy of Stephen Joe and Frances Kuo (found [here](http://web.maths.unsw.edu.au/~fkuo/sobol)).

Below are examples of 2-dimensional points drawn from Sobol and Uniform generators respectively:
<p align="center">
  <img src="http://wsiegenthaler.github.io/lobos/sobol.png" alt="Sobol">
  <img src="http://wsiegenthaler.github.io/lobos/uniform.png" alt="Uniform">
</p>

## Usage

The generator uses the standard Scala ```Iterator``` interface, for example:
```scala
import lobos.SobolSequence

val sequence = new SobolSequence(dims=2)
val points = sequence.take(10)
```
Each n-dimensional point in the sequence contains values between [0,1).  The sequence constructor optionally allows for user-provided initialization values as well as a way to cap sequence length which can improve performance when the number of points needed is low.

## Installation

Since it's currently not available as a published binary artifact, the recommended way to integrate *lobos* is to allow SBT to automatically checkout and build the source with your project.  This can easily be configured with your project's ```project/Build.scala``` definition:

```scala
import sbt._

object MyBuild extends Build {
  lazy val project = Project("my-project", file("."))
    .dependsOn(RootProject(uri("git://github.com/wsiegenthaler/lobos.git")))
}
```
Be sure to replace ```my-project``` with the name of your project as configured in ```build.sbt```.  Note that this method requires SBT 0.11 or greater.

## References

* Joe, Stephen, and Frances Y. Kuo. ["Notes on Generating Sobol Sequences."](http://web.maths.unsw.edu.au/~fkuo/sobol/joe-kuo-notes.pdf) (n.d.): n. pag. Aug. 2008. Web.

* "[Sobol Sequence.](http://en.wikipedia.org/wiki/Sobol_sequence)" Wikipedia. Wikimedia Foundation, n.d. Web. 25 Feb. 2015.

## License

Everything in this repo is BSD License unless otherwise specified

lobos (c) 2015 Weston Siegenthaler
