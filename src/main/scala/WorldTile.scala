
object TileType extends Enumeration {
  type Terrain = Value
  val Plain, Mountain, Ocean = Value
}
object WorldTile {
  def fromHash(hash: Short): WorldTile = {
    val typeId = hash >> 8
    val temperature = hash & 0xFF
    WorldTile(TileType.apply(typeId), temperature.toByte)
  }
}
case class WorldTile(tileType: TileType.Terrain, temperature: Byte){
  def hash: Short = {
    ((tileType.id.toByte << 8) | temperature).toShort
  }
}