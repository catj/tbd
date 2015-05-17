
object ColorSchemes {
  val NineClassSpectral = NineClassInterpolation(
    one = RGBColor(213, 62, 79),
    two = RGBColor(244, 109, 67),
    three = RGBColor(253, 174, 97),
    four = RGBColor(254, 224, 139),
    five = RGBColor(255, 255, 191),
    six = RGBColor(230, 245, 152),
    seven = RGBColor(171, 221, 164),
    eight = RGBColor(102, 194, 165),
    nine = RGBColor(50, 136, 189)
  )
  val FiveColorBlindFriendly = FiveClassInterpolation(
    one = RGBColor(255, 255, 204),
    two = RGBColor(161, 218, 180),
    three = RGBColor(65, 182, 196),
    four = RGBColor(44, 127, 184),
    five = RGBColor(37, 52, 148)
  )

  val SevenColorBlindFriendly = SevenClassInterpolation(
    one = RGBColor(215, 48, 39),
    two = RGBColor(252, 141, 89),
    three = RGBColor(254, 224, 144),
    four = RGBColor(255, 255, 191),
    five = RGBColor(224, 243, 248),
    six = RGBColor(145, 191, 219),
    seven = RGBColor(69, 117, 180)
  )
}

case class RGBColor(red: Short, green: Short, blue: Short)

case class FiveClassInterpolation(one: RGBColor, two: RGBColor, three: RGBColor, four: RGBColor, five: RGBColor) extends InterpolationClass {
  override def getColorsForBucket(bucket: Int): (RGBColor, RGBColor) = {
    bucket match {
      case 0 => (one, two)
      case 1 => (two, three)
      case 2 => (three, four)
      case 3 => (four, five)
      case 4 => (five, five)
    }
  }

  override def getNumberOfBuckets: Int = 4
}

case class SevenClassInterpolation(one: RGBColor, two: RGBColor, three: RGBColor, four: RGBColor, five: RGBColor,
                                   six: RGBColor, seven: RGBColor) extends InterpolationClass {
  override def getColorsForBucket(bucket: Int): (RGBColor, RGBColor) = {
    bucket match {
      case 0 => (one, two)
      case 1 => (two, three)
      case 2 => (three, four)
      case 3 => (four, five)
      case 4 => (five, six)
      case 5 => (six, seven)
      case 6 => (seven, seven)
    }
  }

  override def getNumberOfBuckets: Int = 6
}

case class NineClassInterpolation(one: RGBColor, two: RGBColor, three: RGBColor, four: RGBColor, five: RGBColor,
                                  six: RGBColor, seven: RGBColor, eight: RGBColor, nine: RGBColor) extends InterpolationClass {
  override def getColorsForBucket(bucket: Int): (RGBColor, RGBColor) = {
    bucket match {
      case 0 => (one, two)
      case 1 => (two, three)
      case 2 => (three, four)
      case 3 => (four, five)
      case 4 => (five, six)
      case 5 => (six, seven)
      case 6 => (seven, eight)
      case 7 => (eight, nine)
      case 8 => (nine, nine)
    }
  }

  override def getNumberOfBuckets: Int = 8
}

class ColorInterpolation(min: Int, max: Int, interpolationClass: InterpolationClass) {
  val spread = max - min
  val step: Double = spread / interpolationClass.getNumberOfBuckets.toDouble

  def interpolateChannel(channel1: Short, channel2: Short, value: Double): Short = {
    ((1 - value) * channel1 + value * channel2).toShort
  }

  def interpolate(value: Int): RGBColor = {
    val adjustedValue = value - min
    val bucket = adjustedValue / spread.toDouble * interpolationClass.getNumberOfBuckets
    val bucketMax = (bucket.toShort + 1) * step
    val bucketMin = bucket.toShort * step
    val scaledValue = (adjustedValue - bucketMin) / step
    val (color1, color2) = interpolationClass.getColorsForBucket(bucket.toInt)
    new RGBColor(
      interpolateChannel(color1.red, color2.red, scaledValue),
      interpolateChannel(color1.green, color2.green, scaledValue),
      interpolateChannel(color1.blue, color2.blue, scaledValue)
    )
  }
}

trait InterpolationClass {
  def getColorsForBucket(bucket: Int): (RGBColor, RGBColor)

  def getNumberOfBuckets: Int
}