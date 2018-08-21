# WalletKeyStoreTool
Ethereum Secret Key Tool</br>
Create public and private key and produce Keystore, decrypt the private key according to the password and Keystore.

### WalletKeyStoreTool
* Create public and private key
```Java
public static String createWalletKeyPairStr() {
        WalletKeyPair walletKeyPair = createWalletKeyPair();
        return GJsonUtil.toJson(walletKeyPair, WalletKeyPair.class);
    }
```
* Create keystore

```Java
  public static String createKeyStore(String pwd, String privateKey) {
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
        WalletFile walletFile;
        try {
            walletFile = Wallet.create(pwd, ecKeyPair, 1024, 1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return GJsonUtil.toJson(walletFile, WalletFile.class);
    }
```
* Decrypt keystore's private key
```Java
    public static String recoverKeyStore(String pwd, String keystore) {
        Credentials credentials;
        ECKeyPair keypair;
        String privateKey = null;
        try {
            WalletFile walletFile = objectMapper.readValue(keystore, WalletFile.class);
            credentials = Credentials.create(Wallet.decrypt(pwd, walletFile));
            keypair = credentials.getEcKeyPair();
            privateKey = Numeric.toHexStringNoPrefixZeroPadded(keypair.getPrivateKey(), Keys.PRIVATE_KEY_LENGTH_IN_HEX);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }
        return privateKey;
    }
```
