package com.mbproductions.dynamicvotingsystemandroid.Contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
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
public class Contracts_DB extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610525806100206000396000f3006080604052600436106100615763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041663474da79a811461006657806349061757146101635780634d5a50f6146101fc5780637cca3b0614610223575b600080fd5b34801561007257600080fd5b5061007e60043561023b565b604051808481526020018060200180602001838103835285818151815260200191508051906020019080838360005b838110156100c55781810151838201526020016100ad565b50505050905090810190601f1680156100f25780820380516001836020036101000a031916815260200191505b50838103825284518152845160209182019186019080838360005b8381101561012557818101518382015260200161010d565b50505050905090810190601f1680156101525780820380516001836020036101000a031916815260200191505b509550505050505060405180910390f35b34801561016f57600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526101fa94369492936024939284019190819084018382808284375050604080516020601f89358b018035918201839004830284018301909452808352979a9998810197919650918201945092508291508401838280828437509497506103719650505050505050565b005b34801561020857600080fd5b506102116103df565b60408051918252519081900360200190f35b34801561022f57600080fd5b506101fa6004356103e5565b600060208181529181526040908190208054600180830180548551600293821615610100026000190190911692909204601f81018790048702830187019095528482529194929390928301828280156102d55780601f106102aa576101008083540402835291602001916102d5565b820191906000526020600020905b8154815290600101906020018083116102b857829003601f168201915b50505060028085018054604080516020601f60001961010060018716150201909416959095049283018590048502810185019091528181529596959450909250908301828280156103675780601f1061033c57610100808354040283529160200191610367565b820191906000526020600020905b81548152906001019060200180831161034a57829003601f168201915b5050505050905083565b6001805481018082556040805160608101825282815260208082018781528284018790526000948552848252929093208151815591518051919492936103bc93850192910190610417565b50604082015180516103d8916002840191602090910190610417565b5050505050565b60015481565b6000818152602081905260408120818155906104046001830182610495565b610412600283016000610495565b505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061045857805160ff1916838001178555610485565b82800160010185558215610485579182015b8281111561048557825182559160200191906001019061046a565b506104919291506104dc565b5090565b50805460018160011615610100020316600290046000825580601f106104bb57506104d9565b601f0160209004906000526020600020908101906104d991906104dc565b50565b6104f691905b8082111561049157600081556001016104e2565b905600a165627a7a723058208a828ea73168c8cb9ef92330db2494b1c7844ecf51bc45fd8ffbec25f28d1cce0029";

    public static final String FUNC_CONTRACTS = "contracts";

    public static final String FUNC_ADDCONTRACT = "addContract";

    public static final String FUNC_CONTRACTSNUMBER = "contractsNumber";

    public static final String FUNC_REMOVECONTRACT = "removeContract";

    @Deprecated
    protected Contracts_DB(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Contracts_DB(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Contracts_DB(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Contracts_DB(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<Tuple3<BigInteger, String, String>> contracts(BigInteger param0) {
        final Function function = new Function(FUNC_CONTRACTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        return new RemoteCall<Tuple3<BigInteger, String, String>>(
                new Callable<Tuple3<BigInteger, String, String>>() {
                    @Override
                    public Tuple3<BigInteger, String, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<BigInteger, String, String>(
                                (BigInteger) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> addContract(String contract_name, String c_address) {
        final Function function = new Function(
                FUNC_ADDCONTRACT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(contract_name), 
                new org.web3j.abi.datatypes.Utf8String(c_address)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> contractsNumber() {
        final Function function = new Function(FUNC_CONTRACTSNUMBER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> removeContract(BigInteger id) {
        final Function function = new Function(
                FUNC_REMOVECONTRACT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(id)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static Contracts_DB load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Contracts_DB(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Contracts_DB load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Contracts_DB(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Contracts_DB load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Contracts_DB(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Contracts_DB load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Contracts_DB(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Contracts_DB> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Contracts_DB.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<Contracts_DB> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Contracts_DB.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Contracts_DB> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Contracts_DB.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Contracts_DB> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Contracts_DB.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
