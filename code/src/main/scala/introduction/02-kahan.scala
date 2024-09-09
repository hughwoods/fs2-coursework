package introduction

import fs2.*

object Kahan {

  private case class CompensatedFloat(magnitude: Float, compensation: Float)
  private object CompensatedFloat {
    val zero = CompensatedFloat(0f, 0f)
  }

  private def add(total: CompensatedFloat, toAdd: Float): CompensatedFloat = {
    val y: Float = toAdd - total.compensation
    val t: Float = total.magnitude + y
    CompensatedFloat(magnitude = t, compensation = (t - total.magnitude) - y)
  }

  // Perform Kahan summation, returning a Stream with a single element that is
  // the total
  def sum(stream: Stream[Pure, Float]): Stream[Pure, Float] =
    stream
      .fold(CompensatedFloat.zero)(add)
      .map(_.magnitude)

  // Perform Kahan summation, returning a Stream where each element is the sum
  // of elements in the input up to that point.
  //
  // This is often more useful in a streaming application because the input may
  // never end, or we may want the most up-to-date result at any given point in
  // time.
  def cumulativeSum(stream: Stream[Pure, Float]): Stream[Pure, Float] =
    stream
      .scan(CompensatedFloat.zero)(add)
      .map(_.magnitude)
      .drop(1)
}
