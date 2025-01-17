package foundation.omni.test.rpc.smartproperty

import foundation.omni.OmniDivisibleValue
import foundation.omni.OmniIndivisibleValue
import org.bitcoinj.core.Address
import foundation.omni.BaseRegTestSpec
import foundation.omni.CurrencyID
import foundation.omni.Ecosystem
import org.bitcoinj.core.Coin
import spock.lang.Shared

/*
  Spreadsheet with test transactions for Smart Property:
  https://docs.google.com/spreadsheet/ccc?key=0Al4FhV693WqWdGNFRDhEMTFtaWNmcVNObFlVQmNOa1E&usp=drive_web#gid=0
  Curtis may have sent some of these into the BlockChain
 */

class SmartPropertySpec extends BaseRegTestSpec {

    final static Coin startBTC = 1.0.btc
    final static OmniDivisibleValue startMSC = 50.0.divisible

    @Shared
    Address fundedAddress

    def setup() {
        fundedAddress = createFundedAddress(startBTC, startMSC)
    }

    def "Create smart property with divisible units"() {
        when: "we transmit a property creation transaction in main ecosystem"
        def txid = createProperty(fundedAddress, Ecosystem.OMNI, OmniDivisibleValue.ofWilletts(314159265L))

        and: "a new block is mined"
        client.generateBlocks(1)

        then: "the transaction confirms"
        def transaction = client.omniGetTransaction(txid)
        transaction.confirmations == 1

        and: "it should be valid"
        transaction.isValid()

        and: "is in the main ecosystem"
        def propertyId = transaction.propertyId
        propertyId.ecosystem == Ecosystem.OMNI

        and: "has 3.14159265 divisible units"
        transaction.divisible
        transaction.amount == 3.14159265

        and: "this amount was credited to the issuer"
        def available = omniGetBalance(fundedAddress, propertyId)
        available.balance == 3.14159265
    }

    def "Create test property with indivisible units"() {
        when: "we transmit a property creation transaction in test ecosystem"
        def txid = createProperty(fundedAddress, Ecosystem.TOMNI, OmniIndivisibleValue.of(4815162342L))

        and: "a new block is mined"
        client.generateBlocks(1)

        then: "the transaction confirms"
        def transaction = client.omniGetTransaction(txid)
        transaction.confirmations == 1

        and: "it should be valid"
        transaction.valid

        and: "is in the test ecosystem"
        def propertyId = transaction.propertyId
        propertyId.ecosystem == Ecosystem.TOMNI

        and: "has 4815162342 indivisible units"
        !transaction.divisible
        transaction.amount == 4815162342

        and: "this amount was credited to the issuer"
        def available = omniGetBalance(fundedAddress, propertyId)
        available.balance.toLong() == 4815162342L
    }

}
