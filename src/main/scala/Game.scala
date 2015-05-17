import akka.actor._

import scala.collection.mutable.ArrayBuffer

sealed trait State
case object Menu extends State
case object Generation extends State
case object Render extends State
case object Play extends State
case object Exit extends State

sealed trait Data
case object Uninitialized extends Data
case class WorldState(heightMap: ArrayBuffer[ArrayBuffer[Int]]) extends Data

case class StartMenu(ref: ActorRef)
case class GenerateWorld(ref: ActorRef)
case class RenderWorld(ref: ActorRef)
case class SaveWorld(heightMap: ArrayBuffer[ArrayBuffer[Int]])
case class Exit(ref: ActorRef)

object Game extends App {
  val system = ActorSystem("gameSystem")
  val gameActor = system.actorOf(Props[Game], "gameMain")
  gameActor ! StartMenu(gameActor)
  gameActor ! GenerateWorld(gameActor)
  sys addShutdownHook {
    gameActor ! Kill
    system.shutdown()
  }
}

class Game extends LoggingFSM[State, Data] {

  val renderActorRef = context.actorOf(RenderActor.props, name = "renderActor")
  val worldActorRef = context.actorOf(WorldActor.props, name = "worldActor")

  startWith(Menu, Uninitialized)

  when(Menu) {
    case Event(StartMenu(ref), Uninitialized) => goto(Generation)
  }

  when(Generation) {
    case Event(GenerateWorld(ref), Uninitialized) =>
      worldActorRef ! "start"
      stay()

    case Event(SaveWorld(heightMap), Uninitialized) =>
      goto(Render) using WorldState(heightMap = heightMap)
  }

  when(Render) {
    case Event(RenderWorld(ref), WorldState(heightMap)) =>
      renderActorRef ! stateData
      stay()
    case Event("redraw", WorldState(heightMap)) =>
      renderActorRef ! stateData
      stay()
  }

  when(Exit) {
    case Event(Exit(ref), Uninitialized) =>
      stop(FSM.Normal)
  }

  onTransition {
    case Generation -> Render => self ! RenderWorld(self)
  }

  onTermination {
    case StopEvent(FSM.Normal, state, data) =>
      sys.exit()
  }

  initialize()
}
