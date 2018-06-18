package controllers

import javax.inject._
import java.util.concurrent.Future
import play.api._
import play.api.mvc._
import java.io.File
import java.net.InetAddress

import org.bitcoinj.core._
import org.bitcoinj.crypto.KeyCrypterException
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params._
import org.bitcoinj.utils.BriefLogFormatter
import org.bitcoinj.wallet._
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener

import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.MoreExecutors


// import static com.google.common.base.Preconditions.checkNotNull

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def bitcoinj = Action { implicit request =>
    // Init("6ed8296b95638e0dd0ee32fc98796b6c2273bd98826c62363340006aa6660b99")
    controllers.KitTestNet.main()
    // Init()
    Ok("bitcoinj")
  }

  def Init(): Unit =  {
    BriefLogFormatter.init()
    println("Connecting to node")

    // val params: NetworkParameters = TestNet3Params.get()
    // val filePrefix: String = "forwarding-service-testnet"

    // val forwardingAddress = new Address(params, "2N1eMSZRn4ZsZXZauDLBjmQvkXCegVqpTEr")

    // val kit = new WalletAppKit(params, new File("./forwarding-service/"), filePrefix) {
    //   override def onSetupCompleted() {
    //     if (wallet().getKeyChainGroupSize() < 1)
    //       wallet().importKey(new ECKey())
    //   }
    // }

    // if (params == TestNet3Params.get()) {
    //     kit.connectToLocalHost()
    // }

    // kit.startAsync()
    // kit.awaitRunning()

    // kit.wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
    //   override def onCoinsReceived(w: Wallet, tx: Transaction, prevBalance: Coin, newBalance: Coin) {
    //     println("Error man ko")
    //       // Runs in the dedicated "user thread".
    //   }
    // })

    // kit.wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
    //   override def onCoinsReceived(w: Wallet, tx: Transaction, prevBalance: Coin, newBalance: Coin) {

    //     val value: Coin = tx.getValueSentToMe(w);
    //     println("Received tx for " + value.toFriendlyString() + ": " + tx);
    //     println("Transaction will be forwarded after it confirms.");

    //     Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback[TransactionConfidence]() {
    //       override def onSuccess(result: TransactionConfidence) {
    //         println(result)
    //         // forwardCoins(result)
    //       }


    //       override def onFailure(t: Throwable) { println(t) }
    //     })



    //     // val value: Coin = tx.getValueSentToMe(kit.wallet())
    //     println("Forwarding " + value.toFriendlyString() + " BTC")

    //     val amountToSend: Coin = value.subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE)
    //     val sendResult: Wallet.SendResult = kit.wallet().sendCoins(kit.peerGroup(), forwardingAddress, amountToSend)
    //     println("Sending ...")

    //     println("Sent coins onwards! Transaction hash is " + sendResult.tx.getHashAsString())
    //     // sendResult.broadcastComplete.addListener(new Runnable() {
    //     //   override def run() {
    //     //     println("Sent coins onwards! Transaction hash is " + sendResult.tx.getHashAsString())
    //     //   }
    //     // })
    //   }
    // })
  }

}
