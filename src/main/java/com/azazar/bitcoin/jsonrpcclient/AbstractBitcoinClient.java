/*
 * Bitcoin-JSON-RPC-Client License
 * 
 * Copyright (c) 2013, Mikhail Yevchenko.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 
 * Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.azazar.bitcoin.jsonrpcclient;

import static com.azazar.bitcoin.jsonrpcclient.MapWrapper.mapCTime;
import static com.azazar.bitcoin.jsonrpcclient.MapWrapper.mapDouble;
import static com.azazar.bitcoin.jsonrpcclient.MapWrapper.mapInt;
import static com.azazar.bitcoin.jsonrpcclient.MapWrapper.mapStr;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mikhail Yevchenko <m.ṥῥẚɱ.ѓѐḿởύḙ@azazar.com>
 */
public abstract class AbstractBitcoinClient implements Bitcoin {

    public abstract Object query(String method, Object... o) throws BitcoinException;

    @Override
    public List<String> getRawMempool() throws BitcoinException {
        return (List<String>) query("getrawmempool", new String[] {});
    }

    public void addNode(String node, AddNoteCmd command) throws BitcoinException {
        query("addnode", node, command.toString());
    }

    public String createRawTransaction(List<TxInput> inputs, List<TxOutput> outputs) throws BitcoinException {
        List<Map> pInputs = new ArrayList<Map>();

        for (final TxInput txInput : inputs) {
            pInputs.add(new LinkedHashMap() {
                {
                    put("txid", txInput.txid());
                    put("vout", txInput.vout());
                }
            });
        }

        Map<String, Double> pOutputs = new LinkedHashMap();

        Double oldValue;
        for (TxOutput txOutput : outputs) {
            if ((oldValue = pOutputs.put(txOutput.address(), txOutput.amount())) != null)
                pOutputs.put(txOutput.address(), BitcoinUtil.normalizeAmount(oldValue.doubleValue() + txOutput.amount()));
            // throw new BitcoinException("Duplicate output");
        }

        return (String) query("createrawtransaction", pInputs, pOutputs);
    }

    public RawTransaction decodeRawTransaction(String hex) throws BitcoinException {
        return new RawTransactionImpl((Map) query("decoderawtransaction", hex));
    }

    public String dumpPrivKey(String address) throws BitcoinException {
        return (String) query("dumpprivkey", address);
    }

    public String getAccount(String address) throws BitcoinException {
        return (String) query("getaccount", address);
    }

    public String getAccountAddress(String account) throws BitcoinException {
        return (String) query("getaccountaddress", account);
    }

    public List<String> getAddressesByAccount(String account) throws BitcoinException {
        return (List<String>) query("getaddressesbyaccount", account);
    }

    public double getBalance() throws BitcoinException {
        return ((Number) query("getbalance")).doubleValue();
    }

    public double getBalance(String account) throws BitcoinException {
        return ((Number) query("getbalance", account)).doubleValue();
    }

    public double getBalance(String account, int minConf) throws BitcoinException {
        return ((Number) query("getbalance", account, minConf)).doubleValue();
    }

    private class BlockMapWrapper extends MapWrapper implements Block {

        public BlockMapWrapper(Map m) {
            super(m);
        }

        public String hash() {
            return mapStr("hash");
        }

        public int confirmations() {
            return mapInt("confirmations");
        }

        public int size() {
            return mapInt("size");
        }

        public int height() {
            return mapInt("height");
        }

        public int version() {
            return mapInt("version");
        }

        public String merkleRoot() {
            return mapStr("");
        }

        public List<String> tx() {
            return (List<String>) m.get("tx");
        }

        public Date time() {
            return mapCTime("time");
        }

        public long nonce() {
            return mapLong("nonce");
        }

        public String bits() {
            return mapStr("bits");
        }

        public double difficulty() {
            return mapDouble("difficulty");
        }

        public String previousHash() {
            return mapStr("previousblockhash");
        }

        public String nextHash() {
            return mapStr("nextblockhash");
        }

