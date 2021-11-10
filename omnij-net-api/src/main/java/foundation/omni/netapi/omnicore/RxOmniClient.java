package foundation.omni.netapi.omnicore;

import foundation.omni.rpc.OmniClient;
import io.reactivex.rxjava3.core.Flowable;
import org.bitcoinj.core.NetworkParameters;
import org.consensusj.bitcoin.json.pojo.ChainTip;
import org.consensusj.bitcoin.rx.ChainTipService;
import org.consensusj.bitcoin.rx.jsonrpc.PollingChainTipServiceImpl;
import org.consensusj.bitcoin.rx.jsonrpc.RxJsonChainTipClient;
import org.consensusj.bitcoin.rx.zeromq.RxBitcoinZmqService;

import javax.net.ssl.SSLSocketFactory;
import java.net.URI;

/**
 *
 */
public class RxOmniClient extends OmniClient implements RxJsonChainTipClient, OmniProxyMethods {
    ChainTipService chainTipService;
    private final boolean isOmniProxy;

    public RxOmniClient(NetworkParameters netParams, URI server, String rpcuser, String rpcpassword) {
        this(netParams, server, rpcuser, rpcpassword, true, false);
    }

    public RxOmniClient(NetworkParameters netParams, URI server, String rpcuser, String rpcpassword, boolean useZmq, boolean isOmniProxy) {
        this((SSLSocketFactory)SSLSocketFactory.getDefault(), netParams, server, rpcuser, rpcpassword, useZmq, isOmniProxy);
    }

    public RxOmniClient(SSLSocketFactory sslSocketFactory,  NetworkParameters netParams, URI server, String rpcuser, String rpcpassword, boolean useZmq, boolean isOmniProxy) {
        super(sslSocketFactory, netParams, server, rpcuser, rpcpassword);
        this.isOmniProxy = isOmniProxy;
        if (useZmq) {
            chainTipService = new RxBitcoinZmqService(this);
        } else {
            // TODO: Add ability to Set polling interval and/or enable/disable polling
            PollingChainTipServiceImpl pollingChainTipService = new PollingChainTipServiceImpl(this);
            pollingChainTipService.start();
            chainTipService = pollingChainTipService;
        }
    }

    @Override
    public Flowable<ChainTip> chainTipPublisher() {
        return Flowable.fromPublisher(chainTipService.chainTipPublisher());
    }

    @Override
    public boolean isOmniProxyServer() {
        return this.isOmniProxy;
    }
}