package com.mbproductions.dynamicvotingsystemandroid.utils;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.WalletUtils;
//import org.web3j.crypto.SecureRandomUtils;

import java.io.File;
import java.io.IOException;

//import static org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT;

public class myBip44utils extends WalletUtils {
        public static Bip39Wallet generateBip44Wallet(String password, File destinationDirectory)
                throws CipherException, IOException {
            return generateBip44Wallet(password, destinationDirectory, false);
        }

        public static Bip39Wallet generateBip44Wallet(String password, File destinationDirectory,
                                                      boolean testNet)
                throws CipherException, IOException {
            byte[] initialEntropy = new byte[16];
            //SecureRandomUtils.secureRandom().nextBytes(initialEntropy);

            String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
            byte[] seed = MnemonicUtils.generateSeed(mnemonic, null);

            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
            Bip32ECKeyPair bip44Keypair = generateBip44KeyPair(masterKeypair, testNet);

            String walletFile = WalletUtils.generateWalletFile(password, bip44Keypair, destinationDirectory, false);

            return new Bip39Wallet(walletFile, mnemonic);
        }

        public static Bip32ECKeyPair generateBip44KeyPair(Bip32ECKeyPair master) {
            return generateBip44KeyPair(master, false);
        }

        public static Bip32ECKeyPair generateBip44KeyPair(Bip32ECKeyPair master, boolean testNet) {
            if (testNet) {
                // /m/44'/0'/0
                final int[] path = {44 | 0x80000000, 0 | 0x80000000, 0 | 0x80000000, 0};
                return Bip32ECKeyPair.deriveKeyPair(master, path);
            } else {
                // m/44'/60'/0'/0
                final int[] path = {44 | 0x80000000, 60 | 0x80000000,0 | 0x80000000, 0| 0, 0};
                return Bip32ECKeyPair.deriveKeyPair(master, path);
            }
        }

        public static Credentials loadBip44Credentials(String password, String mnemonic) {
            return loadBip44Credentials(password, mnemonic, false);
        }

        public static Credentials loadBip44Credentials(String password, String mnemonic,
                                                       boolean testNet) {
            byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
            Bip32ECKeyPair bip44Keypair = generateBip44KeyPair(masterKeypair, testNet);
            return Credentials.create(bip44Keypair);
        }
    }