        public Block previous() throws BitcoinException {
            if (!m.containsKey("previousblockhash"))
                return null;
            return getBlock(previousHash());
        }

        public Block next() throws BitcoinException {
            if (!m.containsKey("nextblockhash"))
                return null;
            return getBlock(nextHash());
        }

    }

    public Block getBlock(String blockHash) throws BitcoinException {
        return new BlockMapWrapper((Map) query("getblock", blockHash));
    }

    public int getBlockCount() throws BitcoinException {
        return ((Number) query("getblockcount")).intValue();
    }

    public String getBlockHash(int blockId) throws BitcoinException {
        return (String) query("getblockhash", blockId);
    }

    public int getConnectionCount() throws BitcoinException {
        return ((Number) query("getconnectioncount")).intValue();
    }

    public double getDifficulty() throws BitcoinException {
        return ((Number) query("getdifficulty")).doubleValue();
    }

    public boolean getGenerate() throws BitcoinException {
        return (Boolean) query("getgenerate");
    }

    public double getHashesPerSec() throws BitcoinException {
        return ((Number) query("gethashespersec")).doubleValue();
    }

    private class InfoMapWrapper extends MapWrapper implements Info {

        public InfoMapWrapper(Map m) {
            super(m);
        }

        public int version() {
            return mapInt("version");
        }

        public int protocolversion() {
            return mapInt("protocolversion");
        }

        public int walletversion() {
            return mapInt("walletversion");
        }

        public double balance() {
            return mapDouble("balance");
        }

        public int blocks() {
            return mapInt("blocks");
        }

        public int timeoffset() {
            return mapInt("timeoffset");
        }

        public int connections() {
            return mapInt("connections");
        }

        public String proxy() {
            return mapStr("proxy");
        }

        public double difficulty() {
            return mapDouble("difficulty");
        }

        public boolean testnet() {
            return mapBool("testnet");
        }

        public int keypoololdest() {
            return mapInt("keypoololdest");
        }

        public int keypoolsize() {
            return mapInt("keypoolsize");
        }

        public int unlocked_until() {
            return mapInt("unlocked_until");
        }

        public double paytxfee() {
            return mapDouble("paytxfee");
        }

        public double relayfee() {
            return mapDouble("relayfee");
        }

        public String errors() {
            return mapStr("errors");
        }

    }

    public Info getInfo() throws BitcoinException {
        return new InfoMapWrapper((Map) query("getinfo"));
    }

    private class MiningInfoMapWrapper extends MapWrapper implements MiningInfo {

        public MiningInfoMapWrapper(Map m) {
            super(m);
        }

        public int blocks() {
            return mapInt("blocks");
        }

        public int currentblocksize() {
            return mapInt("currentblocksize");
        }

        public int currentblocktx() {
            return mapInt("currentblocktx");
        }

        public double difficulty() {
            return mapDouble("difficulty");
        }

        public String errors() {
            return mapStr("errors");
        }

        public int genproclimit() {
            return mapInt("genproclimit");
        }

        public double networkhashps() {
            return mapDouble("networkhashps");
        }

        public int pooledtx() {
            return mapInt("pooledtx");
        }

        public boolean testnet() {
            return mapBool("testnet");
        }

        public String chain() {
            return mapStr("chain");
        }

        public boolean generate() {
            return mapBool("generate");
        }

    }

    public MiningInfo getMiningInfo() throws BitcoinException {
        return new MiningInfoMapWrapper((Map) query("getmininginfo"));
    }

    public String getNewAddress() throws BitcoinException {
        return (String) query("getnewaddress");
    }

    public String getNewAddress(String account) throws BitcoinException {
        return (String) query("getnewaddress", account);
    }

    private class PeerInfoMapWrapper extends MapWrapper implements PeerInfo {

        public PeerInfoMapWrapper(Map m) {
            super(m);
        }

        public String addr() {
            return mapStr("addr");
        }

        public String services() {
            return mapStr("services");
        }

        public int lastsend() {
            return mapInt("lastsend");
        }

        public int lastrecv() {
            return mapInt("lastrecv");
        }

