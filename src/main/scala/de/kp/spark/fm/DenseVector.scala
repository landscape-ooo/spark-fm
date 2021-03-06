package de.kp.spark.fm
/* Copyright (c) 2014 Dr. Krusche & Partner PartG
* 
* This file is part of the Spark-FM project
* (https://github.com/skrusche63/spark-fm).
* 
* Spark-FM is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* 
* Spark-FM is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* Spark-FM. 
* 
* If not, see <http://www.gnu.org/licenses/>.
*/

import scala.language.implicitConversions

object DenseVector {
  
  def apply(elements: Array[Double]) = new DenseVector(elements)

  def apply(elements: Double*) = new DenseVector(elements.toArray)

  def apply(length: Int, initializer: Int => Double): DenseVector = {
    
    val elements: Array[Double] = Array.tabulate(length)(initializer)
    new DenseVector(elements)
  
  }

  def zeros(length: Int) = new DenseVector(new Array[Double](length))

  def ones(length: Int) = DenseVector(length, _ => 1)

  class Multiplier(num: Double) {
    def * (vec: DenseVector) = vec * num
  }

  implicit def doubleToMultiplier(num: Double) = new Multiplier(num)

}
class DenseVector(elements: Array[Double]) extends Serializable {

  def length = elements.length

  def apply(index: Int) = elements(index)

  def + (other: DenseVector): DenseVector = {
    
    if (length != other.length) {
      throw new IllegalArgumentException("DenseVectors of different length")
    }
    
    DenseVector(length, i => DenseVector.this(i) + other(i))
  
  }

  def get() = elements
  
  def add(other: DenseVector) = DenseVector.this + other

  def - (other: DenseVector): DenseVector = {
    
    if (length != other.length) {
      throw new IllegalArgumentException("DenseVectors of different length")
    }
    
    DenseVector(length, i => DenseVector.this(i) - other(i))
  
  }

  def subtract(other: DenseVector) = DenseVector.this - other

  def dot(other: DenseVector): Double = {
    
    if (length != other.length) {
      throw new IllegalArgumentException("DenseVectors of different length")
    }
    
    var ans = 0.0
    var i = 0
    while (i < length) {
      ans += DenseVector.this(i) * other(i)
      i += 1
    }
    ans
  }

  /**
   * return (this + plus) dot other, but without creating any intermediate storage
   * @param plus
   * @param other
   * @return
   */
  def plusDot(plus: DenseVector, other: DenseVector): Double = {
    
    if (length != other.length) {
      throw new IllegalArgumentException("DenseVectors of different length")
    }
    
    if (length != plus.length) {
      throw new IllegalArgumentException("DenseVectors of different length")
    }
    
    var ans = 0.0
    var i = 0
    while (i < length) {
      ans += (DenseVector.this(i) + plus(i)) * other(i)
      i += 1
    }
    ans
  }

  def += (other: DenseVector): DenseVector = {
    
    if (length != other.length) {
      throw new IllegalArgumentException("DenseVectors of different length")
    }
    var i = 0
    while (i < length) {
      elements(i) += other(i)
      i += 1
    }
    DenseVector.this
  }

  def addInPlace(other: DenseVector) = DenseVector.this +=other

  def * (scale: Double): DenseVector = DenseVector(length, i => DenseVector.this(i) * scale)

  def multiply (d: Double) = DenseVector.this * d

  def / (d: Double): DenseVector = DenseVector.this * (1 / d)

  def divide (d: Double) = DenseVector.this / d

  def unary_- = DenseVector.this * -1

  def sum = elements.reduceLeft(_ + _)

  def squaredDist(other: DenseVector): Double = {
    var ans = 0.0
    var i = 0
    while (i < length) {
      ans += (DenseVector.this(i) - other(i)) * (DenseVector.this(i) - other(i))
      i += 1
    }
    ans
  }

  def dist(other: DenseVector): Double = math.sqrt(squaredDist(other))

  def update(i: Int, value: Double) = elements.update(i, value)

  override def toString = elements.mkString("(", ", ", ")")
  
}