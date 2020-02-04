import domain.interact.Request
import network.client.api.ClientInterface

package object client {

  type Client = ClientInterface[Request]

}
