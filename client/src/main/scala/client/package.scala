import domain.interact.Request
import protocol.client.api.ClientInterface

package object client {

  type Client = ClientInterface[Request]

}
