package object api {

  trait Req { type Res}
  trait Command extends Req { final type Res = Unit }
  trait Query extends Req
  trait Event extends Req { final type Res = Unit }

}
