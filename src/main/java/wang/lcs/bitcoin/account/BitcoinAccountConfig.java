package wang.lcs.bitcoin.account;

public interface BitcoinAccountConfig {
	byte getAddressHeader();

	byte getDumpedPrivateKeyHeader();

	public static final BitcoinAccountConfig mainConfig = new BitcoinAccountConfig() {
		public byte getAddressHeader() {
			return 0;
		}

		public byte getDumpedPrivateKeyHeader() {
			return (byte) 128;
		}
	};

	public static final BitcoinAccountConfig testConfig = new BitcoinAccountConfig() {
		public byte getAddressHeader() {
			return 111;
		}

		public byte getDumpedPrivateKeyHeader() {
			return (byte) 239;
		}
	};
}
