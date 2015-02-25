# Lobos
*A Sobol sequence generator for Scala*

*Lobos* provides Sobol low-discrepancy quasirandom sequences which, when used for integration, can cover a domain evenly with as little as a few points and will continue to uniformly fill the space as more points are added.

For efficiency, *Lobos* employs the recursive variant of the gray code optimization proposed by *Antonov-Saleev*.  Initialization values supporting up to 21,201 dimensions are provided courtesy of Stephen Joe and Frances Kuo (found [here](http://web.maths.unsw.edu.au/~fkuo/sobol)).

Below are examples of 2-dimensional points drawn from a Sobol and Uniform generators respectively:
<p align="center">
  <img src="http://wsiegenthaler.github.io/Lobos/sobol.png" alt="Sobol">
  <img src="http://wsiegenthaler.github.io/Lobos/uniform.png" alt="Uniform">
</p>

## Usage

The generator uses the standard Scala ```Iterator``` interface, for example:
```scala
import lobos.SobolSequence

val sequence = new SobolSequence(dims=2)
val points = sequence.take(10)
```
Each n-dimensional point in the sequence contains values between [0,1).  The sequence constructor optionally allows for user-provided initialization values as well as a way to cap sequence length which can improve performance when the number of points needed is low.

## References
* Joe, Stephen, and Frances Y. Kuo. "Notes on Generating Sobol Sequences." (n.d.): n. pag. Aug. 2008. Web.

* "Sobol Sequence." Wikipedia. Wikimedia Foundation, n.d. Web. 25 Feb. 2015.

## License
Everything in this repo is BSD License unless otherwise specified

Lobos (c) 2015 Weston Siegenthaler
