import api.{Command, Query}
import protocol.client.api.ClientInterface

package object client {

  type Client = ClientInterface[Command, Query]

}