        public int bytessent() {
            return mapInt("bytessent");
        }

        public int bytesrecv() {
            return mapInt("bytesrecv");
        }

        public int blocksrequested() {
            return mapInt("blocksrequested");
        }

        public Date conntime() {
            return mapCTime("conntime");
        }

        public int version() {
            return mapInt("version");
        }

        public String subver() {
            return mapStr("subver");
        }

        public boolean inbound() {
            return mapBool("inbound");
        }

        public int startingheight() {
            return mapInt("startingheight");
        }

        public int banscore() {
            return mapInt("banscore");
        }

    }

    public PeerInfo getPeerInfo() throws BitcoinException {
        return new PeerInfoMapWrapper((Map) query("getmininginfo"));
    }

    public String getRawTransactionHex(String txId) throws BitcoinException {
        return (String) query("getrawtransaction", txId);
    }

    private class RawTransactionImpl extends MapWrapper implements RawTransaction {

        public RawTransactionImpl(Map<String, Object> tx) {
            super(tx);
        }

        public String hex() {
            return mapStr("hex");
        }

        public String txId() {
            return mapStr("txid");
        }

        public int version() {
            return mapInt("version");
        }

        public long lockTime() {
            return mapLong("locktime");
        }

        private class InImpl extends MapWrapper implements In {

            public InImpl(Map m) {
                super(m);
            }

            public String txid() {
                return mapStr("txid");
            }

            public int vout() {
                return mapInt("vout");
            }

            public Map<String, Object> scriptSig() {
                return (Map) m.get("scriptSig");
            }

            public long sequence() {
                return mapLong("sequence");
            }

            public RawTransaction getTransaction() {
                try {
                    return getRawTransaction(mapStr("txid"));
                } catch (BitcoinException ex) {
                    throw new RuntimeException(ex);
                }
            }

            public Out getTransactionOutput() {
                return getTransaction().vOut().get(mapInt("vout"));
            }

        }

        public List<In> vIn() {
            final List<Map<String, Object>> vIn = (List<Map<String, Object>>) m.get("vin");
            return new AbstractList<In>() {

                @Override
                public In get(int index) {
                    return new InImpl(vIn.get(index));
                }

                @Override
                public int size() {
                    return vIn.size();
                }
            };
        }

        private class OutImpl extends MapWrapper implements Out {

            public OutImpl(Map m) {
                super(m);
            }

            public double value() {
                return mapDouble("value");
            }

            public int n() {
                return mapInt("n");
            }

            private class ScriptPubKeyImpl extends MapWrapper implements ScriptPubKey {

                public ScriptPubKeyImpl(Map m) {
                    super(m);
                }

                public String asm() {
                    return mapStr("asm");
                }

                public String hex() {
                    return mapStr("hex");
                }

                public int reqSigs() {
                    return mapInt("reqSigs");
                }

                public String type() {
                    return mapStr(type());
                }

                public List<String> addresses() {
                    return (List) m.get("addresses");
                }

            }

            public ScriptPubKey scriptPubKey() {
                return new ScriptPubKeyImpl((Map) m.get("scriptPubKey"));
            }

            public TxInput toInput() {
                return new BasicTxInput(transaction().txId(), n());
            }

            public RawTransaction transaction() {
                return RawTransactionImpl.this;
            }

        }

        public List<Out> vOut() {
            final List<Map<String, Object>> vOut = (List<Map<String, Object>>) m.get("vout");
            return new AbstractList<Out>() {

                @Override
                public Out get(int index) {
                    return new OutImpl(vOut.get(index));
                }

                @Override
                public int size() {
                    return vOut.size();
                }
            };
        }

        public String blockHash() {
            return mapStr("blockhash");
        }

        public int confirmations() {
            return mapInt("confirmations");
        }

        public Date time() {
            return mapCTime("time");
        }

        public Date blocktime() {
            return mapCTime("blocktime");
        }

    }

    public RawTransaction getRawTransaction(String txId) throws BitcoinException {
        return new RawTransactionImpl((Map) query("getrawtransaction", txId, 1));
    }

