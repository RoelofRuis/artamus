package storage.file

import core.idea.Idea

object Serializers {

  implicit object IdeaSerializer extends Serializer[Idea] {
    def serialize(in: Idea): String = s"${in.id},${in.title}\n"
    def deserialize(in: String): Idea = {
      val parts = in.split(',')
      Idea(parts(0).toLong, parts(1))
    }
  }

}
