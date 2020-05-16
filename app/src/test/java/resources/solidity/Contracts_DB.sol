pragma solidity ^0.4.0;

contract Contracts_DB { //Form the contract
    struct Contract{
      uint id;
      string contract_name;
      string contract_address;
    }

    mapping(uint => Contract) public contracts;

    uint public contractsNumber; //how many contracts there are

    constructor() public{ //constructor

    }

    function addContract (string contract_name, string c_address) public{
      contractsNumber++; //increment the number of contracts
      contracts[contractsNumber]=Contract(contractsNumber, contract_name, c_address);
    }

    function removeContract (uint id) public{
      delete contracts[id];
    }

}
