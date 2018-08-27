package com.xiong.wallet.keystoretool.utils;

import com.xiong.wallet.keystoretool.entity.WalletKeyPair;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import static org.web3j.crypto.Hash.sha256;


/**
 * Created by xionglh on 2018/8/20
 */
public class WalletUtils {

    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();
    private static final int PUBLIC_KEY_SIZE = 64;
    private static final int PUBLIC_KEY_LENGTH_IN_HEX = PUBLIC_KEY_SIZE << 1;
    private static ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    private static WalletKeyPair createWalletKeyPair() {
        WalletKeyPair walletKeyPair = new WalletKeyPair();
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(secureRandom, 128, "", creationTimeSeconds);
        ECKeyPair ecKeyPair = getECKeyPair(ds);
        BigInteger publicKey = ecKeyPair.getPublicKey();
        String publicKeyStr = Numeric.toHexStringWithPrefixZeroPadded(publicKey, PUBLIC_KEY_LENGTH_IN_HEX);
        BigInteger privateKey = ecKeyPair.getPrivateKey();
        String privateKeyStr = Numeric.toHexStringNoPrefixZeroPadded(privateKey, Keys.PRIVATE_KEY_LENGTH_IN_HEX);
        walletKeyPair.setPrivateKey(privateKeyStr);
        walletKeyPair.setPublicKey(publicKeyStr);
        return walletKeyPair;
    }

    private static ECKeyPair getECKeyPair(DeterministicSeed ds) {
        byte[] seedBytes = ds.getSeedBytes();
        return ECKeyPair.create(sha256(seedBytes));
    }


    public static String createWalletKeyPairStr() {
        WalletKeyPair walletKeyPair = createWalletKeyPair();
        return GJsonUtil.toJson(walletKeyPair, WalletKeyPair.class);
    }

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

}
