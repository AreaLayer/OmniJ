package foundation.omni.test;

import org.consensusj.bitcoin.jsonrpc.test.FundingSource;
import foundation.omni.OmniDivisibleValue;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;

/**
 *
 */
public interface OmniFundingSource extends FundingSource {
// Not Yet: need to move methods from OmniTestSupport to RegTestOmniFundingSource
// For now Groovy code can extend RegTestOmniFundingSource and implement the OmniTestSupport interface
//    Address createFundedAddress(Coin bitcoinAmount, OmniDivisibleValue omniAmount);
}
