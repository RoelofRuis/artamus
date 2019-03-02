package storage.file

import core.Idea

object Serializers {

  implicit object IdeaSerializer extends Serializer[Idea] {
    def serialize(in: Idea): String = in.title + "\n"
    def deserialize(in: String): Idea = Idea(in)
  }

}