    public double getReceivedByAccount(String account) throws BitcoinException {
        return ((Number) query("getreceivedbyaccount", account)).doubleValue();
    }

    public double getReceivedByAccount(String account, int minConf) throws BitcoinException {
        return ((Number) query("getreceivedbyaccount", account, minConf)).doubleValue();
    }

    public double getReceivedByAddress(String address) throws BitcoinException {
        return ((Number) query("getreceivedbyaddress", address)).doubleValue();
    }

    public double getReceivedByAddress(String address, int minConf) throws BitcoinException {
        return ((Number) query("getreceivedbyaddress", address, minConf)).doubleValue();
    }

    public RawTransaction getTransaction(String txId) throws BitcoinException {
        return this.getRawTransaction(txId);
    }

    public TxOutSetInfo getTxOutSetInfo() throws BitcoinException {
        final Map txoutsetinfoResult = (Map) query("gettxoutsetinfo");
        return new TxOutSetInfo() {

            public int height() {
                return ((Number) txoutsetinfoResult.get("height")).intValue();
            }

            public String bestBlock() {
                return (String) txoutsetinfoResult.get("bestblock");
            }

            public int transactions() {
                return ((Number) txoutsetinfoResult.get("transactions")).intValue();
            }

            public int txOuts() {
                return ((Number) txoutsetinfoResult.get("txouts")).intValue();
            }

            public int bytesSerialized() {
                return ((Number) txoutsetinfoResult.get("bytes_serialized")).intValue();
            }

            public String hashSerialized() {
                return (String) txoutsetinfoResult.get("hash_serialized");
            }

            public double totalAmount() {
                return ((Number) txoutsetinfoResult.get("total_amount")).doubleValue();
            }

            @Override
            public String toString() {
                return txoutsetinfoResult.toString();
            }

        };

    }

    public Work getWork() throws BitcoinException {
        final Map workResult = (Map) query("getwork");
        return new Work() {

            public String midstate() {
                return (String) workResult.get("midstate");
            }

            public String data() {
                return (String) workResult.get("data");
            }

            public String hash1() {
                return (String) workResult.get("hash1");
            }

            public String target() {
                return (String) workResult.get("target");
            }

            @Override
            public String toString() {
                return workResult.toString();
            }

        };

    }

    public void importPrivKey(String bitcoinPrivKey) throws BitcoinException {
        query("importprivkey", bitcoinPrivKey);
    }

    public void importPrivKey(String bitcoinPrivKey, String label) throws BitcoinException {
        query("importprivkey", bitcoinPrivKey, label);
    }

    public void importPrivKey(String bitcoinPrivKey, String label, boolean rescan) throws BitcoinException {
        query("importprivkey", bitcoinPrivKey, label, rescan);
    }

    public Map<String, Number> listAccounts() throws BitcoinException {
        return (Map) query("listaccounts");
    }

    public Map<String, Number> listAccounts(int minConf) throws BitcoinException {
        return (Map) query("listaccounts", minConf);
    }

    public List<ReceivedAddress> listReceivedByAccount() throws BitcoinException {
        return new ReceivedAddressListWrapper((List) query("listreceivedbyaccount"));
    }

    public List<ReceivedAddress> listReceivedByAccount(int minConf) throws BitcoinException {
        return new ReceivedAddressListWrapper((List) query("listreceivedbyaccount", minConf));
    }

    public List<ReceivedAddress> listReceivedByAccount(int minConf, boolean includeEmpty) throws BitcoinException {
        return new ReceivedAddressListWrapper((List) query("listreceivedbyaccount", minConf, includeEmpty));
    }

    private static class ReceivedAddressListWrapper extends AbstractList<ReceivedAddress> {
        private final List<Map<String, Object>> wrappedList;

        public ReceivedAddressListWrapper(List<Map<String, Object>> wrappedList) {
            this.wrappedList = wrappedList;
        }

