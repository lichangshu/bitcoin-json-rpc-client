# bitcoin-json-rpc-client
java json-rpc for bitcoin client

java code copy from https://bitbucket.org/azazar/bitcoin-json-rpc-client/src/e77e4ac8070484c0c51fb7c9d81e0b39ce1789ce?at=default

Demo wiki https://en.bitcoin.it/wiki/Bitcoin-JSON-RPC-Client

bitcoin with docker for test https://hub.docker.com/r/freewil/bitcoin-testnet-box/

    ># docker run -t -i -p 19001:19001 -p 19011:19011 freewil/bitcoin-testnet-box -daemon  -txindex 
    
修改  Mackfile 的start ，
     bitcoind -datadir=1  -daemon -reindex -txindex
     bitcoind -datadir=1  -daemon -reindex -txindex


