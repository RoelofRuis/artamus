package nl.roelofruis.artamus.core

package object api {

  trait Request { type Res }
  trait Command extends Request { final type Res = Unit }
  trait Query extends Request

  trait Event { final type Res = Unit }

}
