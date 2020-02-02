package domain

package object interact {

  trait Request { type Res}
  trait Command extends Request { final type Res = Unit }
  trait Query extends Request

  trait Event { final type Res = Unit }

}
