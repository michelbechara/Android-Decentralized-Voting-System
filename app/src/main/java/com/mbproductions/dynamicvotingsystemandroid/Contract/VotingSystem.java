package com.mbproductions.dynamicvotingsystemandroid.Contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.2.0.
 */
public class VotingSystem extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b5061060f806100206000396000f3006080604052600436106100825763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630121b93f811461008757806309e9d7e2146100a1578063298073d2146100b95780633477ee2e1461011657806366f5dd9e146101bc57806385209ce1146101e3578063a3ec138d14610245575b600080fd5b34801561009357600080fd5b5061009f600435610287565b005b3480156100ad57600080fd5b5061009f6004356102f4565b3480156100c557600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261009f94369492936024939284019190819084018382808284375094975050505091351515925061032b915050565b34801561012257600080fd5b5061012e6004356103ac565b60408051858152908101839052811515606082015260806020808301828152865192840192909252855160a084019187019080838360005b8381101561017e578181015183820152602001610166565b50505050905090810190601f1680156101ab5780820380516001836020036101000a031916815260200191505b509550505050505060405180910390f35b3480156101c857600080fd5b506101d1610462565b60408051918252519081900360200190f35b3480156101ef57600080fd5b5060408051602060046024803582810135601f810185900485028601850190965285855261009f958335953695604494919390910191908190840183828082843750949750505050913515159250610468915050565b34801561025157600080fd5b5061027373ffffffffffffffffffffffffffffffffffffffff600435166104ec565b604080519115158252519081900360200190f35b3360009081526020819052604090205460ff16156102a457600080fd5b6000811180156102b657506002548111155b15156102c157600080fd5b33600090815260208181526040808320805460ff1916600190811790915593835290839052902060020180549091019055565b60008181526001602081905260408220828155919061031590830182610501565b5060006002820155600301805460ff1916905550565b6002805460019081019182905560408051608081018252838152602080820187815260008385018190528715156060850152958652848252929094208151815591518051919492936103839390850192910190610548565b50604082015160028201556060909101516003909101805460ff19169115159190911790555050565b600160208181526000928352604092839020805481840180548651600296821615610100026000190190911695909504601f810185900485028601850190965285855290949193929091908301828280156104485780601f1061041d57610100808354040283529160200191610448565b820191906000526020600020905b81548152906001019060200180831161042b57829003601f168201915b50505050600283015460039093015491929160ff16905084565b60025481565b600083815260016020818152604080842060028101548251608081018452898152808501898152938101829052871515606082015295899052848452855182559151805192959491936104c19392850192910190610548565b50604082015160028201556060909101516003909101805460ff191691151591909117905550505050565b60006020819052908152604090205460ff1681565b50805460018160011615610100020316600290046000825580601f106105275750610545565b601f01602090049060005260206000209081019061054591906105c6565b50565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061058957805160ff19168380011785556105b6565b828001600101855582156105b6579182015b828111156105b657825182559160200191906001019061059b565b506105c29291506105c6565b5090565b6105e091905b808211156105c257600081556001016105cc565b905600a165627a7a72305820e6808adee695897696266aefe097415da4d419b6247efae1ec16d7c9411be3850029";

    public static final String FUNC_VOTE = "vote";

    public static final String FUNC_REMOVECANDIDATE = "removeCandidate";

    public static final String FUNC_ADDCANDIDATE = "addCandidate";

    public static final String FUNC_CANDIDATES = "candidates";

    public static final String FUNC_CANDIDATESNUMBER = "candidatesNumber";

    public static final String FUNC_MODIFYCANDIDATE = "modifyCandidate";

    public static final String FUNC_VOTERS = "voters";

    @Deprecated
    protected VotingSystem(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected VotingSystem(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected VotingSystem(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected VotingSystem(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> vote(BigInteger _candidateId) {
        final Function function = new Function(
                FUNC_VOTE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_candidateId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> removeCandidate(BigInteger id) {
        final Function function = new Function(
                FUNC_REMOVECANDIDATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(id)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> addCandidate(String candidate_name, Boolean isMale) {
        final Function function = new Function(
                FUNC_ADDCANDIDATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(candidate_name), 
                new org.web3j.abi.datatypes.Bool(isMale)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple4<BigInteger, String, BigInteger, Boolean>> candidates(BigInteger param0) {
        final Function function = new Function(FUNC_CANDIDATES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
        return new RemoteCall<Tuple4<BigInteger, String, BigInteger, Boolean>>(
                new Callable<Tuple4<BigInteger, String, BigInteger, Boolean>>() {
                    @Override
                    public Tuple4<BigInteger, String, BigInteger, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<BigInteger, String, BigInteger, Boolean>(
                                (BigInteger) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (Boolean) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<BigInteger> candidatesNumber() {
        final Function function = new Function(FUNC_CANDIDATESNUMBER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> modifyCandidate(BigInteger id, String candidate_name, Boolean isMale) {
        final Function function = new Function(
                FUNC_MODIFYCANDIDATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(id), 
                new org.web3j.abi.datatypes.Utf8String(candidate_name), 
                new org.web3j.abi.datatypes.Bool(isMale)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> voters(String param0) {
        final Function function = new Function(FUNC_VOTERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    @Deprecated
    public static VotingSystem load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new VotingSystem(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static VotingSystem load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new VotingSystem(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static VotingSystem load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new VotingSystem(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static VotingSystem load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new VotingSystem(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<VotingSystem> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(VotingSystem.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<VotingSystem> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(VotingSystem.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<VotingSystem> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(VotingSystem.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<VotingSystem> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(VotingSystem.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