        @Override
        public ReceivedAddress get(int index) {
            final Map<String, Object> e = wrappedList.get(index);
            return new ReceivedAddress() {

                public String address() {
                    return (String) e.get("address");
                }

                public String account() {
                    return (String) e.get("account");
                }

                public double amount() {
                    return ((Number) e.get("amount")).doubleValue();
                }

                public int confirmations() {
                    return ((Number) e.get("confirmations")).intValue();
                }

                @Override
                public String toString() {
                    return e.toString();
                }

            };
        }

        @Override
        public int size() {
            return wrappedList.size();
        }
    }

    public List<ReceivedAddress> listReceivedByAddress() throws BitcoinException {
        return new ReceivedAddressListWrapper((List) query("listreceivedbyaddress"));
    }

    public List<ReceivedAddress> listReceivedByAddress(int minConf) throws BitcoinException {
        return new ReceivedAddressListWrapper((List) query("listreceivedbyaddress", minConf));
    }

    public List<ReceivedAddress> listReceivedByAddress(int minConf, boolean includeEmpty) throws BitcoinException {
        return new ReceivedAddressListWrapper((List) query("listreceivedbyaddress", minConf, includeEmpty));
    }

    private class TransactionListMapWrapper extends ListMapWrapper<Transaction> {

        public TransactionListMapWrapper(List<Map> list) {
            super(list);
        }

        @Override
        protected Transaction wrap(final Map m) {
            return new Transaction() {

                public String account() {
                    return mapStr(m, "account");
                }

                public String address() {
                    return mapStr(m, "address");
                }

                public String category() {
                    return mapStr(m, "category");
                }

                public double amount() {
                    return mapDouble(m, "amount");
                }

                public double fee() {
                    return mapDouble(m, "fee");
                }

                public int confirmations() {
                    return mapInt(m, "confirmations");
                }

                public String blockHash() {
                    return mapStr(m, "blockhash");
                }

                public int blockIndex() {
                    return mapInt(m, "blockindex");
                }

                public Date blockTime() {
                    return mapCTime(m, "blocktime");
                }

                public String txId() {
                    return mapStr(m, "txid");
                }

                public Date time() {
                    return mapCTime(m, "time");
                }

                public Date timeReceived() {
                    return mapCTime(m, "timereceived");
                }

                public String comment() {
                    return mapStr(m, "comment");
                }

                public String commentTo() {
                    return mapStr(m, "to");
                }

                private RawTransaction raw = null;

                public RawTransaction raw() {
                    if (raw == null)
                        try {
                            raw = getRawTransaction(txId());
                        } catch (BitcoinException ex) {
                            throw new RuntimeException(ex);
                        }
                    return raw;
                }

                @Override
                public String toString() {
                    return m.toString();
                }

            };
        }

    }

    private class TransactionsSinceBlockImpl implements TransactionsSinceBlock {

        public final List<Transaction> transactions;
        public final String lastBlock;

        public TransactionsSinceBlockImpl(Map r) {
            this.transactions = new TransactionListMapWrapper((List) r.get("transactions"));
            this.lastBlock = (String) r.get("lastblock");
        }

        public List<Transaction> transactions() {
            return transactions;
        }

        public String lastBlock() {
            return lastBlock;
        }

    }

    public TransactionsSinceBlock listSinceBlock() throws BitcoinException {
        return new TransactionsSinceBlockImpl((Map) query("listsinceblock"));
    }

    public TransactionsSinceBlock listSinceBlock(String blockHash) throws BitcoinException {
        return new TransactionsSinceBlockImpl((Map) query("listsinceblock", blockHash));
    }

    public TransactionsSinceBlock listSinceBlock(String blockHash, int targetConfirmations) throws BitcoinException {
        return new TransactionsSinceBlockImpl((Map) query("listsinceblock", blockHash, targetConfirmations));
    }

    public List<Transaction> listTransactions() throws BitcoinException {
        return new TransactionListMapWrapper((List) query("listtransactions"));
    }

    public List<Transaction> listTransactions(String account) throws BitcoinException {
        return new TransactionListMapWrapper((List) query("listtransactions", account));
    }

