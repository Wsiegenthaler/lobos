package lobos


package object breeze {
  import _root_.breeze.linalg.{DenseMatrix, DenseVector}

  implicit def indexedSeq2Vector(seq:IndexedSeq[Double]):DenseVector[Double] = DenseVector(seq:_*)
  implicit def indexedSeqIterator2Matrix(iterator:Iterator[IndexedSeq[Double]]):DenseMatrix[Double] = DenseMatrix(iterator.toSeq:_*)
}
