package storage.file

import core.ID
import core.idea.Idea

object Serializers {

  implicit object IdeaSerializer extends Serializer[Idea] {
    def serialize(in: Idea): String = s"${in.id},${in.title}\n"
    def deserialize(in: String): Idea = {
      val parts = in.split(',')
      Idea(ID(parts(0).toLong), parts(1))
    }
  }

}