    public List<Transaction> listTransactions(String account, int count) throws BitcoinException {
        return new TransactionListMapWrapper((List) query("listtransactions", account, count));
    }

    public List<Transaction> listTransactions(String account, int count, int from) throws BitcoinException {
        return new TransactionListMapWrapper((List) query("listtransactions", account, count, from));
    }

    private class UnspentListWrapper extends ListMapWrapper<Unspent> {

        public UnspentListWrapper(List<Map> list) {
            super(list);
        }

        @Override
        protected Unspent wrap(final Map m) {
            return new Unspent() {

                public String txid() {
                    return mapStr(m, "txid");
                }

                public int vout() {
                    return mapInt(m, "vout");
                }

                public String address() {
                    return mapStr(m, "address");
                }

                public String scriptPubKey() {
                    return mapStr(m, "scriptPubKey");
                }

                public String account() {
                    return mapStr(m, "account");
                }

                public double amount() {
                    return mapDouble(m, "amount");
                }

                public int confirmations() {
                    return mapInt(m, "confirmations");
                }

            };
        }
    }

    public List<Unspent> listUnspent() throws BitcoinException {
        return new UnspentListWrapper((List) query("listunspent"));
    }

    public List<Unspent> listUnspent(int minConf) throws BitcoinException {
        return new UnspentListWrapper((List) query("listunspent", minConf));
    }

    public List<Unspent> listUnspent(int minConf, int maxConf) throws BitcoinException {
        return new UnspentListWrapper((List) query("listunspent", minConf, maxConf));
    }

    public List<Unspent> listUnspent(int minConf, int maxConf, String... addresses) throws BitcoinException {
        return new UnspentListWrapper((List) query("listunspent", minConf, maxConf, addresses));
    }

    public String sendFrom(String fromAccount, String toBitcoinAddress, double amount) throws BitcoinException {
        return (String) query("sendfrom", fromAccount, toBitcoinAddress, amount);
    }

    public String sendFrom(String fromAccount, String toBitcoinAddress, double amount, int minConf) throws BitcoinException {
        return (String) query("sendfrom", fromAccount, toBitcoinAddress, amount, minConf);
    }

    public String sendFrom(String fromAccount, String toBitcoinAddress, double amount, int minConf, String comment) throws BitcoinException {
        return (String) query("sendfrom", fromAccount, toBitcoinAddress, amount, minConf, comment);
    }

    public String sendFrom(String fromAccount, String toBitcoinAddress, double amount, int minConf, String comment, String commentTo) throws BitcoinException {
        return (String) query("sendfrom", fromAccount, toBitcoinAddress, amount, minConf, comment, commentTo);
    }

    public String sendMany(String fromAccount, List<TxOutput> outputs) throws BitcoinException {
        Map<String, Double> pOutputs = new LinkedHashMap();

        Double oldValue;
        for (TxOutput txOutput : outputs) {
            if ((oldValue = pOutputs.put(txOutput.address(), txOutput.amount())) != null)
                pOutputs.put(txOutput.address(), BitcoinUtil.normalizeAmount(oldValue.doubleValue() + txOutput.amount()));
            // throw new BitcoinException("Duplicate output");
        }
        return (String) query("sendmany", fromAccount, pOutputs);
    }

    public String sendMany(String fromAccount, List<TxOutput> outputs, int minConf) throws BitcoinException {
        Map<String, Double> pOutputs = new LinkedHashMap();

        Double oldValue;
        for (TxOutput txOutput : outputs) {
            if ((oldValue = pOutputs.put(txOutput.address(), txOutput.amount())) != null)
                pOutputs.put(txOutput.address(), BitcoinUtil.normalizeAmount(oldValue.doubleValue() + txOutput.amount()));
            // throw new BitcoinException("Duplicate output");
        }
        return (String) query("sendmany", fromAccount, pOutputs, minConf);
    }

