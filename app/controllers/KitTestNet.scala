package controllers

import java.io.File
import com.google.common.util.concurrent.{FutureCallback, ListenableFuture, Futures}
import org.bitcoinj.wallet.Wallet.BalanceType
import org.bitcoinj.crypto.KeyCrypterException
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params._
import org.bitcoinj.utils.BriefLogFormatter
import org.bitcoinj.wallet._
import org.bitcoinj.wallet.listeners._
import org.bitcoinj.core.listeners.AbstractPeerEventListener
import org.bitcoinj.script.Script
import org.bitcoinj.utils.{Threading, BriefLogFormatter}

import org.bitcoinj.core.Coin._

import java.net.InetAddress
import java.util.concurrent.Future
import org.bitcoinj.core._
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.store.{MemoryBlockStore, BlockStore}
import org.bitcoinj.utils.BriefLogFormatter

import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.MoreExecutors

import FetchBlockTestNet._
import DumpWallet._
// import DoubleSpend._

object KitTestNet {
  def main(): Unit = {
    val params: NetworkParameters = TestNet3Params.get
    val path = "./forwarding-service/"
    val kit = new WalletAppKit(params, new File(path), "walletappkit-testnet-example")

    // val blockStore: BlockStore  = new MemoryBlockStore(params)
    // val chain: BlockChain  = new BlockChain(params, blockStore)
    // val peerGroup: PeerGroup  = new PeerGroup(params, chain)

    kit.startAsync
    kit.awaitRunning

    val wListener: KitTestNet.WalletListener = new KitTestNet.WalletListener
    kit.wallet.addEventListener(wListener)

    // FetchBlockTestNet.performFetch("000000000000000016603a15ec1538514af4ba5db4001a6449edcab57d9cc64e")
    println("send money to: " + kit.wallet.freshReceiveAddress.toString)
    // RestoreFromSeedWalletAppKit.main()
    // DumpWallet.main(path)
    // SendRequest.main(params, kit)
    // println("shutting down again")
    // kit.stopAsync()
    // kit.awaitTerminated()
  }


  private class WalletListener extends AbstractWalletEventListener {
    override def onCoinsReceived(wallet: Wallet, tx: Transaction, prevBalance: Coin, newBalance: Coin) = {
      println("coins resceived: " + tx.getHashAsString)
      println("received: " + tx.getValue(wallet))
    }

    override def onTransactionConfidenceChanged(wallet: Wallet, tx: Transaction) = {
      println("confidence changed: " + tx.getHashAsString)
      val confidence: TransactionConfidence = tx.getConfidence
      println("new block depth: " + confidence.getDepthInBlocks)
    }

    override def onCoinsSent(wallet: Wallet, tx: Transaction, prevBalance: Coin, newBalance: Coin) = {
      println("coins sent")
    }

    override def onReorganize(wallet: Wallet) = {}
    override def onWalletChanged(wallet: Wallet) = {}
    def onKeysAdded(keys: List[ECKey]) = { println("new key added") }
    def onScriptsAdded(wallet: Wallet, scripts: List[Script]) = { println("new script added") }
  }

}


object FetchBlockTestNet {
  def performFetch(blockHashString: String): Unit = {
    BriefLogFormatter.init()
    val params: NetworkParameters = TestNet3Params.get()
    val blockStore: BlockStore = new MemoryBlockStore(params)
    val chain: BlockChain = new BlockChain(params, blockStore)
    val peerGroup: PeerGroup = new PeerGroup(params, chain)

    // peerGroup.addPeerDiscovery(new DnsDiscovery(params))
    peerGroup.addAddress(new PeerAddress(InetAddress.getLocalHost(), params.getPort()))
    peerGroup.start()
    peerGroup.waitForPeers(1).get()
    val peer: Peer = peerGroup.getConnectedPeers().get(0)
    val blockHash: Sha256Hash = Sha256Hash.wrap(blockHashString)
    val future: Future[Block] = peer.getBlock(blockHash)

    println("Waiting for node to send us the requested block: " + blockHash);
    val block: Block = future.get()
    println(block)
    peerGroup.stopAsync()
  }
}