    public String sendMany(String fromAccount, List<TxOutput> outputs, int minConf, String comment) throws BitcoinException {
        Map<String, Double> pOutputs = new LinkedHashMap();

        Double oldValue;
        for (TxOutput txOutput : outputs) {
            if ((oldValue = pOutputs.put(txOutput.address(), txOutput.amount())) != null)
                pOutputs.put(txOutput.address(), BitcoinUtil.normalizeAmount(oldValue.doubleValue() + txOutput.amount()));
            // throw new BitcoinException("Duplicate output");
        }
        return (String) query("sendmany", fromAccount, pOutputs, minConf, comment);
    }

    public String sendRawTransaction(String hex) throws BitcoinException {
        return (String) query("sendrawtransaction", hex);
    }

    public String sendToAddress(String toAddress, double amount) throws BitcoinException {
        return (String) query("sendtoaddress", toAddress, amount);
    }

    public String sendToAddress(String toAddress, double amount, String comment) throws BitcoinException {
        return (String) query("sendtoaddress", toAddress, amount, comment);
    }

    public String sendToAddress(String toAddress, double amount, String comment, String commentTo) throws BitcoinException {
        return (String) query("sendtoaddress", toAddress, amount, comment, commentTo);
    }

    public String signMessage(String address, String message) throws BitcoinException {
        return (String) query("signmessage", address, message);
    }

    public String signRawTransaction(String hex) throws BitcoinException {
        Map result = (Map) query("signrawtransaction", hex);

        if ((Boolean) result.get("complete"))
            return (String) result.get("hex");
        else
            throw new BitcoinException("Incomplete");
    }

    public void stop() throws BitcoinException {
        query("stop");
    }

    public AddressValidationResult validateAddress(String address) throws BitcoinException {
        final Map validationResult = (Map) query("validateaddress", address);
        return new AddressValidationResult() {

            public boolean isValid() {
                return ((Boolean) validationResult.get("isvalid"));
            }

            public String address() {
                return (String) validationResult.get("address");
            }

            public boolean isMine() {
                return ((Boolean) validationResult.get("ismine"));
            }

            public boolean isScript() {
                return ((Boolean) validationResult.get("isscript"));
            }

            public String pubKey() {
                return (String) validationResult.get("pubkey");
            }

            public boolean isCompressed() {
                return ((Boolean) validationResult.get("iscompressed"));
            }

            public String account() {
                return (String) validationResult.get("account");
            }

            @Override
            public String toString() {
                return validationResult.toString();
            }

        };
    }

    public boolean verifyMessage(String address, String signature, String message) throws BitcoinException {
        return (Boolean) query("verifymessage", address, signature, message);
    }

    // static {
    // logger.setLevel(Level.ALL);
    // for (Handler handler : logger.getParent().getHandlers())
    // handler.setLevel(Level.ALL);
    // }

    // public static void donate() throws Exception {
    // Bitcoin btc = new BitcoinJSONRPCClient();
    // if (btc.getBalance() > 10)
    // btc.sendToAddress("1AZaZarEn4DPEx5LDhfeghudiPoHhybTEr", 10);
    // }

    // public static void main(String[] args) throws Exception {
    // BitcoinJSONRPCClient b = new BitcoinJSONRPCClient(true);
    //
    // System.out.println(b.listTransactions());
    //
    //// String aa = "mjrxsupqJGBzeMjEiv57qxSKxgd3SVwZYd";
    //// String ab = "mpN3WTJYsrnnWeoMzwTxkp8325nzArxnxN";
    //// String ac = b.getNewAddress("TEST");
    ////
    //// System.out.println(b.getBalance("", 0));
    //// System.out.println(b.sendFrom("", ab, 0.1));
    //// System.out.println(b.sendToAddress(ab, 0.1, "comment", "tocomment"));
    //// System.out.println(b.getReceivedByAddress(ab));
    //// System.out.println(b.sendToAddress(ac, 0.01));
    ////
    //// System.out.println(b.validateAddress(ac));
    ////
    ////// b.importPrivKey(b.dumpPrivKey(aa));
    ////
    //// System.out.println(b.getAddressesByAccount("TEST"));
    //// System.out.println(b.listReceivedByAddress());
    // }

}