object DumpWallet {
  def performDump(path: String): Unit = {
    val wallet: Wallet = Wallet.loadFromFile(new File(path))
    println(wallet.toString(true, true, true, null))
  }

  def main(path: String): Unit = {
    performDump(path)
  }
}

object RestoreFromSeedWalletAppKit extends App {
  def main(): Unit = {
    val params = TestNet3Params.get
    val dir = new File("./forwarding-service/")
    val filePrefix = "restored"
    val walletAppkit: WalletAppKit = new WalletAppKit(params, dir, filePrefix)

    val seedCode = "tesnet"
    val passphrase = ""
    val creationTime = System.currentTimeMillis()

    val seed = new DeterministicSeed(seedCode, null, passphrase, creationTime)

    // walletAppkit.restoreWalletFromSeed(seed)
    println("Restoring Wallet...")

    println(walletAppkit.restoreWalletFromSeed(seed))
    println("Done!")
  }
}

object SendRequest {
  def main(params: NetworkParameters, kit: WalletAppKit): Unit = {
    println("Send money to: " + kit.wallet.currentReceiveAddress.toString)

    val value: Coin = Coin.parseCoin("0.09")
    val to: Address = new Address(params, "mupBAFeT63hXfeeT4rnAUcpKHDkz1n4fdw")

    try {
      val result: Wallet.SendResult = kit.wallet.sendCoins(kit.peerGroup, to, value)
      println("coins sent. transaction hash: " + result.tx.getHashAsString)
    }
    catch {
      case e: InsufficientMoneyException => {
        println("Not enough coins in your wallet. Missing " + e.missing.getValue + " satoshis are missing (including fees)")
        // println("Send money to: " + kit.wallet.currentReceiveAddress.toString)
        val balanceFuture: ListenableFuture[Coin] = kit.wallet.getBalanceFuture(value, BalanceType.AVAILABLE)
        val callback: FutureCallback[Coin] = new FutureCallback[Coin] {
          def onSuccess(balance: Coin) {
            println("coins arrived and the wallet now has enough balance")
          }

          def onFailure(t: Throwable) {
            println("something went wrong")
          }
        }
        Futures.addCallback(balanceFuture, callback)
      }
    }
  }
}


// object DoubleSpend {
//   override def main(args: Array[String]): Unit = {
//     BriefLogFormatter.init()
//     val params: RegTestParams = RegTestParams.get
//     val kit: WalletAppKit = new WalletAppKit(params, new File("."), "doublespend")
//     kit.connectToLocalHost()
//     kit.setAutoSave(false)
//     kit.startAsync()
//     kit.awaitRunning()

//     Console.println(kit.wallet())

    // kit.wallet().getBalanceFuture(COIN, Wallet.BalanceType.AVAILABLE).get

//     val tx1 = kit.wallet().createSend(new Address(params, "muYPFNCv7KQEG2ZLM7Z3y96kJnNyXJ53wm"), CENT)
//     val tx2 = kit.wallet().createSend(new Address(params, "muYPFNCv7KQEG2ZLM7Z3y96kJnNyXJ53wm"), CENT.add(SATOSHI.multiply(10)))

//     val peer: Peer = kit.peerGroup().getConnectedPeers().get(0)

//     peer.addEventListener(new AbstractPeerEventListener() {
//       override def onPreMessageReceived(peer: Peer, m: Message): Message ={
//         Console.err.println("Got a message!" + m)
//         m
//       }
//     }, Threading.SAME_THREAD )

//     peer.sendMessage(tx1)
//     peer.sendMessage(tx2)

//     Thread.sleep(5000)
//     kit.stopAsync()
//     kit.awaitTerminated()
//   }
// }
